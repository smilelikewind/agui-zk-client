package com.agui.zk.client;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.lingshou.util.logger.LoggerConstants;
import com.lingshou.util.logger.LoggerFactory;
import com.lingshou.util.logger.LoggerWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Watcher;
import sun.rmi.runtime.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.agui.zk.client.Logger.ZK_INFO;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ConfigLoader {

    private static ZKClient zkClient= ZKClient.getInstance();



    public static Map<String,String> metaDataCache = new MapMaker()
            .expiration(15,TimeUnit.MINUTES)
            .concurrencyLevel(20)
            .makeComputingMap(new Function<String, String>() {
                @Override
                public String apply(String from) {
                    Watcher value = zkWatcher.get(from);
                    return zkClient.getData(from,value);
                }
            });

    public static Map<String,Watcher> zkWatcher = new MapMaker()
            .concurrencyLevel(20)
            .makeComputingMap(new Function<String, Watcher>() {
                @Override
                public Watcher apply(String from) {
                    return new DataChangeWatcher(from);
                }
            });

    public static String getValue(String key,String defaultValue){
        String zkValue = metaDataCache.get(key);
        return StringUtils.isBlank(zkValue) ? defaultValue : zkValue;
    }

    public static void remove(String key){
        zkWatcher.remove(key);
        metaDataCache.remove(key);
    }

    /**
     * 当zkClient Expired 需要清除所有的Watcher
     */
    public static void clearWathcer(){
        zkWatcher.clear();
    }

}
