
package scifly.middleware.network;

import java.util.List;

import android.net.wifi.ScanResult;

public interface IWifiManager {

    public int getWifiState();

    public boolean isWifiEnabled();

    public List<ScanResult> getScanResults();
}
