package com.vladiscrafter.createidlx.config;

public class CIDLXClient extends CIDLXConfigBase {

    public final ConfigBool enablePlaceholdersGuideButton = b(true, "enableGuideButtons", Comments.enablePlaceholdersGuideButton);

    public final ConfigGroup placeholdersGuideButtonTooltipCustomization = group(1, "tooltipCustomization", "Tooltip Customization");
    public final ConfigBool enableActiveSpecifiersTooltip = b(true, "enableActivePlaceholdersTooltip", Comments.enableActiveSpecifiersTooltip);
    public final ConfigBool enableProgressBarSupportStateTooltip = b(true, "enableProgressBarSupportStateTooltip", Comments.enableProgressBarSupportStateTooltip);


    @Override public String getName() { return "client"; }

    private static class Comments {
        static String enablePlaceholdersGuideButton = "Show the hover-on Placeholders & String Slicing Usage Guides tooltip buttons in Display Link interface.";
        static String enableActiveSpecifiersTooltip = "Show the Active placeholders part of the Placeholders Usage Guide tooltip.";
        static String enableProgressBarSupportStateTooltip = "Show the Progress Bar display format support part of the Placeholders Usage Guide tooltip.";
    }
}
