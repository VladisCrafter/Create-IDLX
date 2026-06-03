package com.vladiscrafter.createidlx.util.fluid;

import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class HosePulleyFluidReservoirVolumeHelper {
    private HosePulleyFluidReservoirVolumeHelper() {}

    public static Pair<Integer, Integer> calculateVolume(Level level, BlockPos rootPos, int maxRange, int maxBlocks) {
        if (level == null || rootPos == null || !level.isLoaded(rootPos)) return Pair.of(0, 0);

        FluidState rootFluidState = level.getFluidState(rootPos);
        if (rootFluidState.isEmpty()) return Pair.of(0, 0);

        int maxRangeSq = maxRange * maxRange;
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> frontier = new ArrayDeque<>();
        frontier.add(rootPos);

        Fluid fluid = null;

        while (!frontier.isEmpty() && (maxBlocks < 0 || visited.size() <= maxBlocks)) {
            BlockPos currentPos = frontier.removeFirst();
            if (!level.isLoaded(currentPos)) break;
            if (!visited.add(currentPos)) continue;

            FluidState fluidState = level.getFluidState(currentPos);
            if (fluidState.isEmpty()) continue;

            Fluid currentFluid = FluidHelper.convertToStill(fluidState.getType());
            if (fluid == null) fluid = currentFluid;
            if (!currentFluid.isSame(fluid)) continue;

            for (Direction side : Iterate.directions) {
                BlockPos offsetPos = currentPos.relative(side);
                if (!level.isLoaded(offsetPos)) continue;
                if (visited.contains(offsetPos)) continue;
                if (offsetPos.distSqr(rootPos) > maxRangeSq) continue;

                FluidState nextFluidState = level.getFluidState(offsetPos);
                if (nextFluidState.isEmpty()) continue;

                Fluid nextFluid = nextFluidState.getType();
                if (nextFluid == FluidHelper.convertToFlowing(nextFluid) && side == Direction.UP
                        && !VecHelper.onSameAxis(rootPos, offsetPos, Direction.Axis.Y)) continue;

                frontier.add(offsetPos);
            }
        }

        int allVisited = visited.size();

        visited.removeIf(blockPos -> !level.getFluidState(blockPos).isSource());

        int sourcesVisited = visited.size();

        return Pair.of(allVisited, sourcesVisited);
    }
}
