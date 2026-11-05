package com.vladiscrafter.createidlx.util.attachedLabel;

public class AttachedLabelPart {
    public enum PlaceholderType { DOLLAR, BRACKETS, TRIM, TRIM_SHORT, TRIM_ALT, INVALID }
    public enum PlaceholderState { ACTIVE, ESCAPED, DISABLED, ESCAPED_DISABLED, /*INCOMPLETE, UNOPTIMIZED, SYNTACTICALLY_ERRORSOME,*/ UNIDENTIFIED}

    public enum CaptureGroupValueType { CHARS, FR_DECIMAL, FR_PERCENT, FR_PROPER }
    public enum CaptureGroupOperation { CUT, PRESERVE, NONE }
    public enum CaptureGroupRounding { CEIL, FLOOR, AUTO }

    public static CaptureGroup defaultCG = new CaptureGroup(CaptureGroupValueType.CHARS, 0, CaptureGroupOperation.NONE, CaptureGroupRounding.AUTO);

    public static PlaceholderProperties defaultPr = new PlaceholderProperties(defaultCG, CaptureGroupOperation.NONE, defaultCG);

    public static Placeholder dollar = new Placeholder("$", PlaceholderType.DOLLAR, PlaceholderState.ACTIVE, defaultPr);
    public static Placeholder brackets = new Placeholder("{}", PlaceholderType.BRACKETS, PlaceholderState.ACTIVE, defaultPr);
    public static Placeholder invalid = new Placeholder("", PlaceholderType.INVALID, PlaceholderState.UNIDENTIFIED, defaultPr);

    public static non-sealed class Placeholder implements AttachedLabelBreakdownResult {
        private String raw;
        private PlaceholderType type;
        private PlaceholderState state;
        private PlaceholderProperties properties;

        public Placeholder(String raw, PlaceholderType type, PlaceholderState state, PlaceholderProperties properties) {
            this.raw = raw;
            this.type = type;
            this.state = state;
            this.properties = properties;
        }

        public Placeholder copy() {
            return new Placeholder(this.raw, this.type, this.state, this.properties.copy());
        }

        public String getString() {
            return raw;
        }

        public void setString(String string) {
            raw = string;
        }

        public PlaceholderType type() {
            return type;
        }

        public void setType(PlaceholderType type) {
            this.type = type;
            reconstruct();
        }

        public Placeholder withType(PlaceholderType type) {
            this.type = type;
            return reconstruct();
        }

        public PlaceholderState state() {
            return state;
        }

        public void setState(PlaceholderState state) {
            this.state = state;
            reconstruct();
        }

        public Placeholder withState(PlaceholderState state) {
            this.state = state;
            return reconstruct();
        }

        public PlaceholderProperties properties() {
            return properties;
        }

        public void setProperties(PlaceholderProperties properties) {
            this.properties = properties;
            reconstruct();
        }

        public Placeholder withProperties(PlaceholderProperties properties) {
            this.properties = properties;
            return reconstruct();
        }

        public String asDebugString() {
            return debug(raw, type.name()) + " | " + state.name() + "\n    - " + properties.asDebugString();
        }

        public int length() {
            return raw.length();
        }

        public boolean isType(PlaceholderType comparable) {
            return type == comparable;
        }

        public boolean isPrimitive() {
            return type == PlaceholderType.DOLLAR || type == PlaceholderType.BRACKETS;
        }

        public boolean isTrimming() {
            return type == PlaceholderType.TRIM || type == PlaceholderType.TRIM_SHORT || type == PlaceholderType.TRIM_ALT;
        }

        public boolean isStandard() {
            return type == PlaceholderType.DOLLAR || type == PlaceholderType.TRIM || type == PlaceholderType.TRIM_SHORT;
        }

        public boolean isAlternative() {
            return type == PlaceholderType.BRACKETS || type == PlaceholderType.TRIM_ALT;
        }

        public boolean isState(PlaceholderState comparable) {
            return state == comparable;
        }

        /*public boolean isWorking() {
            return state == PlaceholderState.ACTIVE || state == PlaceholderState.UNOPTIMIZED;
        }*/

        public boolean isEscaped() {
            return state == PlaceholderState.ESCAPED || state == PlaceholderState.ESCAPED_DISABLED;
        }

        public boolean isDisabled() {
            return state == PlaceholderState.DISABLED || state == PlaceholderState.ESCAPED_DISABLED;
        }

        public PlaceholderType conditional(boolean alt) {
            boolean noLeftGroup = this.properties.leftGroup.operation == CaptureGroupOperation.NONE;
            boolean noRightGroup = this.properties.rightGroup.operation == CaptureGroupOperation.NONE;

            if (noLeftGroup && noRightGroup) return !alt ? PlaceholderType.DOLLAR : PlaceholderType.BRACKETS;
            else if (!alt && (noLeftGroup || noRightGroup)) return PlaceholderType.TRIM_SHORT;
            else return !alt ? PlaceholderType.TRIM : PlaceholderType.TRIM_ALT;
        }

        public Placeholder reconstruct() {
            this.type = conditional(this.isAlternative());
            this.raw = AttachedLabelProcessingUtils.constructRawPlaceholder(this);
            return this;
        }
    }

