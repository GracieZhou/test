package com.eostek.scifly.ime.persian;

import com.eostek.scifly.ime.common.TextEditor;

public class PersianEditor  extends TextEditor{

    @Override
    protected boolean doCompose(int keyCode) {
        return false;
    }

}
