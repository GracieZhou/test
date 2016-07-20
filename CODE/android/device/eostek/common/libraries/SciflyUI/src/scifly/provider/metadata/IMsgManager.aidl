
package scifly.provider.metadata;

import scifly.provider.metadata.Msg;
import scifly.provider.metadata.IMsgStateListener;

interface IMsgManager{
    void putCommand(in Msg msg);
    void setMsgStateChangeListener(IMsgStateListener listener);
}