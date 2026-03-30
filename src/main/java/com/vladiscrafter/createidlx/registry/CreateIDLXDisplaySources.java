package com.vladiscrafter.createidlx.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.redstone.displayLink.source.StopWatchDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.TimeOfDayDisplaySource;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.content.source.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CreateIDLXDisplaySources {

    public static final DisplaySource TIME_OF_DAY = new TimeOfDayDisplaySource();
    public static final DisplaySource STOPWATCH = new StopWatchDisplaySource();

    public static final DisplaySource CURRENT_FLOOR_EXTENDED = new CurrentFloorExtendedDisplaySource();
    public static final DisplaySource CURRENT_TARGET_FLOOR = new CurrentTargetFloorDisplaySource();

    public static final DisplaySource ELEVATOR_MOVEMENT_DIRECTION = new ElevatorMovementDirectionDisplaySource();

    public static final DisplaySource COUNTDOWN = new CountdownDisplaySource();

    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(CreateBuiltInRegistries.DISPLAY_SOURCE.key())) return;

        registerSource(TIME_OF_DAY, Create.asResource("time_of_day"),
                AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);
        registerSource(STOPWATCH, Create.asResource("stopwatch"),
                AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);

        registerSource(CURRENT_FLOOR_EXTENDED, "current_floor_extended",
                AllBlocks.ELEVATOR_CONTACT);
        registerSource(CURRENT_TARGET_FLOOR, "current_target_floor",
                AllBlocks.ELEVATOR_CONTACT);

        registerSource(ELEVATOR_MOVEMENT_DIRECTION, "elevator_movement_direction",
                AllBlocks.ELEVATOR_PULLEY);

        registerSource(COUNTDOWN, "countdown",
                AllBlocks.CUCKOO_CLOCK,
                AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);
    }

    private static void registerSource(DisplaySource displaySource, String displaySourceId, BlockEntry<?>... blocks) {
        registerSource(displaySource, CreateIDLX.asResource(displaySourceId), blocks);
    }

    private static void registerSource(DisplaySource displaySource, ResourceLocation displaySourceIdRL, BlockEntry<?>... blocks) {
        Registry.register(CreateBuiltInRegistries.DISPLAY_SOURCE, displaySourceIdRL, displaySource);
        for (BlockEntry<?> block : blocks) DisplaySource.BY_BLOCK.add(block.get(), displaySource);
    }
}
