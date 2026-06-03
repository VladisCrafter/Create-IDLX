package com.vladiscrafter.createidlx.util.bridge;

import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public interface HosePulleyFluidReservoirVolumeHolder {
    Pair<Integer, Integer> createidlx$getReservoirVolume(Level level);
}