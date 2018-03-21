package com.agui.zk.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by gui.a on 2018/3/19.
 *
 * @author xiaowei.li
 */
public class ConfigLoader {

    private static ZKClient zkClient= ZKClient.getInstance();

    final static Cache<String, String> metaDataCache = CacheBuilder.newBuilder()
            //设置cache的初始大小为10，要合理设置该值
            .initialCapacity(256)
            //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
            .concurrencyLevel(20)
            //设置cache中的数据在写入之后的存活时间为10秒
            .expireAfterWrite(6, TimeUnit.MINUTES)
            //构建cache实例
            .build();
    private static ConcurrentHashMap<String,Watcher> zkWatcher = new ConcurrentHashMap<String, Watcher>();


    public static String getValue(String key,String defaultValue){

        String tmpValue = metaDataCache.getIfPresent(key);

        if (StringUtils.isNotBlank(tmpValue)){
            return tmpValue;
        }

        Watcher value = zkWatcher.get(key);

        if (value == null){
            value = new DataChangeWatcher(key);
            zkWatcher.put(key,value);
        }

        String zkValue = zkClient.getData(key,value);

        if (StringUtils.isBlank(zkValue)){
            zkValue = defaultValue;
        }

        metaDataCache.put(key,zkValue);

        return zkValue;
    }

    public static void remove(String key){
        zkWatcher.remove(key);
    }

}
