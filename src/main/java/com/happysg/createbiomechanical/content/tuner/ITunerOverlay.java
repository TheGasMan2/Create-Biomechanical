package com.happysg.createbiomechanical.content.tuner;

import com.happysg.createbiomechanical.registry.BMItems;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.world.item.ItemStack;

public interface ITunerOverlay extends IHaveGoggleInformation {
    @Override
    default ItemStack getIcon(boolean isPlayerSneaking) {
        return BMItems.TUNER.asStack();
    }
}
