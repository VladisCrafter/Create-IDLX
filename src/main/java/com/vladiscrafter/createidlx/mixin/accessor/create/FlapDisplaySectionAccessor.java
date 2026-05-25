package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.trains.display.FlapDisplaySection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FlapDisplaySection.class)
public interface FlapDisplaySectionAccessor {
    @Accessor("size")
    void createidlx$setSize(float width);
}
