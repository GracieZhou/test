
package scifly;

import scifly.virtualmouse.IVirtualMouseManager;
import scifly.thememanager.IThemeManager; 
import scifly.security.ISecurityManager;

interface ISciflyManager {

    // return virtual mouse service 
    IVirtualMouseManager getVirtualMouseManager();

    // return theme 
    IThemeManager getThemeManager();

    // return security manager 
    ISecurityManager getSecurityManager();
}