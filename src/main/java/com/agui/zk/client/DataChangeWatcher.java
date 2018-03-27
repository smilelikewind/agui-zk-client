package com.agui.zk.client;

import com.alibaba.fastjson.JSON;
import com.lingshou.util.logger.LoggerFactory;
import com.lingshou.util.logger.LoggerWrapper;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import static com.agui.zk.client.Logger.ZK_INFO;

/**
 * Created by gui.a on 2018/3/21.
 *
 * @author xiaowei.li
 */
public class DataChangeWatcher implements Watcher{

    static LoggerWrapper INFO = LoggerFactory.getValue(ZK_INFO);

    private String dataKey;

    public DataChangeWatcher(String dataKey){
        this.dataKey = dataKey;
    }

    @Override
    public void process(WatchedEvent event) {

        if (event == null){
            return;
        }

        if (event.getType() == null){
            return;
        }

        if (event.getType() == Event.EventType.NodeDataChanged || event.getType() == Event.EventType.NodeCreated){
            INFO.info("[DataChangeWatcher] davaValue changed: " + JSON.toJSONString(event));
            ConfigLoader.remove(dataKey);
        }
    }
}
