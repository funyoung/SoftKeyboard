package com.pattern;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;

import com.example.android.softkeyboard.LatinKeyboard;
import com.example.android.softkeyboard.R;
import com.example.android.softkeyboard.SoftKeyboard;

/**
 * Delegation of 3 keyboard(latin, 2 symbol) and switch flag of prediction and
 * completion. For possible future changes by abstracting interface out from
 * this class.
 */
public class KeyboardDelegate {
    private boolean mPredictionOn;
    private boolean mCompletionOn;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mQwertyKeyboard;

    private LatinKeyboard mCurKeyboard;

    public boolean initialized() {
        return mQwertyKeyboard != null;
    }

    /**
     * todo: abstract keyboard instance by Abstract Factory pattern to
     *
     * @param context
     */
    public void onInitializeInterface(Context context) {
        mQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(context, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(context, R.xml.symbols_shift);
    }

    public void onFinishInput() {
        mCurKeyboard = mQwertyKeyboard;
    }

    public boolean hasShiftState(Keyboard keyboard) {
        return mQwertyKeyboard == keyboard;
    }

    public void handleShift(SoftKeyboard softKeyboard, Keyboard currentKeyboard) {
        if (currentKeyboard == mSymbolsKeyboard) {
            mSymbolsKeyboard.setShifted(true);
            softKeyboard.setLatinKeyboard(mSymbolsShiftedKeyboard);
            mSymbolsShiftedKeyboard.setShifted(true);
        } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
            mSymbolsShiftedKeyboard.setShifted(false);
            softKeyboard.setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    public void handleModeChange(SoftKeyboard softKeyboard, Keyboard current) {
        if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
            softKeyboard.setLatinKeyboard(mQwertyKeyboard);
        } else {
            softKeyboard.setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    public void onCreateInputView(SoftKeyboard softKeyboard) {
        softKeyboard.setLatinKeyboard(mQwertyKeyboard);
    }

    public void onStartInputView(SoftKeyboard softKeyboard) {
        softKeyboard.setLatinKeyboard(mCurKeyboard);
    }

    public void onStartInput(SoftKeyboard softKeyboard, EditorInfo attribute) {
        mPredictionOn = false;
        mCompletionOn = false;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                // Phones will also default to the symbols keyboard, though
                // often you will want to have a dedicated phone keyboard.
                mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mQwertyKeyboard;
                mPredictionOn = true;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = softKeyboard.isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                softKeyboard.updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard;
                softKeyboard.updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(softKeyboard.getResources(), attribute.imeOptions);
    }

    public boolean isCompletionOn() {
        return mCompletionOn;
    }

    public boolean isPredictionOn() {
        return mPredictionOn;
    }
}
