
package com.eostek.wasuwidgethost.util;

/**
 * projectName： WasuWidgetHost.
 * moduleName： Constants.java
 *
 * @author fenoss.hu
 * @version 1.0.0
 * @time  2014-8-17 7:59:53 pm
 * @Copyright © 2014 Eos Inc.
 */
public final class Constants {

    private Constants() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static final String SERVERURL = "http://bs3-api.sdk.wasu.tv/XmlData/Recommend?tag=movie";

    public static final String EOSTEK_WIDGET_START = "com.eostek.widgethost.start";

    public static final String EOSTEK_WIDGET_STOP = "com.eostek.widgethost.stop";

    public static final String EOSTEK_CONNECTIVITY_CHANGE = "com.eostek.network_ok";

    /**
     *  MovieWidgetProvideer Handler.
     */
    public static final int IMGFLIPPER_TIME = 10000;

    public static final int DATA_ACCESS_SUCCESS = 0x02;
    
    /**
     * WeChatProvider.java
     * REQUEST_ACCESS_TOKEN_URL:base address.
     * REQUEST_QR_TICKET_URL:access ticket for geting image.
     * REQUEST_QR_IMAGE_URL:get image about qrcode.
     */
    
    public static final String REQUEST_ACCESS_TOKEN_URL = "http://isynergy.88popo.com/requestToken";

    public static final String REQUEST_QR_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";

    public static final String REQUEST_QR_IMAGE_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    
    /**
     * first step.
     */
    public static final int GET_TOCKEN = 0;

    /**
     * second step.
     */
    public static final int GET_TICKET = 1;
    
    /**
     * third step.
     */

    public static final int GET_QR_IMAGE = 2;

}
