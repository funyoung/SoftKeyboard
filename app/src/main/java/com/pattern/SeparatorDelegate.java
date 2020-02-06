package com.pattern;

import android.content.res.Resources;

import com.example.android.softkeyboard.R;

/**
 * Separator Delegate, encapsulate text separation out from SoftKeyboard.
 */
public class SeparatorDelegate {
    private final String mWordSeparators;

    public SeparatorDelegate(Resources resources) {
        mWordSeparators = resources.getString(R.string.word_separators);
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

}
