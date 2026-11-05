package com.vladiscrafter.createidlx.mixin.accessor.minecraft;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("font")
    Font createidlx$getFont();

    @Accessor("displayPos")
    int createidlx$getDisplayPos();

    @Accessor("highlightPos")
    int createidlx$getHighlightPos();
}
