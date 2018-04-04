package com.agui.zk.client.util;

import com.agui.zk.client.constants.ZKConstants;

import java.io.UnsupportedEncodingException;

/**
 * Created by gui.a on 2018/4/5.
 *
 * @author xiaowei.li
 */
public class ZKDecoder {

    public static String decode(byte[] bytes){
        try {
            return new String(bytes, ZKConstants.defaultChraterSet);
        } catch (Exception e) {
            throw new RuntimeException("decode error");
        }
    }
}
