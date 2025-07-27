package com.happysg.createbiomechanical.content.station;

import com.happysg.createbiomechanical.content.cogolem.CogolemEntity;
import com.happysg.createbiomechanical.content.cogolem.GolemCommands;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class StationBlockEntity extends KineticBlockEntity {

    BlockPos controllerPos = BlockPos.ZERO;
    CogolemEntity cachedCogolem = null;
    GolemCommands prevCommand = null; // Used to store the previous command of the occupying cogolem

    public StationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        setLazyTickRate(20); // Set the lazy tick rate to 20 ticks (1 second) for periodic updates
    }


    @Override
    public void lazyTick() {
        super.lazyTick();
        if (!isController())
            return; // Only run the logic if this is the controller block
        chargeGolem();
        releaseOccupyingCogolem();
    }

    private void chargeGolem() {
        if (validateGoloem(cachedCogolem) && cachedCogolem.getChargeLevel() < 100) {

            // If there is a valid cogolem occupying the station, charge it
            cachedCogolem.setYRot(getBlockState().getValue(HORIZONTAL_FACING).toYRot()); // Ensure the cogolem's rotation matches the station's facing
            cachedCogolem.setYBodyRot(getBlockState().getValue(HORIZONTAL_FACING).toYRot());
            BrainUtils.clearMemory(cachedCogolem, MemoryModuleType.LOOK_TARGET); // Clear the look target memory to prevent the cogolem from looking at other entities while charging
            float chargeLevel = cachedCogolem.getChargeLevel();
            if (chargeLevel < 100) {
                // Increase the charge level of the cogolem if it's below 100
                float speed = Math.abs(getLeftShaftSpeed()) + Math.abs(getRightShaftSpeed()); // Calculate the charge value based on the shaft speeds
                float value = speed * 0.01f; // Adjust the multiplier to control the charging rate
                cachedCogolem.setChargeLevel(Math.min(chargeLevel + value, 100)); // Increment charge level by 1, max 100
            }
        }
    }


    private void releaseOccupyingCogolem() {
        if (cachedCogolem == null) {
            return; // No cogolem occupying the station
        }
        if (cachedCogolem.getChargeLevel() == 100) {
            // Release the cogolem if it was wandering or following and has a high charge level
            if (prevCommand == GolemCommands.FOLLOW || prevCommand == GolemCommands.WANDER)
                cachedCogolem.setCommand(prevCommand);
            prevCommand = null; // Reset the previous command after releasing
            cachedCogolem.triggerAnim("charge", "stand");
            cachedCogolem = null; // Clear the cached cogolem reference
        }
    }


    //todo cache the speed of the left and right shaft potentially for performance
    public float getLeftShaftSpeed() {
        Direction front = getBlockState().getValue(HORIZONTAL_FACING);
        Direction left = getBlockState().getValue(HORIZONTAL_FACING).getClockWise();
        BlockPos leftShaftPos = getBlockPos().relative(front).above().relative(left);
        if (level.getBlockEntity(leftShaftPos) instanceof KineticBlockEntity kineticBlockEntity) {
            return kineticBlockEntity.getSpeed();
        }
        return 0;
    }

    public float getRightShaftSpeed() {
        Direction front = getBlockState().getValue(HORIZONTAL_FACING);
        Direction right = getBlockState().getValue(HORIZONTAL_FACING).getCounterClockWise();
        BlockPos rightShaftPos = getBlockPos().relative(front).above().relative(right);
        if (level.getBlockEntity(rightShaftPos) instanceof KineticBlockEntity kineticBlockEntity) {
            return kineticBlockEntity.getSpeed();
        }
        return 0;
    }

    public void setControllerPos(@Nonnull BlockPos pos) {
        controllerPos = pos;
    }


    public boolean isController() {
        return getBlockState().getValue(StationBlock.SHAPE) == StationShape.CENTER;
    }

    public Optional<StationBlockEntity> getController() {
        if (isController()) {
            return Optional.of(this);
        }
        if (controllerPos == null || !(level.getBlockEntity(controllerPos) instanceof StationBlockEntity stationBlockEntity)) {
            return Optional.empty();
        }
        return Optional.of(stationBlockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!isController()) {
            return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        }
        tooltip.add(Component.literal("    Debug Info"));
        tooltip.add(Component.literal("Left Shaft Speed: " + getLeftShaftSpeed()));
        tooltip.add(Component.literal("Right Shaft Speed: " + getRightShaftSpeed()));
        return true;
    }


    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        controllerPos = BlockPos.of(compound.getLong("controllerPos"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putLong("controllerPos", controllerPos.asLong());
    }

    public void removeMultiBlock() {
        getController().ifPresent(controller -> {
            level.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
        });
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(2, 1, 2);
    }

    public void onEntityInside(Entity entity) {
        if (entity instanceof CogolemEntity golem) {
            if (golem.getChargeLevel() > 95 && golem.getCommand() != GolemCommands.STATION) {
                // If the cogolem is already charged enough and not set to STATION, ignore it
                return; // Ignore if the cogolem is already occupying the station
            }
            cachedCogolem = golem; // Set the occupying cogolem if none is set
            prevCommand = golem.getCommand(); // Store the previous command of the occupying cogolem
            cachedCogolem.setCommand(GolemCommands.STAY); // Set the command of the occupying cogolem to STATION
            Vec3 cogolemPosition = getBlockPos().relative(getBlockState().getValue(HORIZONTAL_FACING)).getBottomCenter(); // Get the position of the station block
            cachedCogolem.setPos(cogolemPosition);
            cachedCogolem.setDeltaMovement(0, 0, 0); // Stop any movement of the cogolem when it enters the station
            cachedCogolem.setYBodyRot(getBlockState().getValue(HORIZONTAL_FACING).toYRot()); // Set the rotation of the cogolem to match the station
            cachedCogolem.setYRot(getBlockState().getValue(HORIZONTAL_FACING).toYRot()); // Set the rotation of the cogolem to match the station
            cachedCogolem.triggerAnim("charge", "sit");
        }
    }

    private boolean validateGoloem(CogolemEntity occupyingCogolem) {//rename or refactor this method to better reflect its purpose
        if (occupyingCogolem == null) {
            return false; // No cogolem occupying the station
        }
        Vec3 cogolemPosition = occupyingCogolem.position();
        // Check if the occupying cogolem is still within the station block
        double distanceSquared = cogolemPosition.distanceToSqr(getBlockPos().getBottomCenter());
        return distanceSquared < 2.0 && occupyingCogolem.getCommand() == GolemCommands.STAY;
    }
}

