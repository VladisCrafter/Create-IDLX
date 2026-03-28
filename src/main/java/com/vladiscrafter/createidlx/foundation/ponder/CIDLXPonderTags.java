package com.vladiscrafter.createidlx.foundation.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CIDLXPonderTags {

    public static final ResourceLocation DISPLAY_SOURCES = AllCreatePonderTags.DISPLAY_SOURCES;

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addToTag(DISPLAY_SOURCES)
                .add(AllBlocks.ELEVATOR_CONTACT)
                .add(AllBlocks.ELEVATOR_PULLEY);
    }
}
