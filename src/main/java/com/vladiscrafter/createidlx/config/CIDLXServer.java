package com.vladiscrafter.createidlx.config;

public class CIDLXServer extends CIDLXConfigBase {

    public final ConfigBool hideEscapingOfDisabledSpecifiers = b(false, "hideEscapingOfDisabledPlaceholders", Comments.hideEscapingOfDisabledSpecifiers);
    public final ConfigBool enableCrudeProgressBarSupport = b(false, "enableProgressBarPlaceholdersSupport", Comments.enableCrudeProgressBarSupport);

    public final ConfigGroup placeholdersAvailability = group(1, "placeholdersAvailability", "Placeholders Availability");
    public final ConfigBool enableBracketsSpecifier = b(true, "enableCurlyBracketsPlaceholder", Comments.enableBracketsSpecifier);
    public final ConfigBool enableDollarSpecifier = b(true, "enableDollarSignPlaceholder", Comments.enableDollarSpecifier);

    @Override public String getName() { return "server"; }

    private static class Comments {
        static String enableBracketsSpecifier = "Treat {} (curly brackets) as a placeholder for the Attached Label.";
        static String enableDollarSpecifier = "Treat $ (dollar sign) as a placeholder for the Attached Label.";
        static String hideEscapingOfDisabledSpecifiers = "Hide backslashes placed before the disabled placeholders.";
        static String enableCrudeProgressBarSupport = "Allow usage of placeholders for the Progress Bar display format (the bar characters will incorrectly appear as non-wide).";
    }
}
