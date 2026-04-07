package com.vladiscrafter.createidlx.util.gui;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CreateIDLXGuiTooltipBuffer {
    private CreateIDLXGuiTooltipBuffer() {}

    private enum Type { WIDGET, TOOLTIP }

    private record Entry(Type type, AbstractSimiWidget widget, List<Component> tooltip) {
        boolean matches(AbstractSimiWidget self, List<Component> currentTooltip) {
            return switch (type) {
                case WIDGET -> widget != null && widget == self;
                case TOOLTIP -> tooltip != null && !tooltip.isEmpty() && tooltip.equals(currentTooltip);
            };
        }
    }

    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private static List<Component> deferredTooltip = List.of();
    private static int deferredMouseX;
    private static int deferredMouseY;
    private static boolean hasDeferredTooltip;

    public static void registerWidget(String id, AbstractSimiWidget widget) {
        if (id == null || widget == null) return;
        ENTRIES.put(id, new Entry(Type.WIDGET, widget, null));
    }

    public static void registerTooltip(String id, List<Component> tooltip) {
        if (id == null || tooltip == null) return;
        ENTRIES.put(id, new Entry(Type.TOOLTIP, null, List.copyOf(tooltip)));
    }

    public static boolean shouldDefer(AbstractSimiWidget self, List<Component> currentTooltip) {
        for (Entry entry : ENTRIES.values()) {
            if (entry.matches(self, currentTooltip)) {
                return true;
            }
        }
        return false;
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