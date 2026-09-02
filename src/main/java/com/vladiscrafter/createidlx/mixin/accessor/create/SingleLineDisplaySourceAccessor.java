package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SingleLineDisplaySource.class)
public interface SingleLineDisplaySourceAccessor {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Invoker("allowsLabeling")
    boolean createidlx$callAllowsLabeling(DisplayLinkContext context);

    @Invoker("provideLine")
    MutableComponent createidlx$callProvideLine(DisplayLinkContext context, DisplayTargetStats stats);

    @Invoker("getFlapDisplayLayoutName")
    String createidlx$callGetFlapDisplayLayoutName(DisplayLinkContext context);
}
