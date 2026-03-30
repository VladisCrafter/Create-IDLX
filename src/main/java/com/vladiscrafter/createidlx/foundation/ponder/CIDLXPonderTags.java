package com.vladiscrafter.createidlx.foundation.ponder;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CIDLXPonderTags {

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addToTag(AllCreatePonderTags.DISPLAY_SOURCES)
                .add(AllBlocks.ELEVATOR_CONTACT)
                .add(AllBlocks.ELEVATOR_PULLEY)
                .add(AllBlocks.MECHANICAL_PISTON)
                .add(AllBlocks.STICKY_MECHANICAL_PISTON);
    }
}
