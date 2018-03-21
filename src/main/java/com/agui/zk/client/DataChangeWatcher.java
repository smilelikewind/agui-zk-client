package com.agui.zk.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by gui.a on 2018/3/21.
 *
 * @author xiaowei.li
 */
public class DataChangeWatcher implements Watcher{

    private String dataKey;

    public DataChangeWatcher(String dataKey){
        this.dataKey = dataKey;
    }

    @Override
    public void process(WatchedEvent event) {
        ConfigLoader.remove(dataKey);
    }
}
