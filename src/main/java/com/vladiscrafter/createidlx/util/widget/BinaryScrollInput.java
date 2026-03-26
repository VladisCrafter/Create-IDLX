package com.vladiscrafter.createidlx.util.widget;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.CreateIDLX;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Function;

public class BinaryScrollInput extends SelectionScrollInput {

    protected final Component scrollOrClickToToggle = CreateIDLX.translate("gui.binaryScrollInput.scrollOrClickToToggle");
    Function<ScrollValueBehaviour.StepContext, Integer> step;

    public BinaryScrollInput(int x, int y, int width, int height) {
        super(x, y, width, height);
        state = 1;
        min = 0;
        max = 1;
        step = standardStep();

        forOptions(List.of(
                CreateIDLX.translate("gui.binaryScrollInput.false"),
                CreateIDLX.translate("gui.binaryScrollInput.true")
        ));
        setState(1);
    }

    private void toggle() {
        setState(getState() == 0 ? 1 : 0);
        if (!soundPlayed)
            Minecraft.getInstance()
                    .getSoundManager()
                    .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                            (getState() == 1 ? 1.5f : 0.9f) + 0.1f * (state - min) / (max - min)));
        soundPlayed = true;
        onChanged();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double pScrollX, double pScrollY) {
        if (!inverted)
            pScrollY *= -1;

        ScrollValueBehaviour.StepContext context = new ScrollValueBehaviour.StepContext();
        context.currentValue = state;
        context.forward = pScrollY > 0;

        int priorState = state;
        int step = (int) Math.signum(pScrollY) * this.step.apply(context);

        state += step;

        clampState();

        if (priorState != state) {
            if (!soundPlayed)
                Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                                (getState() == 1 ? 1.5f : 0.9f) + 0.1f * (state - min) / (max - min)));
            soundPlayed = true;
            onChanged();
        }

        return priorState != state;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !visible || !active || !isMouseOver(mouseX, mouseY))
            return false;

        toggle();
        return true;
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
        toolTip.add(scrollOrClickToToggle.plainCopy()
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}