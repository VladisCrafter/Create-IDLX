package com.vladiscrafter.createidlx.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.content.source.CountdownDisplaySource;
import com.vladiscrafter.createidlx.content.source.CurrentFloorExtendedDisplaySource;
import com.vladiscrafter.createidlx.content.source.CurrentTargetFloorDisplaySource;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CreateIDLXDisplaySources {

    public static final DisplaySource CURRENT_FLOOR_EXTENDED = new CurrentFloorExtendedDisplaySource();
    public static final DisplaySource CURRENT_TARGET_FLOOR = new CurrentTargetFloorDisplaySource();

    public static final DisplaySource COUNTDOWN = new CountdownDisplaySource();

    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(CreateBuiltInRegistries.DISPLAY_SOURCE.key())) return;

        registerByBlock(CURRENT_FLOOR_EXTENDED, "current_floor_extended", AllBlocks.ELEVATOR_CONTACT.get()); // TODO: remake with Record
        registerByBlock(CURRENT_TARGET_FLOOR, "current_target_floor", AllBlocks.ELEVATOR_CONTACT.get());

        registerByBlock(COUNTDOWN, "countdown", AllBlocks.CUCKOO_CLOCK.get());
    }

    private static void registerByBlock(DisplaySource displaySource, String displaySourceId, Block block) {
        ResourceLocation displaySourceIdRL = CreateIDLX.asResource(displaySourceId);
        Registry.register(CreateBuiltInRegistries.DISPLAY_SOURCE, displaySourceIdRL, displaySource);
        DisplaySource.BY_BLOCK.add(block, displaySource);
    }
}
