
package com.eostek.scifly.ime;

import java.util.List;

public interface InputEngineInterface {
    void startEngine();

    List<String> getCandidateList(String spl);
    
    public void addSplString(String spl);
}
