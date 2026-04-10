package com.vladiscrafter.createidlx.infrastructure.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.vladiscrafter.createidlx.infrastructure.ponder.scenes.DisplayLinkExtendedScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CIDLXPonderScenes {

    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(AllBlocks.DISPLAY_LINK)
                        .addStoryBoard("attached_label", DisplayLinkExtendedScenes::attachedLabel,
                                AllCreatePonderTags.DISPLAY_SOURCES, AllCreatePonderTags.DISPLAY_TARGETS)
                        .addStoryBoard("clipboard_copying", DisplayLinkExtendedScenes::clipboardCopying,
                                AllCreatePonderTags.DISPLAY_SOURCES, AllCreatePonderTags.DISPLAY_TARGETS)
                        .addStoryBoard("clipboard_copying_properties", DisplayLinkExtendedScenes::clipboardCopyingProperties,
                                AllCreatePonderTags.DISPLAY_SOURCES, AllCreatePonderTags.DISPLAY_TARGETS);
    }
}
