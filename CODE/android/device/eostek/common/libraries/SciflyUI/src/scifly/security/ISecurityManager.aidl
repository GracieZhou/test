package scifly.security;

import scifly.security.IOnInstallEnableListener;

/** {@hide} */
interface ISecurityManager {

    void checkPkgFromBlacklist(String pkg, IOnInstallEnableListener listener);
}