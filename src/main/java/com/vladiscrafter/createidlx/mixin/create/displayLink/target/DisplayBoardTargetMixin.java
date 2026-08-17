package com.vladiscrafter.createidlx.mixin.create.displayLink.target;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayBoardTarget;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.bridge.NixieTubeDisplaySourceColorHolder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayBoardTarget.class)
public abstract class DisplayBoardTargetMixin {
    @Shadow
    protected abstract FlapDisplayBlockEntity getController(DisplayLinkContext context);

    @Inject(method = "acceptFlapText", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/api/behaviour/display/DisplaySource;loadFlapDisplayLayout(Lcom/simibubi/create/content/redstone/displayLink/DisplayLinkContext;Lcom/simibubi/create/content/trains/display/FlapDisplayBlockEntity;Lcom/simibubi/create/content/trains/display/FlapDisplayLayout;I)V"))
    private void createidlx$injectColorAcceptation(int line, List<List<MutableComponent>> text, DisplayLinkContext context, CallbackInfo ci, @Local(name = "i") int i) {
        createidlx$acceptColor(i + line, context);
    }

    @Unique
    private void createidlx$acceptColor(int lineIndex, DisplayLinkContext context) {
        if (!(CIDLXConfigs.server.addColorCopyingToNixieTubeDisplaySource.get())) return;

        DisplaySource source = context.blockEntity().activeSource;
        if (!(source instanceof NixieTubeDisplaySourceColorHolder holder)) return;

        DyeColor color = holder.createidlx$provideColor(context);
        if (color == null) return;

        FlapDisplayBlockEntity controller = getController(context);
        controller.setColour(lineIndex, color);
    }
}
