package com.github.sirblobman.compressed.hearts.object;

public enum DisplayType {
    BOSS_BAR, ACTION_BAR, NONE;

    public static DisplayType parse(String string) {
        try {
            String value = string.toUpperCase().replace('-', '_').replace(' ', '_');
            return valueOf(value);
        } catch (Exception ex) {
            return null;
        }
    }
}
