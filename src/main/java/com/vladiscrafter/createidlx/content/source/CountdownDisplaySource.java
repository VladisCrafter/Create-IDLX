package com.vladiscrafter.createidlx.content.source;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.SingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.source.TimeOfDayDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplaySection;

import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import com.vladiscrafter.createidlx.CreateIDLX;
import com.vladiscrafter.createidlx.util.widget.ModularGuiLineBuilderExt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Locale;

public class CountdownDisplaySource extends SingleLineDisplaySource {

    @Override
    protected MutableComponent provideLine(DisplayLinkContext context, DisplayTargetStats stats) {
        if (!(context.getSourceBlockEntity() instanceof CuckooClockBlockEntity ccbe))
            return Component.literal("-:--");
        if (ccbe.getSpeed() == 0)
            return Component.literal("-:--");


        int lastTotal = context.sourceConfig().getInt("LastCountdownTime");
        int total = context.sourceConfig().getInt("CountdownTime");
        boolean isFinished = context.sourceConfig().getBoolean("IsCountdownFinished");

        if (lastTotal != total) {
            context.sourceConfig().putInt("LastCountdownTime", total);
            onSignalReset(context);
            isFinished = false;
        }

        if (!context.sourceConfig().contains("CountdownStartTime")) onSignalReset(context);

        long started = context.sourceConfig().getLong("CountdownStartTime");
        long current = context.blockEntity().getLevel().getGameTime();

        String finishLabel = context.sourceConfig().getString("FinishLabel");
        boolean overrideLabelOnFinish = context.sourceConfig().getInt("OverrideLabelOnFinish") == 1;

        if (isFinished && finishLabel.isEmpty()) return Component.literal("0:00");

        int diff = (int) (current - started);

        MutableComponent component;

        if (diff <= total) {
            int minutes = ((total - diff) / 60 / 20) % 60;
            int seconds = ((total - diff) / 20) % 60;

            component = Component.literal(String.format(Locale.ROOT, "%01d:%02d", minutes, seconds));
        } else {
            component = !overrideLabelOnFinish ? Component.literal(finishLabel) : Component.literal("0:00");
            context.sourceConfig().putBoolean("IsCountdownFinished", true);;
        }
        return component;
    }

    @Override
    public void onSignalReset(DisplayLinkContext context) {
        context.sourceConfig()
                .putLong("CountdownStartTime", context.blockEntity()
                        .getLevel()
                        .getGameTime());
        context.sourceConfig().putBoolean("IsCountdownFinished", false);
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
        return "Instant";
    }

    @Override
    protected FlapDisplaySection createSectionForValue(DisplayLinkContext context, int size) {
        return new FlapDisplaySection(size * FlapDisplaySection.MONOSPACE, "instant", false, false);
    }

    @Override
    protected String getTranslationKey() {
        return "countdown";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder,
                                         boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) return;

        ((ModularGuiLineBuilderExt) builder).createidlx$addTimerScrollInput(0, 40, (si, l) -> {
            si.titled(CreateIDLX.translate("display_source.countdown.time"))
                    .calling(v -> onSignalReset(context));
        }, "CountdownTime");

        builder.addTextInput(44, 72, (e, t) -> {
            e.setValue("");
            t.withTooltip(ImmutableList.of(CreateIDLX.translate("display_source.countdown.finish_label")
                            .withStyle(s -> s.withColor(0x5391E1)),
                    CreateLang.translateDirect("gui.schedule.lmb_edit")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
        }, "FinishLabel");

        ((ModularGuiLineBuilderExt) builder).createidlx$addBinaryScrollInput(120, 17, (ssi, l) -> {
            ssi.titled(CreateIDLX.translate("display_source.countdown.overlap_label"));
        }, "OverrideLabelOnFinish");
    }
}
