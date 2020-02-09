package com.pattern;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.example.android.softkeyboard.LatinKeyboard;
import com.example.android.softkeyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Delegation of 3 keyboard(latin, 2 symbol) and switch flag of prediction and
 * completion. For possible future changes by abstracting interface out from
 * this class.
 */
public class KeyboardDelegate {
    private StringBuilder mComposing = new StringBuilder();
    private CompletionInfo[] mCompletions;

    private boolean mPredictionOn;
    private boolean mCompletionOn;

    private int mLastDisplayWidth;

    private boolean mCapsLock;
    private long mLastShiftTime;

    private long mMetaState;

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
        if (initialized()) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth(context);
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }

        mQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(context, R.xml.symbols);
        mSymbolsShiftedKeyboard = new LatinKeyboard(context, R.xml.symbols_shift);
    }

    private int getMaxWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public void onFinishInput() {
        mComposing.setLength(0);
        mCurKeyboard = mQwertyKeyboard;
    }

    public boolean hasShiftState(Keyboard keyboard) {
        return mQwertyKeyboard == keyboard;
    }

    public boolean handleShift(KeyboardService softKeyboard, Keyboard currentKeyboard) {
        if (hasShiftState(currentKeyboard)) {
            // Alphabet keyboard
            long now = System.currentTimeMillis();
            if (mLastShiftTime + 800 > now) {
                mCapsLock = !mCapsLock;
                mLastShiftTime = 0;
            } else {
                mLastShiftTime = now;
            }
            return true;
        } else {
            if (currentKeyboard == mSymbolsKeyboard) {
                mSymbolsKeyboard.setShifted(true);
                softKeyboard.setLatinKeyboard(mSymbolsShiftedKeyboard);
                mSymbolsShiftedKeyboard.setShifted(true);
            } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
                mSymbolsShiftedKeyboard.setShifted(false);
                softKeyboard.setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
            return false;
        }
    }

    public void handleModeChange(KeyboardService softKeyboard, Keyboard current) {
        if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
            softKeyboard.setLatinKeyboard(mQwertyKeyboard);
        } else {
            softKeyboard.setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    public void onCreateInputView(KeyboardService softKeyboard) {
        softKeyboard.setLatinKeyboard(mQwertyKeyboard);
    }

    public void onStartInputView(KeyboardService softKeyboard) {
        softKeyboard.setLatinKeyboard(mCurKeyboard);
    }

    public void onStartInput(KeyboardService softKeyboard, Resources resources, EditorInfo attribute, boolean restarting) {
        mComposing.setLength(0);
        mCompletions = null;

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
        mCurKeyboard.setImeOptions(resources, attribute.imeOptions);


        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }
    }

    public boolean isCompletionOn() {
        return mCompletionOn;
    }

    public boolean isPredictionOn() {
        return mPredictionOn;
    }

    public boolean setCompletions(CompletionInfo[] completions) {
        if (isCompletionOn()) {
            mCompletions = completions;
            return true;
        }

        return false;
    }

    public CompletionInfo completionAt(int index) {
        if (isCompletionOn() && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            return mCompletions[index];
        }

        return null;
    }

    public boolean hasComposing() {
        return mComposing.length() > 0;
    }

    public void resetComposing() {
        mComposing.setLength(0);
    }

    public int translate(int c) {
        if (hasComposing()) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }
        return c;
    }

    public boolean commitComposing(InputConnection inputConnection) {
        if (hasComposing()) {
            inputConnection.commitText(mComposing, mComposing.length());
            return true;
        }

        return false;
    }

    public List<String> getSuggestions() {
        List<String> list = null;
        if (hasComposing()) {
            list = new ArrayList<>();
            String str = mComposing.toString();
            list.add(str);
            // extra code to split text as suggestion items.
            for (int i = 0; i < str.length(); i++) {
                list.add(String.valueOf(str.charAt(i)));
            }
        } else {
        }
        return list;
    }

    public void addComposing(InputConnection inputConnection, char ch) {
        mComposing.append(ch);
        inputConnection.setComposingText(mComposing, 1);
    }

    public boolean handleBackspace(InputConnection inputConnection) {
        boolean handled = false;
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            inputConnection.setComposingText(mComposing, 1);
            handled = true;
        } else if (length > 0) {
            resetComposing();
            inputConnection.commitText("", 0);
            handled = true;
        }

        return handled;
    }

    public boolean isCapsLock() {
        return mCapsLock;
    }

    public void onKeyUp(int keyCode, KeyEvent event) {
        if (isPredictionOn()) {
            mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                    keyCode, event);
        }
    }

    public int translate(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        return c;
    }
}
