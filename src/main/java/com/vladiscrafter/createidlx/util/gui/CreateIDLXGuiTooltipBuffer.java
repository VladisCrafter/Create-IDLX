package com.vladiscrafter.createidlx.util.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class CreateIDLXGuiTooltipBuffer {
    private CreateIDLXGuiTooltipBuffer() {}

    private static List<Component> labelingTextBoxTooltip = List.of();
    private static List<Component> targetWidgetTooltip = List.of();

    private static List<Component> deferredTooltip = List.of();
    private static int deferredMouseX;
    private static int deferredMouseY;
    private static boolean hasDeferredTooltip;

    public static void registerLabelingTextBoxTooltip(List<Component> tooltip) {
        labelingTextBoxTooltip = List.copyOf(tooltip);
    }

    public static void registerTargetWidgetTooltip(List<Component> tooltip) {
        targetWidgetTooltip = List.copyOf(tooltip);
    }

    public static boolean isLabelingTextBoxTooltip(List<Component> tooltip) {
        return !labelingTextBoxTooltip.isEmpty() && labelingTextBoxTooltip.equals(tooltip);
    }

    public static boolean isTargetWidgetTooltip(List<Component> tooltip) {
        return !targetWidgetTooltip.isEmpty() && targetWidgetTooltip.equals(tooltip);
    }

    public static void defer(List<Component> tooltip, int mouseX, int mouseY) {
        deferredTooltip = List.copyOf(tooltip);
        deferredMouseX = mouseX;
        deferredMouseY = mouseY;
        hasDeferredTooltip = true;
    }

    public static void renderDeferred(GuiGraphics graphics) {
        if (!hasDeferredTooltip || deferredTooltip.isEmpty()) return;

        graphics.renderComponentTooltip(Minecraft.getInstance().font, deferredTooltip, deferredMouseX, deferredMouseY);
        clearDeferred();
    }

    public static void clearDeferred() {
        deferredTooltip = List.of();
        hasDeferredTooltip = false;
    }
}
