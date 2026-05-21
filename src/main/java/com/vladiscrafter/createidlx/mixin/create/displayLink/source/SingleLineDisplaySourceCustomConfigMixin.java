package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@OnlyIn(Dist.CLIENT)
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceCustomConfigMixin {

    /**
     * Override this method in subclasses to add custom configuration widgets.
     * Called after the standard configuration widgets have been added.
     */
    public void createidlx$addCustomConfigWidgets(ModularGuiLineBuilder builder, DisplayLinkContext context) {
        // Default: no custom widgets. Subclasses can override this method.
    }
}

