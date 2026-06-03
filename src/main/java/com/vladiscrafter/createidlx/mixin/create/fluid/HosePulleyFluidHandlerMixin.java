package com.vladiscrafter.createidlx.mixin.create.fluid;

import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.vladiscrafter.createidlx.util.bridge.HosePulleyFluidReservoirVolumeHolder;
import com.vladiscrafter.createidlx.util.fluid.HosePulleyFluidReservoirVolumeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(HosePulleyFluidHandler.class)
public abstract class HosePulleyFluidHandlerMixin implements HosePulleyFluidReservoirVolumeHolder {
    @Shadow private Supplier<BlockPos> rootPosGetter;

    @Override
    public Pair<Integer, Integer> createidlx$getReservoirVolume(Level level) {
        return HosePulleyFluidReservoirVolumeHelper.calculateVolume(level, rootPosGetter.get(),
                AllConfigs.server().fluids.hosePulleyRange.get(), AllConfigs.server().fluids.hosePulleyBlockThreshold.get());
    }
}