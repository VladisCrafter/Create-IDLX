package com.vladiscrafter.createidlx.util.widget;

import java.util.Locale;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.vladiscrafter.createidlx.CreateIDLX;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;

public class TimerScrollInput extends ScrollInput {

    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    protected final Component scrollToModifySeconds = CreateIDLX.translate("gui.timerScrollInput.scrollToModifySeconds");
    protected final Component ctrlScrollsMinutes = CreateIDLX.translate("gui.timerScrollInput.ctrlScrollsMinutes");

    public TimerScrollInput(int x, int y, int width, int height) {
        super(x, y, width, height);

        withRange(0, 1199981);
        format(this::formatTime);
    }

    @Override
    public ScrollInput setState(int state) {
        return super.setState(Math.max(0, state));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!visible || !active || !isMouseOver(mouseX, mouseY))
            return false;

        int currentTicks = getState();
        int totalSeconds = currentTicks / TICKS_PER_SECOND;

        int minutes = totalSeconds / SECONDS_PER_MINUTE;
        int seconds = totalSeconds % SECONDS_PER_MINUTE;

        int direction = delta > 0 ? 1 : -1;

        if (AllKeys.ctrlDown()) {
            int step = AllKeys.shiftDown() ? 10 : 1;
            minutes = Math.min(Math.max(0, minutes + direction * step), 999);
        } else {
            int step = AllKeys.shiftDown() ? 10 : 1;
            int newTotalSeconds = minutes * SECONDS_PER_MINUTE + seconds + direction * step;

            int maxTotalSeconds = 999 * SECONDS_PER_MINUTE + 59;
            if (newTotalSeconds > maxTotalSeconds)
                newTotalSeconds = maxTotalSeconds;

            if (newTotalSeconds < 0)
                newTotalSeconds = 0;

            minutes = newTotalSeconds / SECONDS_PER_MINUTE;
            seconds = newTotalSeconds % SECONDS_PER_MINUTE;

        }

        int nextTicks = (minutes * SECONDS_PER_MINUTE + seconds) * TICKS_PER_SECOND;
        setState(nextTicks);

        if (currentTicks != nextTicks) {
            if (!soundPlayed)
                Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                                (!AllKeys.ctrlDown() ? 1.5f : 0.9f) + 0.1f * (state - min) / (max - min)));
            soundPlayed = true;
            onChanged();
            return true;
        }

        return false;
    }

    @Override
    protected void updateTooltip() {
        toolTip.clear();
        if (title == null)
            return;
        toolTip.add(title.plainCopy()
                .withStyle(s -> s.withColor(HEADER_RGB.getRGB())));
        if (hint != null)
            toolTip.add(hint.plainCopy()
                    .withStyle(s -> s.withColor(HINT_RGB.getRGB())));
        toolTip.add(scrollToModifySeconds.plainCopy()
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        toolTip.add(ctrlScrollsMinutes.plainCopy()
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        toolTip.add(shiftScrollsFaster.plainCopy()
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
    }

    private Component formatTime(int ticks) {
        int totalSeconds = Math.max(0, ticks / TICKS_PER_SECOND);
        int minutes = totalSeconds / SECONDS_PER_MINUTE;
        int seconds = totalSeconds % SECONDS_PER_MINUTE;

        return Component.literal(String.format(Locale.ROOT, "%01d:%02d", minutes, seconds));
    }
}