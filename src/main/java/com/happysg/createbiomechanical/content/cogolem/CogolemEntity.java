package com.happysg.createbiomechanical.content.cogolem;

import com.happysg.createbiomechanical.content.tuner.ITunerOverlay;
import com.simibubi.create.AllBlocks;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CogolemEntity extends PathfinderMob implements GeoEntity, SmartBrainOwner<CogolemEntity>, ITunerOverlay, OwnableEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final float MAX_CHARGE_LEVEL = 100;

    private static final EntityDataAccessor<Float> CHARGE_LEVEL = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> TEXTURE_STATE =
            SynchedEntityData.defineId(CogolemEntity.class, EntityDataSerializers.INT);


    public CogolemEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new SmoothGroundNavigation(this, level);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void tick() {
        super.tick();
        double motion =Math.abs(getDeltaMovement().x + getDeltaMovement().z)*.1;
        takeCharge((float) motion);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
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

    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 30 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected float getMaxHeadRotationRelativeToBody() {
        return super.getMaxHeadRotationRelativeToBody();
    }

    @Override
    public int getMaxHeadXRot() {
        return 20;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setChargeLevel(MAX_CHARGE_LEVEL);
        this.setCommand(GolemCommands.WANDER);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public float getChargeLevel() {
        return this.entityData.get(CHARGE_LEVEL);
    }

    public void setChargeLevel(float chargeLevel) {
        if(chargeLevel < 0)
            return;
        this.entityData.set(CHARGE_LEVEL, Math.min(chargeLevel, MAX_CHARGE_LEVEL));
    }

    public boolean takeCharge(float amount) {
        if(amount < 0)
            return false;
        float newCharge = this.getChargeLevel() - amount;
        if(newCharge < 0) {
            newCharge = 0;
        }
        this.setChargeLevel(newCharge);
        return true;
    }

    public GolemCommands getCommand() {
        return GolemCommands.values()[this.entityData.get(COMMAND)];
    }

    public void setCommand(GolemCommands command) {
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

    public void setOwner(Player player) {
        if (player != null) {
            this.setOwnerUUID(player.getUUID());
        } else {
            this.setOwnerUUID(null);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE_LEVEL, 0f);
        builder.define(COMMAND, GolemCommands.STAY.ordinal());
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(TEXTURE_STATE, 0);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setChargeLevel(compound.getFloat("ChargeLevel"));
        this.setCommand(GolemCommands.values()[compound.getInt("Command")]);
        if (compound.hasUUID("OwnerUUID")) {
            this.setOwnerUUID(compound.getUUID("OwnerUUID"));
        } else {
            this.setOwnerUUID(null);
        }
        if (compound.contains("TextureState")) {
            setTextureState(compound.getInt("TextureState"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("ChargeLevel", this.getChargeLevel());
        compound.putInt("Command", this.getCommand().ordinal());
        compound.putBoolean("AltTexture", hasAltTexture());
        if (this.getOwnerUUID() != null) {
            compound.putUUID("OwnerUUID", this.getOwnerUUID());
        }
        compound.putInt("TextureState", getTextureState());
    }

    @Override
    public List<? extends ExtendedSensor<? extends CogolemEntity>> getSensors() {
        return CogolemAI.getSensors();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getCoreTasks() {
        return CogolemAI.getCoreTasks();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getIdleTasks() {
        return CogolemAI.getIdleTasks();
    }

    @Override
    public BrainActivityGroup<? extends CogolemEntity> getFightTasks() {
        return CogolemAI.getFightTasks();
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        CogolemAnimation.registerControllers(controllerRegistrar, this);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        this.tickBrain(this);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("    Cogolem"));
        tooltip.add(Component.literal("Health  ")
                .append(barComponent((int) (getHealth() * 20 / getMaxHealth()))));
        tooltip.add(Component.literal("Charge ")
                .append(barComponent((int) (getChargeLevel() * 20/ MAX_CHARGE_LEVEL))));
        return true;
    }


    private MutableComponent barComponent(int level) {
        return Component.empty()
                .append(bars(Math.max(0, level), ChatFormatting.GREEN))
                .append(bars(Math.max(0, 20 - level), ChatFormatting.DARK_RED));

    }

    private MutableComponent bars(int level, ChatFormatting format) {
        return Component.literal(Strings.repeat('|', level))
                .withStyle(format);
    }

    public void cycleCommand(Player player) {
        if(level().isClientSide) {
            return;
        }
        if(getOwnerUUID() != null && !getOwnerUUID().equals(player.getUUID())) {
            return; // Only the owner can change the command
        }
        if(getOwnerUUID() == null) {
            setOwner(player);
        }
        setCommand(getCommand().cycle());
        if(getCommand() == GolemCommands.STAY) {
            getNavigation().stop();
        }
        player.sendSystemMessage(Component.literal("Cogolem command set to: " + getCommand().name())
                .withStyle(ChatFormatting.YELLOW));
    }

    int textureState = 0;  // Default texture (Andesite Block)

    private void toggleTexture(int newTextureState) {
        if (getTextureState() != newTextureState) {
            setTextureState(newTextureState);
        }
    }

    // Add this method in your CogolemEntity class
    public boolean hasAltTexture() {
        // Switch based on textureState to determine if alternate textures should be used
        switch (textureState) {
            case 1: // Andesite Alloy Block
                return true;
            case 2: // Brass Block
                return true;
            default: // Default texture (Andesite Block)
                return false;
        }
    }

    public int getTextureState() {
        return this.entityData.get(TEXTURE_STATE);
    }

    public void setTextureState(int state) {
        this.entityData.set(TEXTURE_STATE, state);
    }

    private long lastInteractionTime = 0;
    private static final long INTERACTION_DELAY = 500; // 500 ms delay

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (!level().isClientSide) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastInteractionTime < INTERACTION_DELAY) {
                return InteractionResult.FAIL;
            }

            lastInteractionTime = currentTime;

            ItemStack itemInHand = player.getItemInHand(hand);
            int newState = -1;

            if (itemInHand.is(Blocks.ANDESITE.asItem())) {
                newState = 0;
            } else if (itemInHand.is(AllBlocks.ANDESITE_ALLOY_BLOCK.get().asItem())) {
                newState = 1;
            } else if (itemInHand.is(AllBlocks.BRASS_BLOCK.get().asItem())) {
                newState = 2;
            }

            if (newState != -1 && getTextureState() != newState) {
                toggleTexture(newState);
                playSwitchSoundAndFeedback(player);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        return super.interactAt(player, vec, hand);
    }

    private void playSwitchSoundAndFeedback(Player player) {
        // Play a metallic sound when switching textures
        float pitch = 0.9F + level().random.nextFloat() * 0.2F;
        level().playSound(
                null, // All players can hear it
                getX(), getY(), getZ(), // Mob's position
                SoundEvents.ANVIL_LAND, // Sound to play
                SoundSource.NEUTRAL, // Sound category
                1.0F, // Volume
                pitch // Randomized pitch
        );

        // Feedback to the player
        player.sendSystemMessage(Component.literal("Switched texture!"));
    }

}
