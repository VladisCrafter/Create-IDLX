package com.vladiscrafter.createidlx.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CreateIDLXCustomBlockOutlineRenderer {
    public enum Type { DISPLAY_LINK, DISPLAY_LINK_INVALID }

    public static void renderShape(VoxelShape s, PoseStack ms, VertexConsumer vb, Type type) {
        PoseStack.Pose transform = ms.last();
        s.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float xDiff = (float) (x2 - x1);
            float yDiff = (float) (y2 - y1);
            float zDiff = (float) (z2 - z1);
            float length = Mth.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

            xDiff /= length;
            yDiff /= length;
            zDiff /= length;

            float r = 255f;
            float g = 255f;
            float b = 255f;

            if (type == Type.DISPLAY_LINK) {
                r = 131f;
                g = 239f;
                b = 145f;
            } else if (type == Type.DISPLAY_LINK_INVALID) {
                r = 239f;
                g = 71f;
                b = 85f;
            }

            vb.addVertex(transform.pose(), (float) x1, (float) y1, (float) z1)
                    .setColor(r / 255f, g / 255f, b / 255f, .8f)
                    .setNormal(transform.copy(), xDiff, yDiff, zDiff);
            vb.addVertex(transform.pose(), (float) x2, (float) y2, (float) z2)
                    .setColor(r / 255f, g / 255f, b / 255f, .8f)
                    .setNormal(transform.copy(), xDiff, yDiff, zDiff);

        });
    }
}
