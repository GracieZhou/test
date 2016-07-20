package com.android.settings.datetimecity;

import java.io.InputStream;
import java.util.List;

public interface TimezoneParser {
	 /** 
     * Timezone object set is obtained by parsing the input stream
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<Timezone> parse(InputStream is) throws Exception;  
      
    /** 
     * A string of Timezone forms is obtained by the XML object collection. 
     * @param timezones 
     * @return 
     * @throws Exception 
     */  
    public String serialize(List<Timezone> timezones) throws Exception;  

}

