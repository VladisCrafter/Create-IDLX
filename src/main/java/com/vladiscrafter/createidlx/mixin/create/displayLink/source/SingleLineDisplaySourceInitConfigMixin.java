package com.vladiscrafter.createidlx.mixin.create.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin injects custom configuration widgets after the standard widgets
 * have been added during the initialization of DisplayLink configuration UI.
 */
@Pseudo
@OnlyIn(Dist.CLIENT)
@Mixin(SingleLineDisplaySource.class)
public abstract class SingleLineDisplaySourceInitConfigMixin {

    @Inject(
            method = "initConfigurationWidgets",
            at = @At("TAIL"),
            require = 0
    )
    private void createidlx$callCustomConfigWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                                    boolean isFirstLine, CallbackInfo ci) {
        // Call custom widgets for non-first lines
        if (isFirstLine) return;
        
        try {
            var method = ((Object) this).getClass().getDeclaredMethod("addCustomConfigWidgets",
                    ModularGuiLineBuilder.class, DisplayLinkContext.class);
            method.setAccessible(true);
            method.invoke(this, builder, context);
        } catch (NoSuchMethodException e) {
            // Method doesn't exist, which is fine for Display Sources without custom widgets
        } catch (Exception e) {
            // Silently ignore other exceptions
        }
    }
}
