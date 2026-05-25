package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MechanicalPistonBlockEntity.class)
public interface MechanicalPistonBlockEntityAccessor {
    @Accessor(value = "extensionLength", remap = false)
    int $createidlx$getExtensionLength();
}