package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NixieTubeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NixieTubeDisplaySource.class)
public class NixieTubeDisplaySourceMixin {
    @ModifyReturnValue(method = "allowsLabeling", at = @At("RETURN"))
    protected boolean allowsLabeling(boolean original, @Local(argsOnly = true) DisplayLinkContext context) {
        return CIDLXConfigs.server.enhanceNixieTubeDisplaySource.get() || !(context.blockEntity().activeTarget instanceof NixieTubeDisplayTarget);
    }
}
