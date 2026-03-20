package com.vladiscrafter.createidlx.mixin.create;

import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.util.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.util.InBoundsSelectionScrollInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModularGuiLineBuilder.class)
public abstract class ModularGuiLineBuilderMixin {

    @Redirect( method = "addSelectionScrollInput", at = @At(value = "NEW",
            target = "Lcom/simibubi/create/foundation/gui/widget/SelectionScrollInput;"))
    private SelectionScrollInput createidlx$replaceSelectionScrollInput(int x, int y, int width, int height) {
        if (CreateIDLXGuiContext.isInSourceConfig()) {
            return new InBoundsSelectionScrollInput(x, y, width, height, false);
        }
        return new SelectionScrollInput(x, y, width, height);
    }
}