package com.pattern;

import android.content.Context;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodSubtype;

import com.example.android.softkeyboard.CandidateView;
import com.example.android.softkeyboard.LatinKeyboard;
import com.example.android.softkeyboard.LatinKeyboardView;
import com.example.android.softkeyboard.R;
import com.example.android.softkeyboard.SimpleKeyboardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Delegation of 3 keyboard(latin, 2 symbol) and switch flag of prediction and
 * completion. For possible future changes by abstracting interface out from
 * this class.
 */
public class KeyboardDelegate implements KeyboardView.OnKeyboardActionListener {

    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    private static final boolean PROCESS_HARD_KEYS = true;

    private SimpleKeyboardView mInputView;
    private CandidateView mCandidateView;

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


    private final KeyboardService keyboardService;
    private final ImmDelegate immDelegate;
    private final SeparatorDelegate separatorDelegate;

    public KeyboardDelegate(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;

        immDelegate = ImmDelegateFactory.getDelegate(keyboardService.getInputMethodManager());
        separatorDelegate = new SeparatorDelegate(keyboardService.getResources());
    }

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

        if (mInputView != null) {
            mInputView.closing();
        }
        updateCandidates();
    }

    public boolean hasShiftState(Keyboard keyboard) {
        return mQwertyKeyboard == keyboard;
    }

    public boolean handleShift(Keyboard currentKeyboard) {
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
                setLatinKeyboard(mSymbolsShiftedKeyboard);
                mSymbolsShiftedKeyboard.setShifted(true);
            } else if (currentKeyboard == mSymbolsShiftedKeyboard) {
                mSymbolsShiftedKeyboard.setShifted(false);
                setLatinKeyboard(mSymbolsKeyboard);
                mSymbolsKeyboard.setShifted(false);
            }
            return false;
        }
    }

    public void handleModeChange(Keyboard current) {
        if (current == mSymbolsKeyboard || current == mSymbolsShiftedKeyboard) {
            setLatinKeyboard(mQwertyKeyboard);
        } else {
            setLatinKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
        }
    }

    public View onCreateInputView(LayoutInflater inflater) {
        mInputView = (SimpleKeyboardView) inflater.inflate(R.layout.input_simple, null);
        mInputView.setOnKeyboardActionListener(this);
        setLatinKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    public void onStartInputView() {
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
        immDelegate.onStartInputView(mInputView);
    }

    public void onStartInput(Resources resources, EditorInfo attribute, boolean restarting) {
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
                    mCompletionOn = keyboardService.isFullscreenMode();
                }

                // We also want to look at the current state of the editor
                // to decide whether our alphabetic keyboard should start out
                // shifted.
                updateShiftKeyState(attribute);
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(resources, attribute.imeOptions);


        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        updateCandidates();
    }

    public boolean isCompletionOn() {
        return mCompletionOn;
    }

    public boolean isPredictionOn() {
        return mPredictionOn;
    }

    public void setCompletions(CompletionInfo[] completions) {
        if (isCompletionOn()) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (!isCompletionOn()) {
            List<String> suggestions = getSuggestions();
            if (null != suggestions) {
                setSuggestions(suggestions, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        keyboardService.updateCandidateView(suggestions != null && suggestions.size() > 0);

        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
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
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (isPredictionOn()) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }
    }

    public int translate(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        return c;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        final boolean shouldSupportLanguageSwitchKey =
                immDelegate.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
    }

    public View onCreateCandidateView(Context context) {
        mCandidateView = new CandidateView(context);
        mCandidateView.setService(keyboardService);
        return mCandidateView;
    }

    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }


    // Implementation of KeyboardViewListener

    public void onKey(int primaryCode, int[] keyCodes) {
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (hasComposing()) {
                commitTyped(getCurrentInputConnection());
            }
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            handleShift();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboard.KEYCODE_LANGUAGE_SWITCH) {
            handleLanguageSwitch();
            return;
        } else if (primaryCode == LatinKeyboard.KEYCODE_OPTIONS) {
            // Show a menu or somethin'
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
                && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            handleModeChange(current);
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (hasComposing()) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    public void swipeRight() {
        if (isCompletionOn()) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }

    private void handleBackspace() {
        if (handleBackspace(getCurrentInputConnection())) {
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null) {
            return;
        }

        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (handleShift(currentKeyboard)) {
            mInputView.setShifted(isCapsLock() || !mInputView.isShifted());
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (keyboardService.isInputViewShown()) {
            if (mInputView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
            }
        }
        if (isAlphabet(primaryCode) && isPredictionOn()) {
            addComposing(getCurrentInputConnection(), (char)primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
            updateCandidates();
        } else {
            getCurrentInputConnection().commitText(
                    String.valueOf((char) primaryCode), 1);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        keyboardService.requestHideSelf(0);
        mInputView.closing();
    }

    private void handleLanguageSwitch() {
        immDelegate.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }

    public boolean isWordSeparator(int code) {
        return separatorDelegate.isWordSeparator(code);
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    public void pickSuggestionManually(int index) {
        CompletionInfo ci = completionAt(index);
        if (null != ci) {
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (hasComposing()) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(getCurrentInputConnection());
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (hasComposing()) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;

            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        // A silly example: in our input method, Alt+Space
                        // is a shortcut for 'android' in lower case.
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            // First, tell the editor that it is no longer in the
                            // shift state, since we are consuming this.
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (isPredictionOn() && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }
        return false;
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        int c = translate(keyCode, event);

        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        c = translate(c);

        onKey(c, null);

        return true;
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    public void onUpdateSelection() {
        if (hasComposing()) {
            resetComposing();
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
        if (attr != null
                && mInputView != null && hasShiftState(mInputView.getKeyboard())) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != InputType.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(isCapsLock() || caps != 0);
        }
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (commitComposing(inputConnection)) {
            resetComposing();
            updateCandidates();
        }
    }

    private InputConnection getCurrentInputConnection() {
        return keyboardService.getCurrentInputConnection();
    }

    private EditorInfo getCurrentInputEditorInfo() {
        return keyboardService.getCurrentInputEditorInfo();
    }

    private IBinder getToken() {
        return keyboardService.getToken();
    }
}
