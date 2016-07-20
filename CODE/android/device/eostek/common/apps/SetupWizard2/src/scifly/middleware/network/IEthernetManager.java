
package scifly.middleware.network;


public interface IEthernetManager {

    public IpConfig getConfiguration();

    public void setConfiguration(IpConfig config);

    public boolean isEnabled();

    public void setEnabled(boolean enable);
}
