package com.vladiscrafter.createidlx.mixin.accessor.create;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeployerBlockEntity.class)
public interface DeployerBlockEntityAccessor {
    @Accessor("player")
    DeployerFakePlayer createidlx$getPlayer();
}
