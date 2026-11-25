package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SelectionScrollInput.class)
public interface SelectionScrollInputAccessor {
    @Invoker("updateTooltip")
    void createidlx$callUpdateTooltip();
}
