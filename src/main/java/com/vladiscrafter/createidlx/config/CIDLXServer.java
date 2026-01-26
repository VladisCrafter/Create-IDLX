package com.vladiscrafter.createidlx.config;

public class CIDLXServer extends CIDLXConfigBase {

    public final ConfigBool enableBracketsSpecifier = b(true, "enableCurlyBracketsPlaceholder", Comments.enableBracketsSpecifier);
    public final ConfigBool enableDollarSpecifier = b(true, "enableDollarSignPlaceholder", Comments.enableDollarSpecifier);
    public final ConfigBool hideEscapingOfDisabledSpecifiers = b(false, "hideEscapingOfDisabledPlaceholders", Comments.hideEscapingOfDisabledSpecifiers);

    @Override public String getName() { return "server"; }

    private static class Comments {
        static String enableBracketsSpecifier = "Treat {} (curly brackets) as a placeholder for the Attached Label.";
        static String enableDollarSpecifier = "Treat $ (dollar sign) as a placeholder for the Attached Label.";
        static String hideEscapingOfDisabledSpecifiers = "Hide backslashes placed before the disabled placeholders.";
    }
}
