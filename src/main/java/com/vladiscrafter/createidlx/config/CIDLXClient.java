package com.vladiscrafter.createidlx.config;

import net.createmod.catnip.config.ui.ConfigAnnotations;
import org.jetbrains.annotations.NotNull;

import static com.vladiscrafter.createidlx.content.displayLink.DisplayLinkRichLabelEditorScreen.*;

@SuppressWarnings("unused")
public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigGroup displayLinkGUI = group(1, "displayLinkGUI", "Display Link GUI");
    public final ConfigBool enableGuideWidgets = b(true, "enableGuideWidgets", Comments.enableGuideWidgets);
    public final ConfigBool enableRichLabelEditor = b(true, "enableRichLabelEditor", Comments.enableRichLabelEditor);
    public final ConfigBool enableVisualizationSettingsButtons = b(true, "enableVisualizationSettingsButtons", Comments.enableVisualizationSettingsButtons);
    public final ConfigBool alwaysShowVisualizationSettingsButtons = b(false, "alwaysShowVisualizationSettingsButtons", Comments.alwaysShowVisualizationSettingsButtons, Comments.onlyTakesEffectVisualizationButtons);

    public final ConfigGroup guideWidgetsCustomization = group(2, "guideWidgetsCustomization", "Guide Widgets Customization");
