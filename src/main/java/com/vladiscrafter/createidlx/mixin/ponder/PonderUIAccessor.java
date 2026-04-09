package com.vladiscrafter.createidlx.mixin.ponder;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.ui.PonderUI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PonderUI.class)
public interface PonderUIAccessor {
    @Accessor("scenes") List<PonderScene> createidlx$getScenes();
    @Accessor("index") void createidlx$setIndex(int index);
    @Accessor("lazyIndex") LerpedFloat createidlx$getLazyIndex();
}
