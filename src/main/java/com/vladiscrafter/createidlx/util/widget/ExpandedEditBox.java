package com.vladiscrafter.createidlx.util.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class ExpandedEditBox extends EditBox {
    public ExpandedEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component message) {
        this(font, x, y, width, height, editBox, message, Integer.MAX_VALUE);
    }

    public ExpandedEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component message, int maxLength) {
        super(font, x, y, width, height, editBox, message);
        super.setMaxLength(maxLength);
    }
}
