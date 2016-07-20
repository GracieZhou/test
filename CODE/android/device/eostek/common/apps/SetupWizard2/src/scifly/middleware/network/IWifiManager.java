
package scifly.middleware.network;

import java.util.List;

import android.net.wifi.WifiManager.ActionListener;

public interface IWifiManager {

    public void connect(WifiConfig config, ActionListener listener);

    public void save(WifiConfig config, ActionListener listener);

    public List<WifiConfig> getConfiguredNetworks();

    public WifiConfig getConfiguredNetworks(int networkId);

    public IpConfig getConfiguration();

    public void setConfiguration(IpConfig config);
}
