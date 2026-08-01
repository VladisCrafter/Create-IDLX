package com.vladiscrafter.createidlx.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.redstone.displayLink.source.StopWatchDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.TimeOfDayDisplaySource;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.content.displayLink.source.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class CreateIDLXDisplaySources {

    public static final DisplaySource CURRENT_FLOOR_EXTENDED = new CurrentFloorExtendedDisplaySource();
    public static final DisplaySource CURRENT_TARGET_FLOOR = new CurrentTargetFloorDisplaySource();

    public static final DisplaySource ELEVATOR_MOVEMENT_DIRECTION = new ElevatorMovementDirectionDisplaySource();

    public static final DisplaySource COUNTDOWN = new CountdownDisplaySource();

    public static final DisplaySource MECHANICAL_PISTON_EXTENSION_STATE = new MechanicalPistonExtensionStateDisplaySource();

    public static final DisplaySource FLUID_RESERVOIR_VOLUME = new FluidReservoirVolumeDisplaySource();

    public static final DisplaySource HELD_ITEM_DURABILITY = new HeldItemDurabilityDisplaySource();

    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(CreateBuiltInRegistries.DISPLAY_SOURCE.key())) return;

        registerSource(CURRENT_FLOOR_EXTENDED, "current_floor_extended",
                AllBlocks.ELEVATOR_CONTACT);
        registerSource(CURRENT_TARGET_FLOOR, "current_target_floor",
                AllBlocks.ELEVATOR_CONTACT);

        registerSource(ELEVATOR_MOVEMENT_DIRECTION, "elevator_movement_direction",
                AllBlocks.ELEVATOR_PULLEY);

        registerSource(COUNTDOWN, "countdown",
                AllBlocks.CUCKOO_CLOCK,
                AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);

        registerSource(MECHANICAL_PISTON_EXTENSION_STATE, "mechanical_piston_extension_state",
                AllBlocks.MECHANICAL_PISTON,
                AllBlocks.STICKY_MECHANICAL_PISTON);

        registerSource(FLUID_RESERVOIR_VOLUME, "fluid_reservoir_volume",
                AllBlocks.HOSE_PULLEY);

        registerSource(HELD_ITEM_DURABILITY, "held_item_durability",
                AllBlocks.DEPLOYER);

        addSourceToBlocks(getSourceFromCreate("time_of_day"), AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);
        addSourceToBlocks(getSourceFromCreate("stopwatch"), AllBlocks.MYSTERIOUS_CUCKOO_CLOCK);
    }

    private static DisplaySource getSourceFromCreate(String id) {
        return CreateBuiltInRegistries.DISPLAY_SOURCE.get(Create.asResource(id));
    }

    private static void registerSource(DisplaySource displaySource, String displaySourceId, BlockEntry<?>... blocks) {
        Registry.register(CreateBuiltInRegistries.DISPLAY_SOURCE, CreateIDLX.asResource(displaySourceId), displaySource);
        addSourceToBlocks(displaySource, blocks);
    }

    private static void addSourceToBlocks(DisplaySource displaySource, BlockEntry<?>... blocks) {
        for (BlockEntry<?> block : blocks) DisplaySource.BY_BLOCK.add(block.get(), displaySource);
    }
}
