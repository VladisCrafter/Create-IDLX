package com.vladiscrafter.createidlx.util.widget;

import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;

import java.util.function.BiConsumer;

public interface ModularGuiLineBuilderExt {
    ModularGuiLineBuilder createidlx$addTimerScrollInput(int x, int width, BiConsumer<ScrollInput, Label> inputTransform, String dataKey);
    ModularGuiLineBuilder createidlx$addBinaryScrollInput(int x, int width, BiConsumer<SelectionScrollInput, Label> inputTransform, String dataKey);
}