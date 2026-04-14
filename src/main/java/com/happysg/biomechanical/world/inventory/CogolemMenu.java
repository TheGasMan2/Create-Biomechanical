package com.happysg.biomechanical.world.inventory;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.registry.BMMenuTypes;
import com.happysg.biomechanical.world.entity.Cogolem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class CogolemMenu extends AbstractContainerMenu {
    private static final List<EquipmentSlot> SLOTS = List.of(
            EquipmentSlot.MAINHAND,
            EquipmentSlot.BODY,
            EquipmentSlot.CHEST
    );
    private static final List<ResourceLocation> EMPTY_ICONS = List.of(
            BiomechanicalConstants.id("item/empty_cogolem_slot_arms"),
            BiomechanicalConstants.id("item/empty_cogolem_slot_shield"),
            BiomechanicalConstants.id("item/empty_cogolem_slot_core")
    );

    private final Container armorContainer;
    private final Cogolem cogolem;

    // Open on client
    public CogolemMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, (Cogolem) playerInventory.player.level().getEntity(buf.readInt()));
    }

    public CogolemMenu(int containerId, Inventory inventory, Cogolem cogolem) {
        super(BMMenuTypes.COGOLEM_MENU.get(), containerId);
        this.armorContainer = cogolem.getArmor();
        this.cogolem = cogolem;
        for(int i = 0; i < 3; i++) {
            this.addSlot(new ArmorSlot(this.armorContainer, cogolem, SLOTS.get(i), i, 8, 20 + i*18, EMPTY_ICONS.get(i)));
        }

        int x = 8;
        int y = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, x + col * 18, y + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.armorContainer.stillValid(player)
                && this.cogolem.isAlive()
                && player.canInteractWithEntity(this.cogolem, 4.0);
    }
}
