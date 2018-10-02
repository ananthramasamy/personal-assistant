package com.personalassistant.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class OnTouchPasswordListener implements View.OnTouchListener {

    private int mPreviousInputType;
    private EditText passwordET;
    public OnTouchPasswordListener(EditText passwordET) {
        this.passwordET = passwordET;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPreviousInputType = passwordET.getInputType();
                setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setInputType(mPreviousInputType, true);
                mPreviousInputType = -1;
                break;
        }

        return false;
    }

    private void setInputType(int inputType, boolean keepState) {
        int selectionStart = -1;
        int selectionEnd = -1;
        if (keepState) {
            selectionStart = passwordET.getSelectionStart();
            selectionEnd = passwordET.getSelectionEnd();
        }
        passwordET.setInputType(inputType);
        if (keepState) {
            passwordET.setSelection(selectionStart, selectionEnd);
        }
    }
}