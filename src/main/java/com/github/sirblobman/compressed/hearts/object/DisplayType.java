package com.github.sirblobman.compressed.hearts.object;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

public enum DisplayType {
    BOSS_BAR, ACTION_BAR, NONE;
    
    @Nullable
    public static DisplayType parse(String string) {
        try {
            String uppercase = string.toLowerCase(Locale.US);
            String replace = uppercase.replace('-', '_').replace(' ', '_');
            return valueOf(replace);
        } catch(IllegalArgumentException ex) {
            return null;
        }
    }
}
