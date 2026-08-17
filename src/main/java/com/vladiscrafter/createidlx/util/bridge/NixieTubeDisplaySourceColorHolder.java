package com.vladiscrafter.createidlx.util.bridge;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;

public interface NixieTubeDisplaySourceColorHolder {
    @Nullable DyeColor createidlx$provideColor(DisplayLinkContext context);
}
