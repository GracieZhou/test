
package com.bq.tv.traxex.util;

import android.content.Context;

/**
 * Class used to convert dpi to pix.
 */
public class UnitConverter {
    /**
     * Convert dpi to pix.
     * 
     * @param paramInt
     * @param paramContext
     * @return
     */
    public static int dpi2pix(int paramInt, Context paramContext) {
        return (int) (paramContext.getResources().getDisplayMetrics().density * paramInt);
    }
}
