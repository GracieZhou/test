
package com.eostek.scifly.ime.common;

import android.inputmethodservice.Keyboard;
import android.text.InputType;
import android.view.inputmethod.InputConnection;

import com.eostek.scifly.ime.util.Constans;

/**
 * 此类用于处理文字.
 * 
 * @author Youpeng
 */
public abstract class TextEditor {
    
    protected boolean mEnterAsLineBreak = false;

    /**
     * 根据输入框类型初始化文字处理器. Resets the internal state of this editor, typically
     * called when a new input session commences.
     */
    public void start(int inputType) {
        composingText.setLength(0);
        canCompose = true;
        mEnterAsLineBreak = false;

        Constans.print("LEO_TCIME", "public void start 1 ==>" + inputType);

        switch (inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                // Composing is disabled for number, date-time, and phone input
                // types.
                canCompose = false;
                break;

            case InputType.TYPE_CLASS_TEXT:
                int variation = inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
                    // Make enter-key as line-breaks for messaging.
                    mEnterAsLineBreak = true;
                }
                break;
        }
    }

    protected StringBuilder composingText = new StringBuilder();

    private boolean canCompose;

    public StringBuilder composingText() {
        return composingText;
    }
    public void setComposingText(StringBuilder builder){
        this.composingText = new StringBuilder(builder);
		Constans.print("TextEditor", "setCompopsing:" + builder.toString());
    }

    public boolean hasComposingText() {
        return composingText.length() > 0;
    }

    public void clearComposingText(InputConnection ic) {
        if (hasComposingText()) {
            // Clear composing only when there's composing-text to avoid the
            // selected
            // text being cleared unexpectedly.
            composingText.setLength(0);
            updateComposingText(ic);
        }
    }

    private void updateComposingText(InputConnection ic) {
        if (ic != null) {
            // Set cursor position 1 to advance the cursor to the text end.
            ic.setComposingText(composingText, 1);
        }
    }

    private boolean deleteLastComposingChar(InputConnection ic) {
        if (hasComposingText()) {
            // Delete-key are accepted only when there's text in composing.
            composingText.deleteCharAt(composingText.length() - 1);
            updateComposingText(ic);
            return true;
        }
        return false;
    }

    /**
     * Commits the given text to the editing field.
     */
    public boolean commitText(InputConnection ic, CharSequence text) {
        if (ic != null) {
            if (text.length() > 1) {
                // Batch edit a sequence of characters.
                ic.beginBatchEdit();
                ic.commitText(text, 1);
                ic.endBatchEdit();
            } else {
                ic.commitText(text, 1);
            }
            // Composing-text in the editor has been cleared.
            composingText.setLength(0);
            return true;
        }
        return false;
    }

    public boolean treatEnterAsLinkBreak() {
        return mEnterAsLineBreak;
        // return false;
    }

    /**
     * Composes the composing-text further with the specified key-code.
     * 
     * @return {@code true} if the key is handled and consumed for composing.
     */
    public boolean compose(InputConnection ic, int keyCode) {
        if (keyCode == Keyboard.KEYCODE_DELETE) {
            return deleteLastComposingChar(ic);
        }

        if (canCompose && doCompose(keyCode)) {
            updateComposingText(ic);
            return true;
        }
        return false;
    }

    protected abstract boolean doCompose(int keyCode);

    public void delete(int start, int end) {
        composingText.delete(start, end);

    }

    public void append(char primaryCode) {
        composingText.append(primaryCode);
    }

    public void setLength(int i) {
        composingText.setLength(i);
        Constans.print("TextEditor", "setlengh:"+i);
    }

}
