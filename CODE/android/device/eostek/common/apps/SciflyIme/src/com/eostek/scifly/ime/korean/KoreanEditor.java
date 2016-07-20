package com.eostek.scifly.ime.korean;

import com.eostek.scifly.ime.common.TextEditor;

public class KoreanEditor  extends TextEditor{

    @Override
    protected boolean doCompose(int keyCode) {
        return false;
    }

}
