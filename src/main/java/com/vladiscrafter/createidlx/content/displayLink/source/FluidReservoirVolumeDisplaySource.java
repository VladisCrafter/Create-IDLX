package com.vladiscrafter.createidlx.content.displayLink.source;

import com.simibubi.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import com.simibubi.create.content.fluids.hosePulley.HosePulleyFluidHandler;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.mixin.accessor.create.HosePulleyBlockEntityAccessor;
import com.vladiscrafter.createidlx.util.bridge.HosePulleyFluidReservoirVolumeHolder;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public class FluidReservoirVolumeDisplaySource extends NumericSingleLineDisplaySource {
	@Override
	protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof HosePulleyBlockEntity hpbe))
			return EMPTY_LINE;

        HosePulleyFluidHandler handler = ((HosePulleyBlockEntityAccessor) hpbe).createidlx$getHandler();
        if (!(handler instanceof HosePulleyFluidReservoirVolumeHolder holder))
            return EMPTY_LINE;

		Pair<Integer, Integer> reservoirVolumeUnpacked = holder.createidlx$getReservoirVolume(hpbe.getLevel());
        int reservoirVolumeAll = reservoirVolumeUnpacked.getLeft();
        int reservoirVolumeSources = reservoirVolumeUnpacked.getRight();

        boolean sourceOnly = context.sourceConfig().getBoolean("SourceOnly");
        int reservoirVolume = !sourceOnly ? reservoirVolumeAll : reservoirVolumeSources;

        int threshold = AllConfigs.server().fluids.hosePulleyBlockThreshold.get();
        int mode = getMode(context);
        if (reservoirVolumeAll >= threshold) return switch (mode) {
            case 0 -> CreateIDLX.translate("display_source.fluid_reservoir_volume.comparison_template", Math.min(reservoirVolume, threshold));
            case 1 -> CreateIDLX.translate("display_source.fluid_reservoir_volume.infinity_template");
            case 2 -> CreateLang.translateDirect("hint.hose_pulley.title");
            default -> EMPTY_LINE;
        };

        return Component.literal(String.valueOf(reservoirVolume));
	}

    private int getMode(DisplayLinkContext context) {
        return context.sourceConfig()
                .getInt("Mode");
    }

    @Override
    protected boolean allowsLabeling(DisplayLinkContext context) {
        return true;
    }

	@Override
	protected String getTranslationKey() {
		return "fluid_reservoir_volume";
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) return;

        builder.addSelectionScrollInput(0, 116, (ssi, l) -> {
            ssi.forOptions(CreateIDLX.translatedOptions("display_source.fluid_reservoir_volume",
                            "comparison", "infinity", "words"))
                    .titled(CreateIDLX.translate("display_source.fluid_reservoir_volume.bottomless_display"));
        }, "Mode");

        ((ModularGuiLineBuilderExt) builder).createidlx$addBinaryScrollInput(120, 17, (ssi, l) -> {
            ssi.titled(CreateIDLX.translate("display_source.fluid_reservoir_volume.source_only"))
                    .setState(1);
        }, "SourceOnly");
    }
}

