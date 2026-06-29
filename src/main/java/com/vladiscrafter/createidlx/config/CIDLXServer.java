package com.vladiscrafter.createidlx.config;

public class CIDLXServer extends CIDLXConfigBase {

    public final ConfigGroup placeholdersSettings = group(1, "placeholdersSettings", "Placeholders Settings");
    public final ConfigBool enableBracketsPlaceholder = b(true, "enableCurlyBracketsPlaceholder", Comments.enableBracketsPlaceholder);
    public final ConfigBool enableDollarPlaceholder = b(true, "enableDollarSignPlaceholder", Comments.enableDollarPlaceholder);
    public final ConfigBool enableEscapingOfPlaceholders = b(true, "enableEscapingOfPlaceholders", Comments.enableEscapingOfPlaceholders);
    public final ConfigBool hideEscapingOfDisabledPlaceholders = b(false, "hideEscapingOfDisabledPlaceholders", Comments.hideEscapingOfDisabledPlaceholders);

    public final ConfigGroup trimmingPlaceholdersSettings = group(2, "trimmingPlaceholdersSettings", "Trimming Placeholders Settings");
    public final ConfigBool enableOriginalTrimmingPlaceholder = b(true, "enableOriginalTrimmingPlaceholder", Comments.enableOriginalTrimmingPlaceholder);
    public final ConfigBool enableShortenedOriginalTrimmingPlaceholder = b(true, "enableShortenedOriginalTrimmingPlaceholder", Comments.enableShortenedOriginalTrimmingPlaceholder);
    public final ConfigBool enableAlternativeTrimmingPlaceholder = b(true, "enableAlternativeTrimmingPlaceholder", Comments.enableAlternativeTrimmingPlaceholder);

    public final ConfigGroup displaySourceSettings = group(1, "displaySourceSettings", "Display Source Settings");

//    public final ConfigGroup newDisplaySources = group(2, "newDisplaySources", "New Display Sources");
//    public final ConfigBool enableCurrentFloorExtendedDisplaySource = b(true, "enableCurrentFloorExtendedDisplaySource", Comments.enableCurrentFloorExtendedDisplaySource);
//    public final ConfigBool enableCountdownDisplaySource = b(true, "enableCountdownDisplaySource", Comments.enableCountdownDisplaySource);

//    public final ConfigGroup existingDisplaySources = group(2, "existingDisplaySources", "Existing Display Sources");
    public final ConfigBool enhanceCurrentFloorDisplaySource = b(true, "enhanceCurrentFloorDisplaySource", Comments.enhanceCurrentFloorDisplaySource);
    public final ConfigBool enhanceTrainStatusDisplaySource = b(true, "enhanceTrainStatusDisplaySource", Comments.enhanceTrainStatusDisplaySource);
    public final ConfigBool enhanceNixieTubeDisplaySource = b(true, "enhanceNixieTubeDisplaySource", Comments.enhanceNixieTubeDisplaySource);

//    public final ConfigGroup visualDisplaySettings

    @Override public String getName() { return "server"; }

    private static class Comments {
        static String enableBracketsPlaceholder = "Treat '{}' (curly brackets) as a placeholder for the Attached Label.";
        static String enableDollarPlaceholder = "Treat '$' (dollar sign) as a placeholder for the Attached Label.";
        static String enableEscapingOfPlaceholders = "Treat '\\' (backslash) placed right before a placeholder as a sign to treat that placeholder as a literal character.";
        static String hideEscapingOfDisabledPlaceholders = "Hide backslashes placed before the disabled placeholders.";

        static String enableOriginalTrimmingPlaceholder = "Treat the 'Ax$yB' structure (where A and B are either '{' or '}' (curly brackets), x and y are numerics) as a trimming placeholder for the Attached Label.";
        static String enableShortenedOriginalTrimmingPlaceholder = "Treat the 'Ax$' and '$yB' structures (where A and B are either '{' or '}' (curly brackets), x and y are numerics) as trimming placeholders for the Attached Label.";
        static String enableAlternativeTrimmingPlaceholder = "Treat the '${xABCy}' structure (where A, B and C are either '+' (plus sign) or '-' (minus sign), x and y are numerics) as a trimming placeholder for the Attached Label.";

        static String enableCurrentFloorExtendedDisplaySource = "Enable the Current Floor Extended display source.";
        static String enableCountdownDisplaySource = "Enable the Countdown display source.";

        static String enhanceCurrentFloorDisplaySource = "Add Attached Label functionality to the Current Floor display source from Create.";
        static String enhanceTrainStatusDisplaySource = "Add Attached Label functionality to the Train Status display source from Create.";
        static String enhanceNixieTubeDisplaySource = "Add Attached Label functionality irregardless of the Target Display type to the Nixie TUbe display source from Create.";
    }
}
