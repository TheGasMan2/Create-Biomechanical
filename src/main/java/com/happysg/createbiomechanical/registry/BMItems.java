package com.happysg.createbiomechanical.registry;

import com.happysg.createbiomechanical.Biomechanical;
import com.happysg.createbiomechanical.content.tuner.TunerItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class BMItems {

    public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_ELECTRON_BATTERY = Biomechanical.REGISTRATE
            .item("incomplete_electron_battery", SequencedAssemblyItem::new)
            .register();

    public static final ItemEntry<Item> ELECTRON_BATTERY = Biomechanical.REGISTRATE
            .item("electron_battery", Item::new)
            .register();

    public static final ItemEntry<TunerItem> TUNER = Biomechanical.REGISTRATE
            .item("tuner", TunerItem::new)
            .model(AssetLookup.itemModel("tuner"))
            .register();

    public static void register() {
        Biomechanical.LOGGER.info("Registering Items for " + Biomechanical.MODID);
    }
}
