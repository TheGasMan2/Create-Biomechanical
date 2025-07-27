package com.happysg.createbiomechanical.content.tuner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.happysg.createbiomechanical.Biomechanical;
import com.happysg.createbiomechanical.registry.BMItems;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.api.equipment.goggles.IHaveCustomOverlayIcon;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.RemovedGuiUtils;
import com.simibubi.create.foundation.mixin.accessor.MouseHandlerAccessor;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CClient;

import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.outliner.Outliner.OutlineEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class GolemTunerOverlayRenderer {

    public static final LayeredDraw.Layer ENTITY_OVERLAY = GolemTunerOverlayRenderer::renderOverlay;

    private static final Map<Object, OutlineEntry> outlines = Outliner.getInstance().getOutlines();

    public static int hoverTicks = 0;
    public static Entity lastHovered = null;

    public static void registerOverlay(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Biomechanical.rl("goggle_entity_info"), GolemTunerOverlayRenderer.ENTITY_OVERLAY);
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof EntityHitResult result)) {
            lastHovered = null;
            hoverTicks = 0;
            return;
        }

        for (OutlineEntry entry : outlines.values()) {
            if (!entry.isAlive())
                continue;
            Outline outline = entry.getOutline();
            if (outline instanceof ValueBox && !((ValueBox) outline).isPassive)
                return;
        }

        Entity entity = result.getEntity();

        hoverTicks++;
        lastHovered = entity;

        boolean holdingTuner = mc.player.getMainHandItem().is(BMItems.TUNER.asItem())
                || mc.player.getOffhandItem().is(BMItems.TUNER.asItem());

        boolean isShifting = mc.player.isShiftKeyDown();

        boolean hasGoggleInformation = entity instanceof ITunerOverlay;
        boolean hasHoveringInformation = entity instanceof IHaveHoveringInformation;

        boolean goggleAddedInformation = false;
        boolean hoverAddedInformation = false;

        ItemStack item = BMItems.TUNER.asStack();
        List<Component> tooltip = new ArrayList<>();

        if (entity instanceof IHaveCustomOverlayIcon customOverlayIcon)
            item = customOverlayIcon.getIcon(isShifting);

        if (hasGoggleInformation && holdingTuner) {
            IHaveGoggleInformation gte = (IHaveGoggleInformation) entity;
            goggleAddedInformation = gte.addToGoggleTooltip(tooltip, isShifting);
        }

        if (hasHoveringInformation) {
            if (!tooltip.isEmpty())
                tooltip.add(CommonComponents.EMPTY);
            IHaveHoveringInformation hte = (IHaveHoveringInformation) entity;
            hoverAddedInformation = hte.addToTooltip(tooltip, isShifting);

            if (goggleAddedInformation && !hoverAddedInformation)
                tooltip.remove(tooltip.size() - 1);
        }

        // break early if goggle or hover returned false when present
        if ((hasGoggleInformation && !goggleAddedInformation) && (hasHoveringInformation && !hoverAddedInformation)) {
            hoverTicks = 0;
            return;
        }

        if (tooltip.isEmpty()) {
            hoverTicks = 0;
            return;
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        int tooltipTextWidth = 0;
        for (FormattedText textLine : tooltip) {
            int textLineWidth = mc.font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2; // gap between title lines and next lines
            tooltipHeight += (tooltip.size() - 1) * 10;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();

        CClient cfg = AllConfigs.client();
        int posX = width / 2 + cfg.overlayOffsetX.get();
        int posY = height / 2 + cfg.overlayOffsetY.get();

        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);

        float fade = Mth.clamp((hoverTicks + deltaTracker.getGameTimeDeltaPartialTick(false)) / 24f, 0, 1);
        Boolean useCustom = cfg.overlayCustomColor.get();
        Color colorBackground = useCustom ? new Color(cfg.overlayBackgroundColor.get())
                : BoxElement.COLOR_VANILLA_BACKGROUND.scaleAlpha(.75f);
        Color colorBorderTop = useCustom ? new Color(cfg.overlayBorderColorTop.get())
                : BoxElement.COLOR_VANILLA_BORDER.getFirst().copy();
        Color colorBorderBot = useCustom ? new Color(cfg.overlayBorderColorBot.get())
                : BoxElement.COLOR_VANILLA_BORDER.getSecond().copy();

        if (fade < 1) {
            poseStack.translate(Math.pow(1 - fade, 3) * Math.signum(cfg.overlayOffsetX.get() + .5f) * 8, 0, 0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        GuiGameElement.of(item)
                .at(posX + 10, posY - 16, 450)
                .render(guiGraphics);

        if (!Mods.MODERNUI.isLoaded()) {
            // default tooltip rendering when modernUI is not loaded
            RemovedGuiUtils.drawHoveringText(guiGraphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(),
                    colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);

            poseStack.popPose();
            return;
        }

        /* special handling for modernUI */
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        double cursorX = mouseHandler.xpos();
        double cursorY = mouseHandler.ypos();
        ((MouseHandlerAccessor) mouseHandler).create$setXPos(Math.round(cursorX / guiScale) * guiScale);
        ((MouseHandlerAccessor) mouseHandler).create$setYPos(Math.round(cursorY / guiScale) * guiScale);

        RemovedGuiUtils.drawHoveringText(guiGraphics, tooltip, posX, posY, width, height, -1, colorBackground.getRGB(),
                colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);

        ((MouseHandlerAccessor) mouseHandler).create$setXPos(cursorX);
        ((MouseHandlerAccessor) mouseHandler).create$setYPos(cursorY);
        poseStack.popPose();
    }
}