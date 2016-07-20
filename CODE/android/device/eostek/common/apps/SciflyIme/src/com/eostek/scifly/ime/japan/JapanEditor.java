package com.eostek.scifly.ime.japan;

import com.eostek.scifly.ime.common.TextEditor;

public class JapanEditor extends TextEditor {

    @Override
    protected boolean doCompose(int keyCode) {
        return false;
    }

}
