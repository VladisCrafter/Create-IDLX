package com.vladiscrafter.createidlx.mixin.create.displayLink;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.vladiscrafter.createidlx.content.DisplayLinkBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkBlockEntity.class)
public abstract class DisplayLinkBlockEntityMixin {

    @Inject(method = "addBehaviours", at = @At("TAIL"))
    private void createidlx$addClipboardBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        SmartBlockEntity self = (SmartBlockEntity) (Object) this;

        if (self.getBehaviour(DisplayLinkBehaviour.TYPE) == null) {
            behaviours.add(new DisplayLinkBehaviour(self));
        }
    }
}