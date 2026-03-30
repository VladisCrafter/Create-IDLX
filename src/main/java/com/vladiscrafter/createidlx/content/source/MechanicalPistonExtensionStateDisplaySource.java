package com.vladiscrafter.createidlx.content.source;

import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.PercentOrProgressBarDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.mixin.create.piston.MechanicalPistonBlockEntityAccessor;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.text.DecimalFormat;

public class MechanicalPistonExtensionStateDisplaySource extends PercentOrProgressBarDisplaySource {
    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        Float rawProgress = this.getProgress(context);
        int mode = getMode(context);
        
        if (rawProgress == null && (mode == 0 || mode == 1 || mode == 2))
            return EMPTY_LINE;

        if (progressBarActive(context)) return super.provideLine(context, stats);
        if (!progressBarActive(context) && (mode == 1 || mode == 2)) return formatNumeric(context, rawProgress);

        if (!(context.getSourceBlockEntity() instanceof MechanicalPistonBlockEntity mpbe)) {
            return EMPTY_LINE;
        }

        float rawTotal = (float) ((MechanicalPistonBlockEntityAccessor) mpbe).$createidlx$getExtensionLength();
        float rawUsed = mpbe.offset;
        float rawLeft = rawTotal - rawUsed;

        boolean roundFloats = context.sourceConfig().getBoolean("RoundFloats");

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        String used = String.valueOf(roundFloats ? Math.round(rawUsed) : df.format(rawUsed));
        String total = String.valueOf(roundFloats ? Math.round(rawTotal) : df.format(rawTotal));
        String left = String.valueOf(roundFloats ? Math.round(rawLeft) : df.format(rawLeft));


        return switch (mode) {
            case 3 -> Component.literal(used);
            case 4 -> Component.literal(left);
            case 5 -> Component.literal(total);
            case 6 -> CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template", used, total);
            case 7 -> CreateIDLX.translate("display_source.mechanical_piston_extension_state.ratio_template", left, total);
            default -> EMPTY_LINE;
        };
    }

    @Override
    protected MutableComponent formatNumeric(DisplayLinkContext context, Float currentLevel) {
        return Component.literal(Mth.clamp(Math.round(currentLevel * 100), 0, 100) + "%");
    }

    private int getMode(DisplayLinkContext context) {
        return context.sourceConfig()
                .getInt("Mode");
    }

    @Override
    protected Float getProgress(DisplayLinkContext context) {
        if (!(context.getSourceBlockEntity() instanceof MechanicalPistonBlockEntity mpbe))
            return null;

        int extensionLength = ((MechanicalPistonBlockEntityAccessor) mpbe).$createidlx$getExtensionLength();
        float offset = mpbe.offset;

        if (extensionLength == 0) return 0f;

        return switch (getMode(context)) {
            case 0, 2 -> offset / extensionLength;
            case 1, 3 -> 1f - (offset / extensionLength);
            default -> 0f;
        };
    }

    @Override
    protected boolean progressBarActive(DisplayLinkContext context) {
        return getMode(context) == 0;
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 20;
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

    @Override
    protected String getFlapDisplayLayoutName(DisplayLinkContext context) {
        return !progressBarActive(context) ? "Instant" : super.getFlapDisplayLayoutName(context);
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return !progressBarActive(context)
                ? new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false)
                : super.createSectionForValue(context, size);
    }

    @Override
    protected String getTranslationKey() {
        return "mechanical_piston_extension_state";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine)
            return;

        builder.addSelectionScrollInput(0, 116, (ssi, l) -> {
            ssi.forOptions(CreateIDLX.translatedOptions("display_source.mechanical_piston_extension_state",
                            "progress_bar", "used_percent", "spare_percent", "used", "spare", "total", "used_n_total", "spare_n_total"))
                    .titled(CreateIDLX.translate("display_source.mechanical_piston_extension_state.display"));
        }, "Mode");

        ((ModularGuiLineBuilderExt) builder).createidlx$addBinaryScrollInput(120, 17, (ssi, l) -> {
            ssi.titled(CreateIDLX.translate("display_source.mechanical_piston_extension_state.round_floats"))
                    .setState(1);
        }, "RoundFloats");
    }
}
