package com.vladiscrafter.createidlx.util;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.vladiscrafter.createidlx.config.CIDLXConfigs;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.regex.Matcher;

public final class CreateIDLXMixinUtils {
    private CreateIDLXMixinUtils() {}

    public static boolean hasUnescapedPlaceholders(String s) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();

        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) == '$' && (i == 0 || s.charAt(i - 1) != '\\') && isDollarSignPlaceholderEnabled)
                    || (s.charAt(i) == '{' && s.charAt(i + 1) == '}' && (i == 0 || s.charAt(i - 1) != '\\') && isBracketsPlaceholderEnabled)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEscapedPlaceholders(String s) {
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
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean hideEscapingOfDisabledPlaceholders = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        String label = context.sourceConfig().getString("Label");
        if (label.isEmpty()) return raw;

        String result = label;
        if (hasUnescapedPlaceholders(label)) {
            if (isDollarSignPlaceholderEnabled) result = result.replaceAll("(?<!\\\\)\\$", Matcher.quoteReplacement(raw));
            if (isBracketsPlaceholderEnabled) result = result.replaceAll("(?<!\\\\)\\{}", Matcher.quoteReplacement(raw));
        } else {
            result = label + " " + raw;
        }

        if (isDollarSignPlaceholderEnabled) result = result.replaceAll("\\\\\\$", "\\$");
        if (isBracketsPlaceholderEnabled) result = result.replaceAll("\\\\\\{}", "{}");
        if (hideEscapingOfDisabledPlaceholders) result = result
                .replaceAll("\\\\\\$", "\\$").replaceAll("\\\\\\{}", "{}");

        return result;
    }

    public static Tuple<ArrayList<Object>, Integer> breakDownLabelWithPlaceholders(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean hideEscapingOfDisabledPlaceholders = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        StringBuilder breakableLabel = new StringBuilder(label);
        ArrayList<Object> labelParts = new ArrayList<>();
        int amountOfPlaceholders = 0;

        while (!breakableLabel.isEmpty()) {
            StringBuilder labelPart = new StringBuilder();
            boolean foundPlaceholder = false;

            for (int i = 0; i < breakableLabel.length(); i++) {
                if ((breakableLabel.charAt(i) == '$' && (i == 0 || breakableLabel.charAt(i - 1) != '\\')
                && isDollarSignPlaceholderEnabled)
                || (breakableLabel.charAt(i) == '{' && breakableLabel.charAt(i + 1) == '}' && (i == 0 || breakableLabel.charAt(i - 1) != '\\')
                && isBracketsPlaceholderEnabled)) {
                    if (!labelPart.isEmpty()) {
                        labelParts.add(labelPart.toString());
                    }

                    labelParts.add((char) '$');
                    amountOfPlaceholders++;
                    breakableLabel.delete(0, i + (breakableLabel.charAt(i) == '$' ? 1 : 2));
                    foundPlaceholder = true;
                    break;
                }

//                if (i > 0 && ((label.charAt(i) == '$' && label.charAt(i - 1) == '\\'
//                        && (isDollarSignPlaceholderEnabled || hideEscapingOfDisabledPlaceholders))
//                        || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && label.charAt(i - 1) == '\\'
//                        && (isBracketsPlaceholderEnabled || hideEscapingOfDisabledPlaceholders)))) {
//                    breakableLabel.delete(0, i - 1);
//                }

                labelPart.append(breakableLabel.charAt(i));
            }

            if (!foundPlaceholder) {
                labelParts.add(breakableLabel.toString());
                breakableLabel.setLength(0);
            }
        }

//        if (labelParts.getFirst() instanceof Character) labelParts.addFirst("");
//        if (labelParts.getLast() instanceof Character) labelParts.addLast("");
//        CreateIDLX.LOGGER.info(String.valueOf(labelParts));

        Tuple<ArrayList<Object>, Integer> labelPartsAndAmountOfPlaceholders = new Tuple<>(labelParts, amountOfPlaceholders);
        return labelPartsAndAmountOfPlaceholders;
    }

    public static int getLabelSizeAccountingPlaceholders(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean hideEscapingOfDisabledPlaceholders = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        int labelSize = label.length();

        for (int i = 0; i < label.length(); i++) {
            if ((label.charAt(i) == '$'
                    && (i == 0 || label.charAt(i - 1) != '\\') && isDollarSignPlaceholderEnabled)) labelSize -= 1;
            if (label.charAt(i) == '{'
                    && label.charAt(i + 1) == '}' && (i == 0 || label.charAt(i - 1) != '\\') && isBracketsPlaceholderEnabled) labelSize -= 2;

            if (i > 0 && ((label.charAt(i) == '$' && label.charAt(i - 1) == '\\'
                    && (isDollarSignPlaceholderEnabled || hideEscapingOfDisabledPlaceholders))
            || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && label.charAt(i - 1) == '\\'
                    && (isBracketsPlaceholderEnabled || hideEscapingOfDisabledPlaceholders)))) {
                labelSize -= 1;
            }
        }

        return labelSize;
    }

    public static int countUnescapedPlaceholders(String label) {
        boolean isDollarSignPlaceholderEnabled = CIDLXConfigs.server.enableDollarPlaceholder.get();
        boolean isBracketsPlaceholderEnabled = CIDLXConfigs.server.enableBracketsPlaceholder.get();
        boolean hideEscapingOfDisabledPlaceholders = CIDLXConfigs.server.hideEscapingOfDisabledPlaceholders.get();

        int placeholders = 0;

        for (int i = 0; i < label.length(); i++) {
            if ((label.charAt(i) == '$' && (i == 0 || label.charAt(i - 1) != '\\')
                    && isDollarSignPlaceholderEnabled)
            || (label.charAt(i) == '{' && label.charAt(i + 1) == '}' && (i == 0 || label.charAt(i - 1) != '\\')
                    && isBracketsPlaceholderEnabled)) {
                placeholders++;
            }
        }

        return placeholders;
    }
}