//  public final ConfigBool enableActivePlaceholdersTooltip = b(true, "enableActivePlaceholdersTooltip", Comments.enableActivePlaceholdersTooltip, Comments.onlyTakesEffectGuideWidgets);
    public final ConfigBool enableClipboardIconHighlighting = b(true, "enableClipboardIconHighlighting", Comments.enableClipboardIconHighlighting, Comments.onlyTakesEffectGuideWidgets);
    public final ConfigBool enableRedirectsToPonderScenes = b(true, "enableRedirectsToPonderScenes", Comments.enableRedirectsToPonderScenes, Comments.onlyTakesEffectGuideWidgets);

    public final ConfigGroup attachedLabelVisuals = group(2, "attachedLabelVisuals", "Attached Label Visuals");
    public final ConfigBool colorPlaceholders = b(true, "colorPlaceholders", Comments.colorPlaceholders);
    public final ConfigInt placeholdersColorsAlpha = i(0xAA, 0, 255, "placeholdersColorsAlpha", Comments.alpha, Comments.placeholdersColorsAlpha, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigInt coloringBackgroundHeightIncrease = i(0, -1, Integer.MAX_VALUE, "coloringBackgroundHeightIncrease", Comments.pixels, Comments.coloringBackgroundHeightIncrease, Comments.heightReference);

    public final ConfigGroup placeholderColoringCustomization = group(3, "placeholderColoringCustomization", "Placeholder Coloring Customization");
    public final ConfigBool colorDollarPlaceholders = b(true, "dollarPlaceholders", Comments.colorDollarPlaceholders);
    public final ConfigInt dollarPlaceholderColor = i(0xEAB444, 0, 16777215, "dollarPlaceholderColor", Comments.rgb, Comments.dollarPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorBracketsPlaceholders = b(true, "bracketsPlaceholders", Comments.colorBracketsPlaceholders);
    public final ConfigInt bracketsPlaceholderColor = i(0xC3C54F, 0, 16777215, "bracketsPlaceholderColor", Comments.rgb, Comments.bracketsPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorOriginalTrimmingPlaceholders = b(true, "originalTrimmingPlaceholders", Comments.colorOriginalTrimmingPlaceholders);
    public final ConfigInt originalTrimmingPlaceholderColor = i(0xA0D06B, 0, 16777215, "originalTrimmingPlaceholderColor", Comments.rgb, Comments.originalTrimmingPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorShortenedTrimmingPlaceholders = b(true, "shortenedTrimmingPlaceholders", Comments.colorShortenedTrimmingPlaceholders);
    public final ConfigInt shortenedTrimmingPlaceholderColor = i(0x75D78D, 0, 16777215, "shortenedTrimmingPlaceholderColor", Comments.rgb, Comments.shortenedTrimmingPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorAlternativeTrimmingPlaceholders = b(true, "alternativeTrimmingPlaceholders", Comments.colorAlternativeTrimmingPlaceholders);
    public final ConfigInt alternativeTrimmingPlaceholderColor = i(0x69D3C6, 0, 16777215, "alternativeTrimmingPlaceholderColor", Comments.rgb, Comments.alternativeTrimmingPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorInvalidPlaceholders = b(true, "invalidPlaceholders", Comments.colorInvalidPlaceholders);
    public final ConfigInt invalidPlaceholderColor = i(0xFF6460, 0, 16777215, "invalidPlaceholderColor", Comments.rgb, Comments.invalidPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorEscapedPlaceholders = b(true, "escapedPlaceholders", Comments.colorEscapedPlaceholders);
    public final ConfigInt escapedPlaceholderColor = i(0x743020, 0, 16777215, "escapedPlaceholderColor", Comments.rgb, Comments.escapedPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorDisabledPlaceholders = b(true, "disabledPlaceholders", Comments.colorDisabledPlaceholders);
    public final ConfigInt disabledPlaceholderColor = i(0x000000, 0, 16777215, "disabledPlaceholderColor", Comments.rgb, Comments.disabledPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorEscapedDisabledPlaceholders = b(true, "escapedDisabledPlaceholders", Comments.colorEscapedDisabledPlaceholders);
    public final ConfigInt escapedDisabledPlaceholderColor = i(0x2F0407, 0, 16777215, "escapedDisabledPlaceholderColor", Comments.rgb, Comments.escapedDisabledPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorIncompletePlaceholders = b(true, "incompletePlaceholders", Comments.colorIncompletePlaceholders);
    public final ConfigInt incompletePlaceholderColor = i(0x743020, 0, 16777215, "incompletePlaceholderColor", Comments.rgb, Comments.incompletePlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorUnoptimizedPlaceholders = b(true, "unoptimizedPlaceholders", Comments.colorUnoptimizedPlaceholders);
    public final ConfigInt unoptimizedPlaceholderColor = i(0x743020, 0, 16777215, "unoptimizedPlaceholderColor", Comments.rgb, Comments.unoptimizedPlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());
    public final ConfigBool colorSyntacticallyErrorsomePlaceholders = b(true, "syntacticallyErrorsomePlaceholders", Comments.colorSyntacticallyErrorsomePlaceholders);
    public final ConfigInt syntacticallyErrorsomePlaceholderColor = i(0x743020, 0, 16777215, "syntacticallyErrorsomePlaceholderColor", Comments.rgb, Comments.syntacticallyErrorsomePlaceholderColor, ConfigAnnotations.IntDisplay.HEX.asComment());

    public final ConfigGroup richLabelEditorCustomization = group(2, "richLabelEditorCustomization", "Rich Label Editor Customization");
    public final ConfigEnum<DefaultClosingMode> rleDefaultClosingMode = e(DefaultClosingMode.ASK, "defaultClosingMode", Comments.rleDefaultClosingMode);
    public final ConfigEnum<CounterPosition> rleCounterPosition = e(CounterPosition.DYNAMIC, "counterPosition", Comments.rleCounterPosition);
    public final ConfigEnum<TabWidthDistributionMode> rleTabWidthDistributionMode = e(TabWidthDistributionMode.UNEVEN, "tabWidthDistributionMode", Comments.rleTabWidthDistributionMode);
    public final ConfigBool rleEnableScreenConfigButton = b(true, "enableScreenConfigButton", Comments.rleEnableScreenConfigButton);

    public final ConfigGroup richLabelEditorButtonOutlineSettings = group(2, "richLabelEditorButtonOutlineSettings", "Attached Label Box Outline Settings");
    public final ConfigFloat richLabelEditorButtonOutlineFadeInTime = f(0.25F, 0F, 10F, "richLabelEditorButtonOutlineFadeInTime", Comments.seconds, Comments.richLabelEditorButtonOutlineFadeInTime, Comments.defaultZero);
    public final ConfigFloat richLabelEditorButtonOutlineFadeOutTime = f(0.75F, 0F, 10F, "richLabelEditorButtonOutlineFadeOutTime", Comments.seconds, Comments.richLabelEditorButtonOutlineFadeOutTime, Comments.defaultZero);
    public final ConfigInt richLabelEditorButtonOutlineIdleStateAlpha = i(0x69, 0, 255, "richLabelEditorButtonOutlineIdleStateAlpha", Comments.alpha, Comments.richLabelEditorButtonOutlineIdleStateAlpha, ConfigAnnotations.IntDisplay.HEX.asComment());

    public final ConfigGroup clipboardDisplaySourceGUI = group(1, "clipboardDisplaySourceGUI", "Clipboard Display Source GUI");
    public final ConfigBool deselectEmptyAttachedLabel = b(true, "deselectEmptyAttachedLabel", Comments.deselectEmptyAttachedLabel);

    public final ConfigGroup clipboardDisplayLinkOutlines = group(1, "clipboardDisplayLinkOutlines", "Clipboard Display Link Outlines");
    public final ConfigBool enableCustomOutlineForDisplayLinks = b(true, "enableCustomOutlineForDisplayLinks", Comments.enableCustomOutlineForDisplayLinks);
    public final ConfigBool enableDifferentOutlineForInvalidDisplayLinks = b(true, "enableDifferentOutlineForInvalidDisplayLinks", Comments.enableDifferentOutlineForInvalidDisplayLinks, Comments.onlyTakesEffectOutline);

//  public final ConfigGroup customOutlineSettings = group(2, "customOutlineSettings", "Custom Outline Settings");
    public final ConfigFloat customOutlineAlpha = f(0.8F, 0F, 1F, "customOutlineAlpha", Comments.customOutlineAlpha, Comments.outlineReference, Comments.onlyTakesEffectOutline);

    public final ConfigGroup textFieldsEnhancements = group(1, "textFieldsEnhancements", "Text Fields Enhancements");
    public final ConfigBool truncateOverflowingStrings = b(true, "truncateOverflowingStrings", Comments.truncateOverflowingStrings);
    public final ConfigBool addMarqueeEffectToTruncatedStrings = b(true, "addMarqueeEffectToTruncatedStrings", Comments.addMarqueeEffectToTruncatedStrings, Comments.onlyTakesEffectMarqueeEffect);
    public final ConfigBool showTooltipForSingleOptionSelector = b(true, "showTooltipForSingleOptionSelector", Comments.showTooltipForSingleOptionSelector, Comments.onlyTakesEffectMarqueeEffect);

    public final ConfigGroup marqueeEffectSettings = group(2, "marqueeEffectSettings", "Marquee Effect Settings");
    public final ConfigInt fixedCharTravelTime = i(30, 1, 10000, "fixedCharTravelTime", Comments.milliseconds, Comments.fixedCharTravelTime);
    public final ConfigFloat fixedStringTravelTime = f(0F, 0F, 600F, "fixedStringTravelTime", Comments.seconds, Comments.fixedStringTravelTime, Comments.overrides, Comments.defaultZero);
    public final ConfigFloat maximalStringTravelTime = f(0F, 0F, 600F, "maximalStringTravelTime", Comments.seconds, Comments.maximalStringTravelTime, Comments.defaultZero);
    public final ConfigFloat minimalStringTravelTime = f(0F, 0F, 60F, "minimalStringTravelTime", Comments.seconds, Comments.minimalStringTravelTime, Comments.defaultZero);
    public final ConfigFloat stringPauseTime = f(2F, 0F, 600F, "stringPauseTime", Comments.seconds, Comments.stringPauseTime);


    @Override public @NotNull String getName() { return "client"; }

    private static class Comments {
        static String milliseconds = "[in Milliseconds]";
        static String seconds = "[in Seconds]";
        static String rgb = "[in Hex: #RrGgBb]";
        static String alpha = "[in Hex: #Aa]";
        static String pixels = "[in Pixels]";
        static String defaultZero = "Set to 0 to disable.";
        static String overrides = "Overrides 'Fixed Char Travel Time' and 'Maximal/Minimal String Travel Time'.";
        static String onlyTakesEffectGuideWidgets = "Only takes effect if 'Enable Guide Widgets' is enabled.";
        static String onlyTakesEffectVisualizationButtons = "Only takes effect if 'Enable Visualization Settings Buttons' is enabled.";
        static String onlyTakesEffectMarqueeEffect = "Only takes effect if 'Truncate Overflowing Strings' is enabled.";
        static String onlyTakesEffectOutline = "Only takes effect if 'Enable Custom Outline For Display Links' is enabled.";
        static String outlineReference = "It's set to 0.4 for other components which properties can be copied with a Clipboard.";
        static String heightReference = "Set to -1 to make it fit within the text height; set to 3 to make it fill the whole text input field height.";

        static String enableGuideWidgets = "Show the 'Duplicating Display Link Properties' widget in Display Link interface.";
        static String enableRichLabelEditor = "Enable opening the Rich Label Editor in Display Link interface.";
        static String enableVisualizationSettingsButtons = "Show the 'Show Visualization Settings' button in Display Link interface.";
        static String alwaysShowVisualizationSettingsButtons = "Never hide the Visualization Settings buttons behind the 'Show Visualization Buttons' dropdown button.";

//      static String enableActivePlaceholdersTooltip = "Show the Active placeholders part of the Placeholders Usage Guide tooltip.";
        static String enableClipboardIconHighlighting = "Highlight the 'Duplicating Display Link Properties' icon when hovered on.";
        static String enableRedirectsToPonderScenes = "Allow the guide widgets to redirect to their respective Ponder scenes on clicked.";

        static String colorPlaceholders = "Apply background coloring to placeholders in the Attached Label field.";
        static String placeholdersColorsAlpha = "The alpha value of background colorings of placeholders.";
        static String coloringBackgroundHeightIncrease = "The additional vertical space for placeholders' background coloring rectangles to take.";

        static String colorDollarPlaceholders = "Apply background coloring to all '$' (dollar sign) placeholders.";
        static String dollarPlaceholderColor = "The background color to apply to all '$' (dollar sign) placeholders.";
        static String colorBracketsPlaceholders = "Apply background coloring to all '{}' (brackets) placeholders.";
        static String bracketsPlaceholderColor = "The background color to apply to all '{}' (brackets) placeholders.";
        static String colorOriginalTrimmingPlaceholders = "Apply background coloring to all 'Ax$yB'-structured (original trimming) placeholders.";
        static String originalTrimmingPlaceholderColor = "The background color to apply to all 'Ax$yB'-structured (original trimming) placeholders.";
        static String colorShortenedTrimmingPlaceholders = "Apply background coloring to all 'Ax$'/'$yB'-structured (shortened trimming) placeholders.";
        static String shortenedTrimmingPlaceholderColor = "The background color to apply to all 'Ax$'/'$yB'-structured (shortened trimming) placeholders.";
        static String colorAlternativeTrimmingPlaceholders = "Apply background coloring to all '${xABCy}'-structured (alternative trimming) placeholders.";
        static String alternativeTrimmingPlaceholderColor = "The background color to apply to all '${xABCy}'-structured (alternative trimming) placeholders.";
        static String colorInvalidPlaceholders = "Apply background coloring to all character sequences that have been recognized to be incorrectly structured placeholders.";
        static String invalidPlaceholderColor = "The background color to apply to all character sequences that have been recognized to be incorrectly structured placeholders.";

        static String colorEscapedPlaceholders = "Apply background coloring to all '\\'-annotated (escaped) placeholders.";
        static String escapedPlaceholderColor = "The background color to apply to all '\\'-annotated (escaped) placeholders.";
        static String colorDisabledPlaceholders = "Apply background coloring to all placeholders that are disabled by the server config.";
        static String disabledPlaceholderColor = "The background color to apply to all placeholders that are disabled by the server config.";
        static String colorEscapedDisabledPlaceholders = "Apply background coloring to all '\\'-annotated (escaped) placeholders that are disabled by the server config.";
        static String escapedDisabledPlaceholderColor = "The background color to apply to all '\\'-annotated (escaped) placeholders that are disabled by the server config.";
        static String colorIncompletePlaceholders = "Apply background coloring to all incomplete placeholders.";
        static String incompletePlaceholderColor = "The background color to apply to all incomplete placeholders.";
        static String colorUnoptimizedPlaceholders = "Apply background coloring to all unoptimized placeholders.";
        static String unoptimizedPlaceholderColor = "The background color to apply to all unoptimized placeholders.";
        static String colorSyntacticallyErrorsomePlaceholders = "Apply background coloring to all placeholders containing syntax errors.";
        static String syntacticallyErrorsomePlaceholderColor = "The background color to apply to all placeholders containing syntax errors.";

        static String rleDefaultClosingMode = "Which action to perform to the Label when the Editor is closed with a keybind instead of a button.";
        static String rleCounterPosition = "How should the Character Counter appear: in the same field as the Label or in its own window.";
        static String rleTabWidthDistributionMode = "How should the Placeholder tab widths be distributed in uncertain situations: evenly (may create uneven gaps in between) or unevenly (longer placeholders will have wider tabs).";
        static String rleEnableScreenConfigButton = "Enable the 'Configure' button, redirecting to the respective client config section, in Rich Label Editor interface.";

        static String richLabelEditorButtonOutlineFadeInTime = "The fixed time for the Rich Label Editor button outline to fully fade in.";
        static String richLabelEditorButtonOutlineFadeOutTime = "The fixed time for the Rich Label Editor button outline to fade out back to idle state opacity.";
        static String richLabelEditorButtonOutlineIdleStateAlpha = "The default opacity of the Rich Label Editor button outline when it's not hovered on.";

        static String deselectEmptyAttachedLabel = "Prevent the 'Copy the Attached Label' from being automatically selected if the Attached Label is empty.";

        static String enableCustomOutlineForDisplayLinks = "Enable a green block outline for Display Links when holding a Clipboard that is different from other components which properties can be copied.";
        static String enableDifferentOutlineForInvalidDisplayLinks = "Color the block outline red for Display Links with invalid Source block or Target position data.";

        static String customOutlineAlpha = "The opacity of custom block outlines for Display Links.";

        static String truncateOverflowingStrings = "Hide the overflowing ends of long strings to make them fit into their text fields.";
        static String addMarqueeEffectToTruncatedStrings = "Add the 'Marquee' (AKA 'Running Line') effect to the truncated strings.";
        static String showTooltipForSingleOptionSelector = "Show the 'Type of Information' selector tooltip if it only has a single option.";

        static String fixedCharTravelTime = "The fixed time for the truncated string to scroll through, per overflowing character.";
        static String fixedStringTravelTime = "The fixed time for the truncated string to scroll through, independently of its length.";
        static String maximalStringTravelTime = "The maximal time for the truncated string to scroll through, independently of its length.";
        static String minimalStringTravelTime = "The minimal time for the truncated string to scroll through, independently of its length.";
        static String stringPauseTime = "The fixed time for the truncated string to stay still once at the start or the end of scrolling.";
    }
}
