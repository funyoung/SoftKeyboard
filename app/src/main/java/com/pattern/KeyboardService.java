package com.pattern;


import android.content.res.Resources;
import android.os.IBinder;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

/**
 * todo: estimate dependence of LatinKeyboard
 */
public interface KeyboardService {
    boolean isFullscreenMode();
    void pickSuggestionManually(int index);

    void updateCandidateView(boolean hasSuggestion);

    InputConnection getCurrentInputConnection();
    EditorInfo getCurrentInputEditorInfo();
    void requestHideSelf(int flags);
    boolean isInputViewShown();

    IBinder getToken();

    InputMethodManager getInputMethodManager();

    Resources getResources();
}
