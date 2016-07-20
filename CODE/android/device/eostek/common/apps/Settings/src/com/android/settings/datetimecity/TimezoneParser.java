package com.android.settings.datetimecity;

import java.io.InputStream;
import java.util.List;

public interface TimezoneParser {
	 /** 
     * 解析输入流 得到Timezone对象集合 
     * @param is 
     * @return 
     * @throws Exception 
     */  
    public List<Timezone> parse(InputStream is) throws Exception;  
      
    /** 
     * 序列化Timezone对象集合 得到XML形式的字符串 
     * @param timezones 
     * @return 
     * @throws Exception 
     */  
    public String serialize(List<Timezone> timezones) throws Exception;  

}

