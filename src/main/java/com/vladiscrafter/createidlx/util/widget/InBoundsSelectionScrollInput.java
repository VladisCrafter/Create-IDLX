package com.vladiscrafter.createidlx.util.widget;

import java.util.List;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.gui.MarqueeEffectTruncatedLabel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;

public class InBoundsSelectionScrollInput extends SelectionScrollInput {

    private final boolean truncateOverflowingStrings = CIDLXConfigs.client.truncateOverflowingStrings.get();
    private final boolean showTooltipForSingleOptionSelector = CIDLXConfigs.client.showTooltipForSingleOptionSelector.get();


    private List<Component> createidlx$options = List.of();
    private long createidlx$lastStateChangeMillis = Util.getMillis();
    private int createidlx$lastRenderedState = Integer.MIN_VALUE;

    private final boolean createidlx$isSourceTypeSelector;
    private final boolean createidlx$isSingleOption;

    public InBoundsSelectionScrollInput(int x, int y, int width, int height, boolean isSourceTypeSelector, boolean isSingleOption) {
        super(x, y, width, height);
        this.createidlx$isSourceTypeSelector = isSourceTypeSelector;
        this.createidlx$isSingleOption = isSingleOption;
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

        MarqueeEffectTruncatedLabel.render(graphics, Minecraft.getInstance().font, text, getX() + 4,
                getY() + (height - 8) / 2, width, createidlx$isSourceTypeSelector, createidlx$lastStateChangeMillis);
    }

    @Override
    protected void updateTooltip() {
        toolTip.clear();
        if (title == null || (createidlx$isSingleOption && truncateOverflowingStrings && !showTooltipForSingleOptionSelector))
            return;
        toolTip.add(title.plainCopy()
                .withStyle(s -> s.withColor(HEADER_RGB.getRGB())));
        int min = Math.min(this.max - 16, state - 7);
        int max = Math.max(this.min + 16, state + 8);
        min = Math.max(min, this.min);
        max = Math.min(max, this.max);
        if (this.min + 1 == min)
            min--;
        if (min > this.min) {
            toolTip.add(Component.literal("> ...")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (this.max - 1 == max)
            max++;
        for (int i = min; i < max; i++) {
            if (i == state)
                toolTip.add(Component.empty()
                        .append("-> ")
                        .append(options.get(i))
                        .withStyle(ChatFormatting.WHITE));
            else
                toolTip.add(Component.empty()
                        .append("> ")
                        .append(options.get(i))
                        .withStyle(ChatFormatting.GRAY));
        }
        if (max < this.max) {
            toolTip.add(Component.literal("> ...")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (!createidlx$isSingleOption) {
            if (hint != null)
                toolTip.add(hint.plainCopy()
                        .withStyle(s -> s.withColor(HINT_RGB.getRGB())));
            toolTip.add(scrollToModify.plainCopy()
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        }
    }
}