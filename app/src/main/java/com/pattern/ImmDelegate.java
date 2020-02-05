package com.pattern;

import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.example.android.softkeyboard.LatinKeyboardView;

public interface ImmDelegate {
    void onCreate(InputMethodManager imm);
    boolean shouldOfferSwitchingToNextInputMethod(IBinder token);
    void onStartInputView(LatinKeyboardView mInputView);
    void switchToNextInputMethod(IBinder token, boolean onlyCurrentIme);
}
