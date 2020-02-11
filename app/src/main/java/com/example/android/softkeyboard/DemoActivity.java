package com.example.android.softkeyboard;

import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.pattern.KeyboardDelegate;
import com.pattern.KeyboardService;

public class DemoActivity extends AppCompatActivity implements KeyboardService {
    private KeyboardDelegate keyboardDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_activity);

        if (null == keyboardDelegate) {
            keyboardDelegate = new KeyboardDelegate(this);
            keyboardDelegate.onInitializeInterface(this);
            View candidateView = keyboardDelegate.onCreateCandidateView(this);
            View keyboardView = keyboardDelegate.onCreateInputView(getLayoutInflater());
            ViewGroup container = findViewById(R.id.activityLayout);
            container.addView(candidateView);
            container.addView(keyboardView);
            EditorInfo attribute = new EditorInfo();
            attribute.inputType = InputType.TYPE_CLASS_TEXT;
            keyboardDelegate.onStartInput(getResources(), attribute, true);
            keyboardDelegate.onStartInputView();
        }
    }

    @Override
    public boolean isFullscreenMode() {
        return false;
    }

    @Override
    public void pickSuggestionManually(int index) {

    }

    @Override
    public void updateCandidateView(boolean hasSuggestion) {

    }

    @Override
    public InputConnection getCurrentInputConnection() {
        return new BaseInputConnection(getWindow().getDecorView(), false);
    }

    @Override
    public EditorInfo getCurrentInputEditorInfo() {
        return new EditorInfo();
    }

    @Override
    public void requestHideSelf(int flags) {

    }

    @Override
    public boolean isInputViewShown() {
        return true;
    }

    @Override
    public IBinder getToken() {
        final Window window = getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    @Override
    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }
}
