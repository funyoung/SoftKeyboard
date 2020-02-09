package com.pattern;

import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.example.android.softkeyboard.LatinKeyboardView;
import com.example.android.softkeyboard.SimpleKeyboardView;

/**
 * Empty implement, do nothing
 */
public class ImmDelegateNull implements ImmDelegate {
//    private InputMethodManager mInputMethodManager;
    public void onCreate(InputMethodManager imm) {
//        mInputMethodManager = imm;
    }
    public boolean shouldOfferSwitchingToNextInputMethod(IBinder token) {
        return false;
    }

    public void onStartInputView(SimpleKeyboardView mInputView) {
//        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
//        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    public void switchToNextInputMethod(IBinder token, boolean onlyCurrentIme) {
//        mInputMethodManager.switchToNextInputMethod(token, onlyCurrentIme);
    }
}
