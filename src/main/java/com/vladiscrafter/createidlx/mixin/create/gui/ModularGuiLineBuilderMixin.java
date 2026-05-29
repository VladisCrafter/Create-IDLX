package com.vladiscrafter.createidlx.mixin.create.gui;

import com.simibubi.create.foundation.gui.ModularGuiLine;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.util.widget.*;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(ModularGuiLineBuilder.class)
public abstract class ModularGuiLineBuilderMixin implements ModularGuiLineBuilderExt {

    @Shadow private ModularGuiLine target;
    @Shadow private Font font;
    @Shadow private int x;
    @Shadow private int y;

    @Override
    public ModularGuiLineBuilder createidlx$addTimerScrollInput(int x, int width, BiConsumer<ScrollInput, Label> inputTransform,
                                                String dataKey) {
        TimerScrollInput input = new TimerScrollInput(x + this.x, y - 4, width, 18);
        createidlx$addScrollInput(input, inputTransform, dataKey);
        return (ModularGuiLineBuilder) (Object) this;
    }

    @Override
    public ModularGuiLineBuilder createidlx$addBinaryScrollInput(int x, int width, BiConsumer<SelectionScrollInput, Label> inputTransform,
                                                                 String dataKey) {
        BinaryScrollInput input = new BinaryScrollInput(x + this.x, y - 4, width, 18);
        createidlx$addScrollInput(input, inputTransform, dataKey);
        return (ModularGuiLineBuilder) (Object) this;
    }

    private <T extends ScrollInput> void createidlx$addScrollInput(T input, BiConsumer<T, Label> inputTransform,
                                                                   String dataKey) {
        Label label = new Label(input.getX() + 5, y, CommonComponents.EMPTY);
        label.withShadow();
        inputTransform.accept(input, label);
        input.writingTo(label);
        target.add(Pair.of(label, "Dummy"));
        target.add(Pair.of(input, dataKey));
    }

    @Unique
    public ModularGuiLineBuilder createidlx$addLengthUnrestrainedTextInput(int x, int width, BiConsumer<EditBox, TooltipArea> inputTransform,
                                                                           String dataKey) {
        EditBox input = new ExpandedEditBox(font, x + this.x + 5, y, width - 9, 8, null, CommonComponents.EMPTY);
        input.setBordered(false);
        input.setTextColor(0xffffff);
        input.setFocused(false);
        input.mouseClicked(0, 0, 0);
        TooltipArea tooltipArea = new TooltipArea(this.x + x, y - 4, width, 18);
        inputTransform.accept(input, tooltipArea);
        target.add(Pair.of(input, dataKey));
        target.add(Pair.of(tooltipArea, "Dummy"));
        return (ModularGuiLineBuilder) (Object) this;
    }

    @Redirect(method = "addSelectionScrollInput", at = @At(value = "NEW",
            target = "Lcom/simibubi/create/foundation/gui/widget/SelectionScrollInput;"))
    private SelectionScrollInput createidlx$replaceSelectionScrollInput(int x, int y, int width, int height) {
        if (CreateIDLXGuiContext.isInSourceConfig() && CIDLXConfigs.client.truncateOverflowingStrings.get()) {
            return new InBoundsSelectionScrollInput(x, y, width, height, false, false);
        }
        return new SelectionScrollInput(x, y, width, height);
    }
}