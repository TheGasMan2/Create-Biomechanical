package com.happysg.biomechanical.world.entity;

import com.happysg.biomechanical.content.cogolem.CogolemAI;
import com.happysg.biomechanical.content.cogolem.CogolemAnimation;
import com.happysg.biomechanical.content.cogolem.GolemCommand;
import com.happysg.biomechanical.content.tuner.ITunerOverlay;
import com.simibubi.create.AllItems;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NonnullDefault
public class Cogolem extends PathfinderMob implements GeoEntity, SmartBrainOwner<Cogolem>, ITunerOverlay, OwnableEntity, VariantHolder<Cogolem.Type> {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> CHARGE_LEVEL = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(Cogolem.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    /**
     * Replace it with an attribute
     * */
    @Deprecated
    private static final float MAX_CHARGE_LEVEL = 100;

    /* INIT */
    public Cogolem(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE,32)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_TYPE_ID, 0);
        builder.define(CHARGE_LEVEL, MAX_ENCHANTED_ARMOR_CHANCE);
        builder.define(COMMAND, GolemCommand.STAY.ordinal());
    }

    /* COMMON GETTERS AND SETTERS */
    public Type getVariant() {
        return Type.values()[this.entityData.get(DATA_TYPE_ID)];
    }

    public void setVariant(Type variant) {
        this.entityData.set(DATA_TYPE_ID, variant.ordinal());
    }

    public float getChargeLevel() {
        return this.entityData.get(CHARGE_LEVEL);
    }

    public void setChargeLevel(float chargeLevel) {
        this.entityData.set(CHARGE_LEVEL, chargeLevel);
    }

    public GolemCommand getCommand() {
        return GolemCommand.values()[this.entityData.get(COMMAND)];
    }

    public void setCommand(GolemCommand command) {
        this.entityData.set(COMMAND, command.ordinal());
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public float extractCharge(float amount) {
        if(amount < 0) return insertCharge(-amount);
        float charge = this.getChargeLevel();
        if(amount > charge) {
            setChargeLevel(0);
            return amount - charge;
        }
        setChargeLevel(charge - amount);
        return amount;
    }

    public float insertCharge(float amount) {
        if(amount < 0) return extractCharge(-amount);
        float charge = this.getChargeLevel();
        if(charge + amount > MAX_CHARGE_LEVEL) {
            setChargeLevel(MAX_CHARGE_LEVEL);
            return charge + amount - MAX_CHARGE_LEVEL;
        }
        setChargeLevel(charge + amount);
        return amount;
    }

    /* NBT DATA */
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setChargeLevel(tag.getFloat("ChargeLevel"));
        int command = tag.getInt("Command");
        if(command >= 0 && command < GolemCommand.values().length)
            this.setCommand(GolemCommand.values()[command]);
        this.setOwnerUUID(tag.hasUUID("Owner") ? tag.getUUID("Owner") : null);
        int variant = tag.getInt("Variant");
        if(variant > 0 && variant < Type.values().length)
            this.setVariant(Type.values()[variant]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("ChargeLevel", this.getChargeLevel());
        tag.putInt("Command", this.getCommand().ordinal());
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
        if(variant == null) return super.interactAt(player, vec, hand);
        if(getVariant() == variant) return InteractionResult.CONSUME;
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
        return InteractionResult.SUCCESS;
    }

    /* TICK */
    @Override
    public void tick() {
        super.tick();
        double motion = Math.abs(getDeltaMovement().x + getDeltaMovement().z)*.1;
        extractCharge((float) motion);
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
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundNavigation(this, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends Cogolem>> getSensors() {
        return CogolemAI.getSensors();
    }

    @Override
    public BrainActivityGroup<? extends Cogolem> getCoreTasks() {
        return CogolemAI.getCoreTasks();
    }

    @Override
    public BrainActivityGroup<? extends Cogolem> getIdleTasks() {
        return CogolemAI.getIdleTasks();
    }

    @Override
    public BrainActivityGroup<? extends Cogolem> getFightTasks() {
        return CogolemAI.getFightTasks();
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.tickBrain(this);
    }

    /* GOOGLE */

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    Cogolem"));
        tooltip.add(Component.literal("Health  ")
                .append(barComponent((int) (getHealth() * 20 / getMaxHealth()))));
        tooltip.add(Component.literal("Charge ")
                .append(barComponent((int) (getChargeLevel() * 20/ MAX_CHARGE_LEVEL))));
        return true;
    }

    @Deprecated
    private MutableComponent barComponent(int level) {
        return Component.empty()
                .append(bars(Math.max(0, level), ChatFormatting.GREEN))
                .append(bars(Math.max(0, 20 - level), ChatFormatting.DARK_RED));
    }

    @Deprecated
    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
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
