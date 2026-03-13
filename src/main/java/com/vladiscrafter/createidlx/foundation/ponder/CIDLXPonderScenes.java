package com.vladiscrafter.createidlx.foundation.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.vladiscrafter.createidlx.foundation.ponder.scenes.AttachedLabelScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.foundation.content.DebugScenes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class CIDLXPonderScenes {

    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(AllBlocks.DISPLAY_LINK)
                        .addStoryBoard("attached_label", AttachedLabelScenes::attachedLabel,
                                AllCreatePonderTags.DISPLAY_SOURCES, AllCreatePonderTags.DISPLAY_TARGETS);



    }
}
