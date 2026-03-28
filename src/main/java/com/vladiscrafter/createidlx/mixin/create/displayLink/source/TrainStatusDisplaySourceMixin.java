package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.redstone.displayLink.source.TrainStatusDisplaySource;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrainStatusDisplaySource.class)
public class TrainStatusDisplaySourceMixin {
    @ModifyReturnValue(method = "allowsLabeling", at = @At("RETURN"), remap = false)
    protected boolean allowsLabeling(boolean original) {
        return CIDLXConfigs.server.enhanceTrainStatusDisplaySource.get();
    }
}
