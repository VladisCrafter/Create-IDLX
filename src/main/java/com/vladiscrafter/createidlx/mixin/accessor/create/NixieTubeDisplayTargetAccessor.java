package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NixieTubeDisplayTarget.class)
public interface NixieTubeDisplayTargetAccessor {
    @Invoker("getWidth")
    int createidlx$invokeGetWidth(DisplayLinkContext context);
}
