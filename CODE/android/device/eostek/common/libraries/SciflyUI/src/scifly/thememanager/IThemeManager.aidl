
package scifly.thememanager;
import scifly.thememanager.IThemeChangeListener;

/** {@hide} */
interface IThemeManager {
    // change theme by using the pointed path.
    void changeTheme(String themePath,IThemeChangeListener listener,boolean isUnzip);
    
    // set debug mode.
    void debugEnable(boolean enable);
    
    // rollback.
    void themeRollBack(IThemeChangeListener listener);
}
