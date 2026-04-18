package com.vladiscrafter.createidlx.config;

public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigGroup displayLinkGUI = group(1, "displayLinkGUI", "Display Link GUI");
    public final ConfigBool enableGuideButtons = b(true, "enableGuideButtons", Comments.enableGuideButtons);
    public final ConfigBool enableGuideButtonRedirects = b(true, "enableGuideButtonRedirects", Comments.enableGuideButtonRedirects, Comments.onlyTakesEffectGuideButtons);

    public final ConfigGroup guideTooltipsCustomization = group(2, "guideTooltipsCustomization", "Guide Tooltips Customization");
    public final ConfigBool enableActivePlaceholdersTooltip = b(true, "enableActivePlaceholdersTooltip", Comments.enableActivePlaceholdersTooltip);
    public final ConfigBool enableProgressBarSupportStateTooltip = b(true, "enableProgressBarSupportStateTooltip", Comments.enableProgressBarSupportStateTooltip);

    public final ConfigGroup clipboardDisplaySourceGUI = group(1, "clipboardDisplaySourceGUI", "Clipboard Display Source GUI");
    public final ConfigBool deselectEmptyAttachedLabel = b(true, "deselectEmptyAttachedLabel", Comments.deselectEmptyAttachedLabel);

    public final ConfigGroup clipboardDisplayLinkOutlines = group(1, "clipboardDisplayLinkOutlines", "Clipboard Display Link Outlines");
    public final ConfigBool enableCustomOutlineForDisplayLinks = b(true, "enableCustomOutlineForDisplayLinks", Comments.enableCustomOutlineForDisplayLinks);
    public final ConfigBool enableDifferentOutlineForInvalidDisplayLinks = b(true, "enableDifferentOutlineForInvalidDisplayLinks", Comments.enableDifferentOutlineForInvalidDisplayLinks, Comments.onlyTakesEffectOutline);

//    public final ConfigGroup customOutlineSettings = group(2, "customOutlineSettings", "Custom Outline Settings");
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


    @Override public String getName() { return "client"; }

    private static class Comments {
        static String milliseconds = "[in Milliseconds]";
        static String seconds = "[in Seconds]";
        static String defaultZero = "Set to zero (0.0) to disable.";
        static String overrides = "Overrides 'Fixed Char Travel Time' and 'Maximal/Minimal String Travel Time'.";
        static String onlyTakesEffectGuideButtons = "Only takes effect if 'Enable Guide Buttons' is enabled.";
        static String onlyTakesEffectMarqueeEffect = "Only takes effect if 'Truncate Overflowing Strings' is enabled.";
        static String onlyTakesEffectOutline = "Only takes effect if 'Enable Custom Outline For Display Links' is enabled.";
        static String outlineReference = "It's set to 0.4 for other components which properties can be copied with a Clipboard.";

        static String enableGuideButtons = "Show the 'Placeholders Usage Guide' & 'Duplicating Display Link Properties' buttons in Display Link interface.";
        static String enableGuideButtonRedirects = "Allow the guide buttons to redirect to their respective Ponder scenes on clicked.";

        static String enableActivePlaceholdersTooltip = "Show the Active placeholders part of the Placeholders Usage Guide tooltip.";
        static String enableProgressBarSupportStateTooltip = "Show the Progress Bar display format support part of the Placeholders Usage Guide tooltip.";

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
