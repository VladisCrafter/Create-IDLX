package com.vladiscrafter.createidlx.mixin.create;

import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import com.vladiscrafter.createidlx.content.source.CountdownDisplaySource;
import com.vladiscrafter.createidlx.util.gui.CreateIDLXGuiContext;
import com.vladiscrafter.createidlx.util.widget.InBoundsSelectionScrollInput;
import com.vladiscrafter.createidlx.util.widget.TimerScrollInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModularGuiLineBuilder.class)
public abstract class ModularGuiLineBuilderMixin {

    @Redirect(method = "addSelectionScrollInput", at = @At(value = "NEW",
            target = "Lcom/simibubi/create/foundation/gui/widget/SelectionScrollInput;"), remap = false)
    private SelectionScrollInput createidlx$replaceSelectionScrollInput(int x, int y, int width, int height) {
        if (CreateIDLXGuiContext.isInSourceConfig() && CIDLXConfigs.client.truncateOverflowingStrings.get()) {
            return new InBoundsSelectionScrollInput(x, y, width, height, false);
        }
        return new SelectionScrollInput(x, y, width, height);
    }

    @Redirect(method = "addScrollInput", at = @At(value = "NEW",
            target = "Lcom/simibubi/create/foundation/gui/widget/ScrollInput;"), remap = false)
    private ScrollInput createidlx$replaceScrollInput(int x, int y, int width, int height) {
        if(CreateIDLXGuiContext.currentSource() instanceof CountdownDisplaySource) {
            return new TimerScrollInput(x, y, width, height);
        }
        return new ScrollInput(x, y, width, height);
    }
}