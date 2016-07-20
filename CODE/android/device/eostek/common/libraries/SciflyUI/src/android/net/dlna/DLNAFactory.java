
package android.net.dlna;

public class DLNAFactory {
    /**
     * Create DLNA instance
     * 
     * @return DLNA instance
     */
    public static synchronized DLNA CreateInstance() {
        if (instance == null)
            instance = new DLNAImpl();
        return instance;
    }

    private static DLNA instance;
}
