package com.happysg.biomechanical;

import com.happysg.biomechanical.world.entity.Cogolem;
import com.happysg.biomechanical.registry.*;
import com.happysg.biomechanical.content.tuner.GolemTunerOverlayRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod(BiomechanicalConstants.MOD_ID)
public class Biomechanical {
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(BiomechanicalConstants.MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public Biomechanical(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        /* INIT REGISTRIES */
        BMBlocks.register();
        BMBlockEntityTypes.register();
        BMItems.register();
        BMEntityTypes.register();
        BMPartials.register();
        BMCreativeModTabs.register(modEventBus);

        /* EVENTS */
        modEventBus.addListener(GolemTunerOverlayRenderer::registerOverlay);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    private void blockPlaced(BlockEvent.EntityPlaceEvent event) {
        var level = event.getLevel();
        var pos = event.getPos();
        if(!BMBlockPatternMatch.PUMPKIN_PREDICATE.test(event.getPlacedBlock())) return;

        BlockPattern.BlockPatternMatch match = BMBlockPatternMatch.getOrCreateCogolemFull().find(level, pos);
        if(match != null) {
            Cogolem cogolem = BMEntityTypes.COGOLEM.create((Level) level);
            if(cogolem == null) return;
            for(int x = 0; x < match.getWidth(); x++)
                for(int y = 0; y < match.getHeight(); y++)
                    for(int z = 0; z < match.getDepth(); z++) {
                        BlockInWorld biw = match.getBlock(x,y,z);
                        level.setBlock(biw.getPos(), Blocks.AIR.defaultBlockState(), 2);
                        level.levelEvent(2001, biw.getPos(), Block.getId(biw.getState()));
                    }
            cogolem.moveTo(match.getBlock(0, 2, 0).getPos().getCenter(), 0, 0);
            level.addFreshEntity(cogolem);
            for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, cogolem.getBoundingBox().inflate(5)))
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, cogolem);
            for(int x = 0; x < match.getWidth(); x++)
                for(int y = 0; y < match.getHeight(); y++)
                    for(int z = 0; z < match.getDepth(); z++) {
                        BlockInWorld biw = match.getBlock(x,y,z);
                        level.blockUpdated(biw.getPos(), Blocks.AIR);
                    }
        }
    }
}
