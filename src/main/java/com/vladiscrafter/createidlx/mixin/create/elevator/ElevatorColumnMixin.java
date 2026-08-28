package com.vladiscrafter.createidlx.mixin.create.elevator;

import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.vladiscrafter.createidlx.util.bridge.ElevatorContactBlockEntityLongNameHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ElevatorColumn.class)
public abstract class ElevatorColumnMixin implements ElevatorContactBlockEntityLongNameHolder {

    @Unique
    private String createidlx$lastReportedCurrentFloorLongName = "";

    public void createidlx$setLastReportedCurrentFloorLongName(String name) {
        this.createidlx$lastReportedCurrentFloorLongName = (name == null) ? "" : name;
    }

    public String createidlx$getLastReportedCurrentFloorLongName() {
        return createidlx$lastReportedCurrentFloorLongName;
    }
}
