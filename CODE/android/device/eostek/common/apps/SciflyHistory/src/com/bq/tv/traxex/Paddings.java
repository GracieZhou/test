
package com.bq.tv.traxex;

/**
 * Class of Paddings.
 */
public class Paddings {

    /**
     * Bottom value.
     */
    public final int mBottom;

    /**
     * Left value.
     */
    public final int mLeft;

    /**
     * Right value.
     */
    public final int mRight;

    /**
     * Top value.
     */
    public final int mTop;

    /**
     * Constructor of Paddings class.
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public Paddings(int left, int top, int right, int bottom) {
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
    }
}
