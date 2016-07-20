
package com.eostek.isynergy.setmeup.config;

import com.eostek.isynergy.setmeup.common.Constants;

/**
 * 是setmeup 服务的接口，所有的服务类都应该从这个接口派生
 * 
 * @author nickyang
 */
public interface IfService {
    /**
     * 每个服务类的所有服务在这个方法里头进行实现， 如果服务类需要提供多种类似的服务，可以通过serviceType进行区别
     * 
     * @param serviceType 服务类型
     * @param paras 参数，多个参数由,进行区分
     * @return 0 成功
     */
    int doAction(Constants.ACTION_TYPE type, String paras);
}
