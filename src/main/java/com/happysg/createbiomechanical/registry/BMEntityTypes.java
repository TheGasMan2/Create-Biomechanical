package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import com.happysg.createbiomechanical.content.cogolem.CogolemEntity;
import com.happysg.createbiomechanical.content.cogolem.CogolemRenderer;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import static com.happysg.createbiomechanical.Biomechanical.REGISTRATE;

public class BMEntityTypes {

    public static EntityEntry<CogolemEntity> COGOLEM = REGISTRATE
            .entity("cogolem", CogolemEntity::new, MobCategory.CREATURE)
            .properties(p -> p.sized(1.5f, 2.75f).clientTrackingRange(8).updateInterval(3))
            .properties(EntityType.Builder::fireImmune)
            .attributes(CogolemEntity::createAttributes)
            .renderer(() -> CogolemRenderer::new)
            .loot((registrateEntityLootTables, cogolemEntityEntityType) -> registrateEntityLootTables.add(cogolemEntityEntityType, LootTable.lootTable()
                    .withPool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1.0F))
                                    .add(LootItem.lootTableItem(BMBlocks.POWER_CORE.get()))
                    )))
            .spawnEgg(0xba8459, 0x7e7f73)
            .tab(BMCreativeModTabs.CREATE_BIOMECHANICAL_TAB.getKey())
            .build()
            .register();


    public static void register() {
        Biomechanical.LOGGER.info("Registering Entity Types for " + Biomechanical.MODID);
    }
}
