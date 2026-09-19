package com.vladiscrafter.createidlx.util.widget.rle;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.network.chat.Component;

public class RLECaptureGroupScrollInput extends ScrollInput {

    protected final Component shiftToCycleType = Component.literal("Shift to Cycle Capture Group Types");
    protected final Component ctrlToCycleRounding = Component.literal("Ctrl to Cycle Rounding Options");

    public RLECaptureGroupScrollInput(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }
}
