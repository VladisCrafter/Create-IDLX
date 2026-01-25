package com.vladiscrafter.createidlx.mixin;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkScreen;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DisplayLinkScreen.class)
public abstract class DisplayLinkScreenMixin extends AbstractSimiScreen {
//    @Unique
//    private IconButton createidlx$specifierHelpButton;

    @Shadow private List<DisplaySource> sources;

    @Inject(method = "initGathererSourceSubOptions", at = @At("TAIL"))
    private void createidlx$injectGuideButtons(int i, CallbackInfo ci) {
        boolean isPlaceholdersGuideButtonEnabled = CIDLXConfigs.client.enablePlaceholdersGuideButton.get();
        if (!isPlaceholdersGuideButtonEnabled) return;

        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();

        if (i < 0 || i >= sources.size()) return;

        DisplaySource source = sources.get(i);
        if (!(source instanceof SingleLineDisplaySource)) return;

        IconButton placeholdersGuideButton = new IconButton(guiLeft + 36, guiTop + 46, 16, 16, AllIcons./*I_ACTIVE*/I_PASSIVE);
        placeholdersGuideButton.active = false;
        placeholdersGuideButton.withCallback(() -> {});

        placeholdersGuideButton.getToolTip().addAll(List.of(
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_header")
                        .withStyle(s -> s.withColor(0x5391E1)),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_1")
                        .withStyle(ChatFormatting.GRAY),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_2")
                        .withStyle(ChatFormatting.GRAY),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_3")
                        .withStyle(ChatFormatting.GRAY),
                CreateIDLX.translate("gui.display_link.placeholders_tooltip_4",
                                ((isDollarSignSpecifierEnabled && isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.both") :
                                        (!isDollarSignSpecifierEnabled && isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.brackets_only") :
                                                ((isDollarSignSpecifierEnabled && !isBracketsSpecifierEnabled) ? CreateIDLX.translate("gui.display_link.active_placeholder.dollar_only") :
                                                        CreateIDLX.translate("gui.display_link.active_placeholder.none")))
                                .withStyle(ChatFormatting.UNDERLINE))
                        .withStyle(ChatFormatting.DARK_GRAY)
        ));

        this.addRenderableWidget(placeholdersGuideButton);
//        this.createidlx$specifierHelpButton = placeholdersGuideButton;
    }

}
