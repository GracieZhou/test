package com.eostek.scifly.ime.french;

import com.eostek.scifly.ime.common.TextEditor;

public class FrenchEditor extends TextEditor{

    @Override
    protected boolean doCompose(int keyCode) {
        return false;
    }

}