    public static class PlaceholderProperties {
        private CaptureGroup leftGroup;
        private CaptureGroupOperation middleGroupOperation;
        private CaptureGroup rightGroup;

        public PlaceholderProperties(CaptureGroup leftGroup, CaptureGroupOperation middleGroupOperation, CaptureGroup rightGroup) {
            this.leftGroup = leftGroup;
            this.middleGroupOperation = middleGroupOperation;
            this.rightGroup = rightGroup;
        }

        public PlaceholderProperties copy() {
            return new PlaceholderProperties(this.leftGroup.copy(), this.middleGroupOperation, this.rightGroup.copy());
        }

        public CaptureGroup leftGroup() {
            return leftGroup;
        }

        public void setLeftGroup(CaptureGroup leftGroup) {
            this.leftGroup = leftGroup;
        }

        public PlaceholderProperties withLeftGroup(CaptureGroup leftGroup) {
            this.leftGroup = leftGroup;
            return this;
        }

        public CaptureGroupOperation middleGroupOperation() {
            return middleGroupOperation;
        }

        public void setMiddleGroupOperation(CaptureGroupOperation middleGroupOperation) {
            this.middleGroupOperation = middleGroupOperation;
        }

        public PlaceholderProperties withMiddleGroupOperation(CaptureGroupOperation middleGroupOperation) {
            this.middleGroupOperation = middleGroupOperation;
            return this;
        }

        public CaptureGroup rightGroup() {
            return rightGroup;
        }

        public void setRightGroup(CaptureGroup rightGroup) {
            this.rightGroup = rightGroup;
        }

        public PlaceholderProperties withRightGroup(CaptureGroup rightGroup) {
            this.rightGroup = rightGroup;
            return this;
        }

        public String asDebugString() {
            return String.format("{\n        LG = %s\n        MGO = %s\n        RG = %s\n      }",
                    leftGroup.asDebugString(), middleGroupOperation, rightGroup.asDebugString());
        }
    }

    public static class CaptureGroup {
        private CaptureGroupValueType valueType;
        private Number value;
        private CaptureGroupOperation operation;
        private CaptureGroupRounding rounding;

        public CaptureGroup(CaptureGroupValueType valueType, Number value, CaptureGroupOperation operation, CaptureGroupRounding rounding) {
            this.valueType = valueType;
            this.value = value;
            this.operation = operation;
            this.rounding = rounding;
        }

        public CaptureGroup copy() {
            return new CaptureGroup(this.valueType, this.value, this.operation, this.rounding);
        }

        public CaptureGroupValueType valueType() {
            return valueType;
        }

        public void setValueType(CaptureGroupValueType valueType) {
            this.valueType = valueType;
        }

        public CaptureGroup withValueType(CaptureGroupValueType valueType) {
            this.valueType = valueType;
            return this;
        }

        public Number value() {
            return value;
        }

        public void setValue(Number value) {
            this.value = value;
        }

        public CaptureGroup withValue(Number value) {
            this.value = value;
            return this;
        }

        public CaptureGroupOperation operation() {
            return operation;
        }

        public void setOperation(CaptureGroupOperation operation) {
            this.operation = operation;
        }

        public CaptureGroup withOperation(CaptureGroupOperation operation) {
            this.operation = operation;
            return this;
        }

        public CaptureGroupRounding rounding() {
            return rounding;
        }

        public void setRounding(CaptureGroupRounding rounding) {
            this.rounding = rounding;
        }

        public CaptureGroup withRounding(CaptureGroupRounding rounding) {
            this.rounding = rounding;
            return this;
        }

        public String asDebugString () {
            return String.format("{ VT = %s | V = %s | O = %s | R = %s }", valueType, value.toString(), operation, rounding);
        }

        public boolean isFraction () {
            return valueType != CaptureGroupValueType.CHARS;
        }

        public boolean isInvalid () {
            return valueType == CaptureGroupValueType.CHARS ? value.intValue() < 0 : value.floatValue() > 1.0;
        }

    }

    public record PlainPart(String raw) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public String asDebugString() {
            return debug(raw, "PLAIN");
        }

        public int length() {
            return raw.length();
        }
    }

    public record ProcessedInfoPart(String raw, int color) implements AttachedLabelBreakdownResult {
        public String getString() {
            return raw;
        }

        public int getColor() {
            return color;
        }

        public String asDebugString() {
            return debug(raw, "PROCESSED");
        }

        public int length() {
            return raw.length();
        }
    }

    private static String debug(String raw, String type) {
        return String.format("[ %s ]\n  - < %s >", raw, type);
    }
}
