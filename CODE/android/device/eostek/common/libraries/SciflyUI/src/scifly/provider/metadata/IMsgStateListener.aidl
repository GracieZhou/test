
package scifly.provider.metadata;

import scifly.provider.metadata.Msg;

interface IMsgStateListener{
    void stateChanged(in Msg msg);
}