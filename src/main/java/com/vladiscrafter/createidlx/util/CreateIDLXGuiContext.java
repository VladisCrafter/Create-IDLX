package com.vladiscrafter.createidlx.util;

import com.simibubi.create.api.behaviour.display.DisplaySource;

public final class CreateIDLXGuiContext {
    private CreateIDLXGuiContext() {}

    private static final ThreadLocal<DisplaySource> CURRENT_DISPLAY_SOURCE = new ThreadLocal<>();

    public static void enter(DisplaySource source) { CURRENT_DISPLAY_SOURCE.set(source); }

    public static void exit() { CURRENT_DISPLAY_SOURCE.remove(); }

    public static boolean isInSourceConfig() { return CURRENT_DISPLAY_SOURCE.get() != null; }

    public static DisplaySource currentSource() { return CURRENT_DISPLAY_SOURCE.get(); }
}