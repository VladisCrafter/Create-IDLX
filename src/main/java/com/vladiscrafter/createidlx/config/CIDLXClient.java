package com.vladiscrafter.createidlx.config;

public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigGroup displayLinkInterfaceEnhancements = group(1, "displayLinkInterfaceEnhancements", "Display Link Interface Enhancements");
    public final ConfigBool enablePlaceholdersGuideButton = b(true, "enableGuideButtons", Comments.enablePlaceholdersGuideButton);

    public final ConfigGroup guideTooltipCustomization = group(2, "guideTooltipCustomization", "Guide Tooltip Customization");
    public final ConfigBool enableActiveSpecifiersTooltip = b(true, "enableActivePlaceholdersTooltip", Comments.enableActiveSpecifiersTooltip);
    public final ConfigBool enableProgressBarSupportStateTooltip = b(true, "enableProgressBarSupportStateTooltip", Comments.enableProgressBarSupportStateTooltip);

    public final ConfigGroup textFieldsEnhancements = group(2, "textFieldsEnhancements", "Text Fields Enhancements");
    public final ConfigBool truncateOverflowingStrings = b(true, "truncateOverflowingStrings", Comments.truncateOverflowingStrings);
    public final ConfigBool addMarqueeEffectToTruncatedStrings = b(true, "addMarqueeEffectToTruncatedStrings", Comments.addMarqueeEffectToTruncatedStrings, Comments.onlyTakesEffect);
    public final ConfigBool showTooltipForSingleOptionSelector = b(true, "showTooltipForSingleOptionSelector", Comments.showTooltipForSingleOptionSelector, Comments.onlyTakesEffect);

    public final ConfigGroup marqueeEffectSettings = group(3, "marqueeEffectSettings", "Marquee Effect Settings");
    public final ConfigInt fixedCharTravelTime = i(30, 1, 10000, "fixedCharTravelTime", Comments.milliseconds, Comments.fixedCharTravelTime);
    public final ConfigFloat fixedStringTravelTime = f(0F, 0F, 600F, "fixedStringTravelTime", Comments.seconds, Comments.fixedStringTravelTime, Comments.overrides, Comments.negative);
    public final ConfigFloat maximalStringTravelTime = f(0F, 0F, 600F, "maximalStringTravelTime", Comments.seconds, Comments.maximalStringTravelTime, Comments.negative);
    public final ConfigFloat minimalStringTravelTime = f(0F, 0F, 60F, "minimalStringTravelTime", Comments.seconds, Comments.minimalStringTravelTime, Comments.negative);
    public final ConfigFloat stringPauseTime = f(2F, 0F, 600F, "stringPauseTime", Comments.seconds, Comments.stringPauseTime);


    @Override public String getName() { return "client"; }

    private static class Comments {
        static String milliseconds = "[in Milliseconds]";
        static String seconds = "[in Seconds]";
        static String negative = "Set to zero (0.0) to disable.";
        static String onlyTakesEffect = "Only takes effect if 'Truncate Overflowing Strings' is enabled.";
        static String overrides = "Overrides 'Fixed Char Travel Time' and 'Maximal/Minimal String Travel Time'.";

        static String enablePlaceholdersGuideButton = "Show the hover-on Placeholders & String Slicing Usage Guides tooltip buttons in Display Link interface.";

        static String enableActiveSpecifiersTooltip = "Show the Active placeholders part of the Placeholders Usage Guide tooltip.";
        static String enableProgressBarSupportStateTooltip = "Show the Progress Bar display format support part of the Placeholders Usage Guide tooltip.";

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
