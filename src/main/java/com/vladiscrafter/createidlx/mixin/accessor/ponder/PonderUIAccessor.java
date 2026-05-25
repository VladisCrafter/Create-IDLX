package com.vladiscrafter.createidlx.mixin.accessor.ponder;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PonderUI.class)
public interface PonderUIAccessor {
    @Accessor(value = "scenes", remap = false)
    List<PonderScene> createidlx$getScenes();

    @Accessor(value = "index", remap = false)
    void createidlx$setIndex(int index);

    @Accessor(value = "lazyIndex", remap = false)
    LerpedFloat createidlx$getLazyIndex();
}