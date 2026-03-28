package com.vladiscrafter.createidlx.mixin.create.elevator;

import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.vladiscrafter.createidlx.util.elevator.ElevatorContactBlockEntityExt;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElevatorContactBlockEntity.class)
public abstract class ElevatorContactBlockEntityMixin implements ElevatorContactBlockEntityExt {

    @Shadow public String longName;
    @Unique private String createidlx$lastReportedCurrentFloorLongName = "";

    @Inject(method = "updateDisplayedFloor", at = @At("HEAD"))
    private void createidlx$captureLongName(String floor, CallbackInfo ci) {
        ElevatorContactBlockEntity self = (ElevatorContactBlockEntity) (Object) this;

        if (self.getLevel() == null || self.columnCoords == null) {
            this.createidlx$lastReportedCurrentFloorLongName = this.longName;
            return;
        }

        ElevatorColumn column = ElevatorColumn.get(self.getLevel(), self.columnCoords);
        if (column instanceof ElevatorContactBlockEntityExt holder) {
            String name = holder.createidlx$getLastReportedCurrentFloorLongName();
            this.createidlx$lastReportedCurrentFloorLongName = name.isEmpty() ? "" : name;
        } else {
            this.createidlx$lastReportedCurrentFloorLongName = this.longName;
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void createidlx$writeLastReportedCurrentFloorLongName(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        tag.putString("LastReportedCurrentFloorLongName", createidlx$lastReportedCurrentFloorLongName);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void createidlx$readLastReportedCurrentFloorLongName(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("LastReportedCurrentFloorLongName")) {
            createidlx$lastReportedCurrentFloorLongName = tag.getString("LastReportedCurrentFloorLongName");
        }
    }

    @Override
    public String createidlx$getLastReportedCurrentFloorLongName() {
        return createidlx$lastReportedCurrentFloorLongName;
    }
}