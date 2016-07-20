/*
** Copyright (c) 2010-2011 MStar Semiconductor, Inc.
** All rights reserved.
**
** Unless otherwise stipulated in writing, any and all information contained
** herein regardless in any format shall remain the sole proprietary of
** MStar Semiconductor Inc. and be kept in strict confidence
** ("MStar Confidential Information") by the recipient.
** Any unauthorized act including without limitation unauthorized disclosure,
** copying, use, reproduction, sale, distribution, modification, disassembling,
** reverse engineering and compiling of the contents of MStar Confidential
** Information is unlawful and strictly prohibited. MStar hereby reserves the
** rights to any and all damages, losses, costs and expenses resulting therefrom.
*/

package com.android.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import java.util.List;

/**
 * native tool for browser
 */

public class MouseControl extends BrowserUtil {
    private static final String LOGTAG = "MouseControl";

    public MouseControl() {
        Log.v(LOGTAG, "mouse controler be created");
    }

    private boolean isBtnDown = false;

    public boolean getBtnDownState() {
        return isBtnDown;
    }

    public void enterDragState() {
        Log.i(LOGTAG, "enter DragState...");
        isBtnDown = true;
        mouseLeftDown();
    }

    public void outDragState() {
        Log.i(LOGTAG, "leave DragState...");
        isBtnDown = false;
        mouseLeftUp();
    }
}

