package com.vladiscrafter.createidlx;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.regex.Matcher;

public final class CreateIDLXMixinUtils {
    private CreateIDLXMixinUtils() {}

    public static boolean hasUnescapedSpecifiers(String s) {
        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();

        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '$' && (i == 0 || s.charAt(i - 1) != '\\') && isDollarSignSpecifierEnabled)
                    || (s.charAt(i) == '{' && s.charAt(i + 1) == '}' && (i == 0 || s.charAt(i - 1) != '\\') && isBracketsSpecifierEnabled)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEscapedSpecifiers(String s) {
        for (int i = 1; i < s.length(); i++) {
            if ((s.charAt(i) == '$' && s.charAt(i - 1) == '\\') || (s.charAt(i) == '{' && s.charAt(i + 1) == '}' && s.charAt(i - 1) == '\\')) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProgressDisplayLayout(String flapDisplayLayoutName) {
        return flapDisplayLayoutName.equals("Progress");
    }

//    @Deprecated (not yet)
    public static String assembleFullLine(DisplayLinkContext context, String raw) {
        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
        boolean hideEscapingOfDisabledSpecifiers = CIDLXConfigs.server.hideEscapingOfDisabledSpecifiers.get();

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return raw;

        String result = label;
        if (hasUnescapedSpecifiers(label)) {
            if (isDollarSignSpecifierEnabled) result = result.replaceAll("(?<!\\\\)\\$", Matcher.quoteReplacement(raw));
            if (isBracketsSpecifierEnabled) result = result.replaceAll("(?<!\\\\)\\{}", Matcher.quoteReplacement(raw));
        } else {
            result = label + " " + raw;
        }

        if (isDollarSignSpecifierEnabled) result = result.replaceAll("\\\\\\$", "\\$");
        if (isBracketsSpecifierEnabled) result = result.replaceAll("\\\\\\{}", "{}");
        if (hideEscapingOfDisabledSpecifiers) result = result
                .replaceAll("\\\\\\$", "\\$").replaceAll("\\\\\\{}", "{}");

        return result;
    }

    public static Tuple<ArrayList<Object>, Integer> breakDownLabelWithSpecifiers(String label) {
        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
        boolean hideEscapingOfDisabledSpecifiers = CIDLXConfigs.server.hideEscapingOfDisabledSpecifiers.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<Object> labelParts = new ArrayList<>();
        int amountOfSpecifiers = 0;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundSpecifier = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                if ((breakableLabel.charAt(i) == '$' && (i == 0 || breakableLabel.charAt(i - 1) != '\\')
                && isDollarSignSpecifierEnabled)
                || (breakableLabel.charAt(i) == '{' && breakableLabel.charAt(i + 1) == '}' && (i == 0 || breakableLabel.charAt(i - 1) != '\\')
                && isBracketsSpecifierEnabled)) {
                    if (!labelPart.isEmpty()) {
                        labelParts.add(labelPart.toString());
                    }

                    labelParts.add((char) '$');
                    amountOfSpecifiers++;
                    breakableLabel.delete(0, i + (breakableLabel.charAt(i) == '$' ? 1 : 2));
                    foundSpecifier = true;
                    break;
                }

//                if (i > 0 && ((label.charAt(i) == '$' && label.charAt(i - 1) == '\\'
//                        && (isDollarSignSpecifierEnabled || hideEscapingOfDisabledSpecifiers))
//                        || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && label.charAt(i - 1) == '\\'
//                        && (isBracketsSpecifierEnabled || hideEscapingOfDisabledSpecifiers)))) {
//                    breakableLabel.delete(0, i - 1);
//                }

                labelPart.append(breakableLabel.charAt(i));
            }

            if (!foundSpecifier) {
                labelParts.add(breakableLabel.toString());
                breakableLabel.setLength(0);
            }
        }

//        if (labelParts.getFirst() instanceof Character) labelParts.addFirst("");
//        if (labelParts.getLast() instanceof Character) labelParts.addLast("");
//        CreateIDLX.LOGGER.info(String.valueOf(labelParts));

        Tuple<ArrayList<Object>, Integer> labelPartsAndAmountOfSpecifiers = new Tuple<>(labelParts, amountOfSpecifiers);
        return labelPartsAndAmountOfSpecifiers;
    }

    public static int getLabelSizeAccountingSpecifiers(String label) {
        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
        boolean hideEscapingOfDisabledSpecifiers = CIDLXConfigs.server.hideEscapingOfDisabledSpecifiers.get();

        int labelSize = label.length();

        for (int i = 0; i < label.length(); i++) {
            if ((label.charAt(i) == '$'
                    && (i == 0 || label.charAt(i - 1) != '\\') && isDollarSignSpecifierEnabled)) labelSize -= 1;
            if (label.charAt(i) == '{'
                    && label.charAt(i + 1) == '}' && (i == 0 || label.charAt(i - 1) != '\\') && isBracketsSpecifierEnabled) labelSize -= 2;

            if (i > 0 && ((label.charAt(i) == '$' && label.charAt(i - 1) == '\\'
                    && (isDollarSignSpecifierEnabled || hideEscapingOfDisabledSpecifiers))
            || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && label.charAt(i - 1) == '\\'
                    && (isBracketsSpecifierEnabled || hideEscapingOfDisabledSpecifiers)))) {
                labelSize -= 1;
            }
        }

        return labelSize;
    }

    public static int countUnescapedSpecifiers(String label) {
        boolean isDollarSignSpecifierEnabled = CIDLXConfigs.server.enableDollarSpecifier.get();
        boolean isBracketsSpecifierEnabled = CIDLXConfigs.server.enableBracketsSpecifier.get();
        boolean hideEscapingOfDisabledSpecifiers = CIDLXConfigs.server.hideEscapingOfDisabledSpecifiers.get();

        int specifiers = 0;

        for (int i = 0; i < label.length(); i++) {
            if ((label.charAt(i) == '$' && (i == 0 || label.charAt(i - 1) != '\\')
                    && isDollarSignSpecifierEnabled)
            || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && (i == 0 || label.charAt(i - 1) != '\\')
                    && isBracketsSpecifierEnabled)) {
                specifiers++;
            }
        }

        return specifiers;
    }
}
