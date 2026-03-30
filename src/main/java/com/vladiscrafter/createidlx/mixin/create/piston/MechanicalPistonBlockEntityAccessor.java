package com.vladiscrafter.createidlx.mixin.create.piston;

import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MechanicalPistonBlockEntity.class)
public interface MechanicalPistonBlockEntityAccessor {
    @Accessor("extensionLength") int $createidlx$getExtensionLength();
}
