package com.agui.zk.client.util;

import com.agui.zk.client.constants.ZKConstants;

/**
 * Created by gui.a on 2018/4/5.
 *
 * @author xiaowei.li
 */
public class ZKEncoder {

    public static byte[] encode(String value){
        try {
            return value.getBytes(ZKConstants.defaultChraterSet);
        } catch (Exception e) {
            throw new RuntimeException("decode error");
        }
    }
}
