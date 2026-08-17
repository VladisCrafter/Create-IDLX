package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NixieTubeDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.NixieTubeDisplayTarget;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.bridge.NixieTubeDisplaySourceColorHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NixieTubeDisplaySource.class)
public class NixieTubeDisplaySourceMixin implements NixieTubeDisplaySourceColorHolder {
    @Override
    @Unique
    public DyeColor createidlx$provideColor(DisplayLinkContext context) {
        if (!(CIDLXConfigs.server.addColorCopyingToNixieTubeDisplaySource.get())) return null;

        if (!(context.getSourceBlockEntity() instanceof NixieTubeBlockEntity ntbe)) return null;

        boolean copyColor = context.sourceConfig().getInt("CopyColor") == 1;
        if (!copyColor) return null;

        NixieTubeBlock nixie = (NixieTubeBlock) ntbe.getBlockState().getBlock();

        return nixie.getColor();
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyReturnValue(method = "allowsLabeling", at = @At("RETURN"))
    protected boolean allowsLabeling(boolean original, @Local(argsOnly = true) DisplayLinkContext context) {
        boolean allowLabeling;

        DisplayTarget activeTarget;

        try {
            LevelAccessor level = Minecraft.getInstance().level;

            BlockPos sourcePos = context.getSourceBlockEntity().getBlockPos();
            BlockPos targetPos = context.getTargetBlockEntity().getBlockPos();

            activeTarget = DisplayTarget.get(level, context.blockEntity().getTargetPosition());

            AABB fullTarget = activeTarget.getMultiblockBounds(level, targetPos);

            allowLabeling = !(fullTarget.intersects(new AABB(sourcePos)));
        } catch (NullPointerException e) {
            activeTarget = context.blockEntity().activeTarget;
            allowLabeling = false;
        }

        return (CIDLXConfigs.server.enhanceNixieTubeDisplaySource.get() && allowLabeling) || !(activeTarget instanceof NixieTubeDisplayTarget);
    }
}
