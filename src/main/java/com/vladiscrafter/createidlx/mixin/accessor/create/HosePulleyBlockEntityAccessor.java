package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HosePulleyBlockEntity.class)
public interface HosePulleyBlockEntityAccessor {
    @Accessor("handler") HosePulleyFluidHandler createidlx$getHandler();
}
