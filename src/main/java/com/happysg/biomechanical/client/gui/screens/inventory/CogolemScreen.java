package com.happysg.biomechanical.client.gui.screens.inventory;

import com.happysg.biomechanical.BiomechanicalConstants;
import com.happysg.biomechanical.world.inventory.CogolemMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CogolemScreen extends AbstractContainerScreen<CogolemMenu> {
    private static final ResourceLocation TEXTURE = BiomechanicalConstants.id("textures/gui/container/cogolem.png");
    public CogolemScreen(CogolemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, getGuiLeft(), getGuiTop(), 0,0, 176, 166);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
