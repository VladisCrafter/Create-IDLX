package com.vladiscrafter.createidlx.util.widget;

import java.util.List;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;

public class InBoundsSelectionScrollInput extends SelectionScrollInput {

    private final boolean addMarqueeEffectToTruncatedStrings = CIDLXConfigs.client.addMarqueeEffectToTruncatedStrings.get();
    private final long fixedCharTravelTime = (long) CIDLXConfigs.client.fixedCharTravelTime.get();
    private final long fixedStringTravelTime = (long) ((int) (CIDLXConfigs.client.fixedStringTravelTime.get() * 1000));
    private final long maximalStringTravelTime = (long) ((int) (CIDLXConfigs.client.maximalStringTravelTime.get() * 1000));
    private final long minimalStringTravelTime = (long) ((int) (CIDLXConfigs.client.minimalStringTravelTime.get() * 1000));
    private final long stringPauseTime = (long) ((int) (CIDLXConfigs.client.stringPauseTime.get() * 1000));

    private final boolean isStringTravelTimeFixed = fixedStringTravelTime > 0;
    private final boolean isMaximalStringTravelTimeSpecified = maximalStringTravelTime > 0;
    private final boolean isMinimalStringTravelTimeSpecified = minimalStringTravelTime > 0;


    private List<Component> createidlx$options = List.of();
    private long createidlx$lastStateChangeMillis = Util.getMillis();
    private int createidlx$lastRenderedState = Integer.MIN_VALUE;

    private final boolean createidlx$isSourceTypeSelector;

    public InBoundsSelectionScrollInput(int x, int y, int width, int height, boolean isSourceTypeSelector) {
        super(x, y, width, height);
        this.createidlx$isSourceTypeSelector = isSourceTypeSelector;
    }

    @Override
    public ScrollInput forOptions(List<? extends Component> options) {
        ScrollInput forOptions = super.forOptions(options);

        this.createidlx$options = List.copyOf(options);
        this.format(i -> Component.empty());

        this.createidlx$lastRenderedState = Integer.MIN_VALUE;
        this.createidlx$lastStateChangeMillis = Util.getMillis();

        return forOptions;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (createidlx$options.isEmpty()) return;

        int state = getState();
        if (state < 0 || state >= createidlx$options.size()) return;

        if (state != createidlx$lastRenderedState) {
            createidlx$lastRenderedState = state;
            createidlx$lastStateChangeMillis = Util.getMillis();
        }

        String text = createidlx$options.get(state).getString();
        if (text.isEmpty()) return;

        var font = Minecraft.getInstance().font;
        int textWidth = font.width(text);

        int offset = 0;
        int textX = getX() + 4;
        int textY = getY() + (height - 8) / 2;

        int availableWidthForCheck = Math.max(0, width - 5);
        int availableWidth = createidlx$isSourceTypeSelector ? Math.max(0, width - 8) : Math.max(0, width - 10);

        if (textWidth <= availableWidthForCheck) {
            if (createidlx$isSourceTypeSelector) graphics.drawString(font, text, textX - offset, textY, 0xFFFFFF, true);
            else graphics.drawString(font, text, textX - offset + 1, textY - 1, 0xFFFFFF, true);
            return;
        }

        long elapsed = Util.getMillis() - createidlx$lastStateChangeMillis;

        int overflow = textWidth - availableWidth;

        long pauseMillis = Math.max(0L, stringPauseTime);
        long travelMillis = Math.max(0L,
                isStringTravelTimeFixed ? fixedStringTravelTime :
                        isMinimalStringTravelTimeSpecified && isMaximalStringTravelTimeSpecified ? Math.min(Math.max(minimalStringTravelTime, fixedCharTravelTime * overflow), maximalStringTravelTime) :
                                isMinimalStringTravelTimeSpecified ? Math.max(minimalStringTravelTime, fixedCharTravelTime * overflow) :
                                        isMaximalStringTravelTimeSpecified ? Math.min(fixedCharTravelTime * overflow, maximalStringTravelTime) :
                                                fixedCharTravelTime * overflow);

        long period = (pauseMillis + travelMillis) * 2;
        long t = addMarqueeEffectToTruncatedStrings ? elapsed % period : 0;

        if (t < pauseMillis) {
            offset = 0;
        } else if (t < pauseMillis + travelMillis) {
            double progress = (double) (t - pauseMillis) / (double) travelMillis;
            offset = (int) Math.round(overflow * progress);
        } else if (t < pauseMillis + travelMillis + pauseMillis) {
            offset = overflow;
        } else {
            double progress = (double) (t - pauseMillis - travelMillis - pauseMillis) / (double) travelMillis;
            offset = overflow - (int) Math.round(overflow * progress);
        }

        int minX = createidlx$isSourceTypeSelector ? textX - 4 : textX - 3;
        int maxX = createidlx$isSourceTypeSelector ? textX + availableWidth + 4 : textX + availableWidth + 5;
        int stringX = createidlx$isSourceTypeSelector ? textX - offset : textX - offset + 1;
        int stringY = createidlx$isSourceTypeSelector ? textY : textY - 1;

        graphics.enableScissor(minX, getY(), maxX, getY() + height);
        try {
            graphics.drawString(font, text, stringX, stringY, 0xFFFFFF, true);
        } finally {
            graphics.disableScissor();
        }
    }
}