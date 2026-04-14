package com.happysg.biomechanical;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class BMPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return BiomechanicalConstants.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<Item> HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        var HELPER = helper.withKeyFunction(BuiltInRegistries.ITEM::getKey);

        /* Sample code:
        This adds the item in the ponder tag high logistics section, but requires you to run the gradle task
        "runData" to generate the tag json file


        HELPER.addToTag(AllCreatePonderTags.HIGH_LOGISTICS)
                .add(YOUR_ITEM)
                .add(ANOTHER_ITEM);*/
    }
}
