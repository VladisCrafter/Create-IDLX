package com.vladiscrafter.createidlx.content.clipboard;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.vladiscrafter.createidlx.util.CreateIDLXCustomBlockOutlineRenderer;
import com.vladiscrafter.createidlx.util.CreateIDLXCustomBlockOutlineRenderer.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent.Block;

@EventBusSubscriber
public class ClipboardDisplayLinkBlockOutlineHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void drawCustomBlockSelection(Block event) {
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult target = event.getTarget();
        BlockPos pos = target.getBlockPos();
        BlockState blockstate = mc.level.getBlockState(pos);
        BlockEntity displayLink = mc.level.getBlockEntity(pos);

        if (mc.player == null || mc.player.isSpectator())
            return;
        if (!mc.level.getWorldBorder()
                .isWithinBounds(pos))
            return;
        if (!AllBlocks.CLIPBOARD.isIn(mc.player.getMainHandItem()))
            return;
        if (!(displayLink instanceof SmartBlockEntity smartBE))
            return;
        if (!(displayLink instanceof DisplayLinkBlockEntity))
            return;

        VoxelShape shape = blockstate.getShape(mc.level, pos);
        if (shape.isEmpty())
            return;

        VertexConsumer vb = event.getMultiBufferSource()
                .getBuffer(RenderType.lines());
        Vec3 camPos = event.getCamera()
                .getPosition();

        PoseStack ms = event.getPoseStack();

        ms.pushPose();
        ms.translate(pos.getX() - camPos.x, pos.getY() - camPos.y, pos.getZ() - camPos.z);
        CreateIDLXCustomBlockOutlineRenderer.renderShape(shape, ms, vb,
                ((DisplayLinkBlockEntity) displayLink).activeTarget != null ? Type.DISPLAY_LINK : Type.DISPLAY_LINK_INVALID);
        event.setCanceled(true);

        ms.popPose();
    }
}
