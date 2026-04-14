package com.happysg.biomechanical.world.entity;

import com.happysg.biomechanical.registry.BMAttributes;
import com.happysg.biomechanical.world.inventory.CogolemMenu;
import com.simibubi.create.AllItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;


import java.util.Optional;
import java.util.UUID;

@NonnullDefault
public class Cogolem extends PathfinderMob implements GeoEntity, OwnableEntity, VariantHolder<Cogolem.Type>, MenuProvider, ContainerListener {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> CHARGE_ID = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.FLOAT);

    public static final int SLOT_MAIN_HAND = 0;
    public static final int SLOT_CHEST = 1;
    public static final int SLOT_BODY = 2;

    @Nullable
    private SimpleContainer inventory = null;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /* INIT */
    public Cogolem(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        createInventory();
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = Math.min(simplecontainer.getContainerSize(), this.inventory.getContainerSize());

            for (int j = 0; j < i; j++) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
    }

    public int getInventorySize() {
        return 3;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE,32)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(BMAttributes.MAX_CHARGE, 200);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_TYPE_ID, 0);
        builder.define(CHARGE_ID, 1f);
    }

    @Override
    public Brain<?> getBrain() {
        return super.getBrain();
    }

    /* COMMON GETTERS AND SETTERS */
    public Type getVariant() {
        return Type.values()[this.entityData.get(DATA_TYPE_ID)];
    }
    public void setVariant(Type variant) {
        this.entityData.set(DATA_TYPE_ID, variant.ordinal());
    }

    public float getChargeLevel() {
        return this.entityData.get(CHARGE_ID);
    }
    public void setChargeLevel(float chargeLevel) {
        this.entityData.set(CHARGE_ID, chargeLevel);
    }
    public float extractCharge(float toExtract, boolean simulate) {
        if(toExtract < 0) return insertCharge(-toExtract, simulate); //Or we can throw an exception
        float removed = getChargeLevel() - toExtract;
        if(!simulate) setChargeLevel(Math.max(0, removed));
        return removed > 0 ? 0 : -removed;
    }
    public float insertCharge(float toInsert, boolean simulate) {
        if(toInsert < 0) return extractCharge(-toInsert, simulate); //Or we can throw an exception
        float added = getChargeLevel() + toInsert;
        double max = getAttributeValue(BMAttributes.MAX_CHARGE);
        if(!simulate) setChargeLevel((float) Math.min(max, added));
        return added > max ? (float)(added-max) : 0;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }
    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }


    /* NBT DATA */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setChargeLevel((float) Mth.clamp(tag.getFloat("ChargeLevel"), 0, getAttributeValue(BMAttributes.MAX_CHARGE)));
        this.setOwnerUUID(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
        int variant = tag.getInt("Variant");
        if(variant > 0 && variant < Type.values().length)
            this.setVariant(Type.values()[variant]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("ChargeLevel", this.getChargeLevel());
        if(this.getOwnerUUID() != null) tag.putUUID("Owner", this.getOwnerUUID());
        tag.putInt("Variant", this.getVariant().ordinal());
    }

    /* GECKO */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final RawAnimation anim = RawAnimation.begin().thenLoop("test");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Test", 5, this::predicate));
    }
    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return state.setAndContinue(anim);
    }

    /* INTERACT */
    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if(level().isClientSide) return super.interactAt(player, vec, hand);
        var stack = player.getItemInHand(hand);
        Type variant = Type.parse(stack);
        if(variant != null && getVariant() != variant) {
            setVariant(variant);
            float pitch = 0.9F + level().random.nextFloat() * 0.2F;
            level().playSound(
                    null,
                    getX(), getY(), getZ(),
                    SoundEvents.ANVIL_LAND,
                    SoundSource.NEUTRAL,
                    1.0F,
                    pitch
            );
            stack.consume(1, player);
            return InteractionResult.CONSUME;
        }
        player.openMenu(this, buf -> buf.writeInt(getId()));
        return InteractionResult.SUCCESS;

    }

    /* TICK */
    @Override
    public void tick() {
        super.tick();
        double motion = Math.abs(getDeltaMovement().x + getDeltaMovement().z)*.1;
        extractCharge((float) motion, true);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        this.deathTime++;
        if (this.deathTime >= 30 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    /* PROPERTIES */
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 20;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    /* AI */
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothGroundNavigation(this, level);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CogolemMenu(containerId, playerInventory, this);
    }

    public final Container getArmor() {
        //noinspection DataFlowIssue
        return this.inventory;
    }

    @Override
    public void containerChanged(Container container) {

    }

    /* VARIANT */
    public enum Type {
        ANDESITE, ALLOYED, BRASS;

        @Nullable
        public static Type parse(ItemStack itemStack) {
            return parse(itemStack.getItem());
        }

        @Nullable
        public static Type parse(Item item) {
            if(item == Items.ANDESITE) return ANDESITE;
            if(item == AllItems.BRASS_INGOT.get()) return BRASS;
            if(item == AllItems.ANDESITE_ALLOY.get()) return ALLOYED;
            return null;
        }
    }

}
