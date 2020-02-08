package com.pattern;

import android.view.inputmethod.EditorInfo;

import com.example.android.softkeyboard.LatinKeyboard;

/**
 * todo: estimate dependence of LatinKeyboard
 */
public interface KeyboardService {
    boolean isFullscreenMode();
    void setLatinKeyboard(LatinKeyboard nextKeyboard);
    void updateShiftKeyState(EditorInfo attr);
    void pickSuggestionManually(int index);
}
