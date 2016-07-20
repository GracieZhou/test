package com.google.tv.eoslauncher.model;

public class BackendFunctionSwitch {
    public String functionName;
    public Boolean functionStatus;
    
    public  BackendFunctionSwitch(String functionName, Boolean functionStatus) {
        this.functionName = functionName;
        this.functionStatus = functionStatus;
    }
    
}
