package com.vladiscrafter.createidlx.util;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.vladiscrafter.createidlx.CreateIDLX;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateIDLXDisplaySourceUtils {

    /**
     * Safely calls addCustomConfigWidgets method on a DisplaySource if it exists.
     * This method uses reflection to avoid class loading issues and to support
     * dynamic Display Sources.
     */
    public static void callAddCustomConfigWidgets(DisplaySource source, ModularGuiLineBuilder builder, DisplayLinkContext context) {
        try {
            var method = source.getClass().getDeclaredMethod("addCustomConfigWidgets", ModularGuiLineBuilder.class, DisplayLinkContext.class);
            method.setAccessible(true);
            method.invoke(source, builder, context);
        } catch (NoSuchMethodException e) {
            // Method doesn't exist, which is fine for Display Sources that don't have custom widgets
        } catch (Exception e) {
            CreateIDLX.LOGGER.warn("Failed to call addCustomConfigWidgets on {}: {}", source.getName(), e.getMessage());
        }
    }
}


