package com.vladiscrafter.createidlx.mixin.create.elevator;

import com.simibubi.create.content.contraptions.elevator.ElevatorColumn;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.vladiscrafter.createidlx.util.elevator.ElevatorContactBlockEntityExt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElevatorContraption.class)
public abstract class ElevatorContraptionMixin {

    @Inject(method = "broadcastFloorData", at = @At("HEAD"))
    private void createidlx$cacheLastReportedCurrentFloorLongName(Level level, BlockPos contactPos, CallbackInfo ci) {
        ElevatorColumn column = ElevatorColumn.get(level, ((ElevatorContraption) (Object) this).getGlobalColumn());
        if (column == null) return;
        if (!(level.getBlockEntity(contactPos) instanceof ElevatorContactBlockEntity ecbe)) return;

        ((ElevatorContactBlockEntityExt) column).createidlx$setLastReportedCurrentFloorLongName(ecbe.longName);
    }

    @Inject(method = "broadcastFloorData", at = @At("RETURN"))
    private void createidlx$clearLastReportedCurrentFloorLongNamee(Level level, BlockPos contactPos, CallbackInfo ci) {
        ElevatorColumn column = ElevatorColumn.get(level, ((ElevatorContraption) (Object) this).getGlobalColumn());
        if (column == null) return;

        ((ElevatorContactBlockEntityExt) column).createidlx$clearLastReportedCurrentFloorLongName();
    }
}
