
package scifly.virtualmouse;

import android.view.KeyEvent;

/** {@hide} */
interface IVirtualMouseManager {
    // the virtual mouse enabled?
    boolean isVirtualMouseEnabled();

    void setVirtualMouseEnabled(boolean enabled);

    // toggle virtual mouse
    void toggle();

    // dispatch key event
    boolean dispatchKeyEvent(in KeyEvent event);
}
