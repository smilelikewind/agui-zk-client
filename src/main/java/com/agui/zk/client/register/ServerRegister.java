package com.agui.zk.client.register;

import com.agui.zk.client.ZKClient;
import org.apache.zookeeper.data.Stat;

/**
 * Created by gui.a on 2018/3/27.
 *
 * @author xiaowei.li
 */
public class ServerRegister {

    private static ZKClient zkClient = ZKClient.getInstance();

    public static boolean isRegistered(RegisterWatcher watcher) {
        try {
            String zNodePath = watcher.getServerNode();
            Stat state = zkClient.getState(watcher.getServerNode(), watcher);

            if (state != null) {
                return state.getEphemeralOwner() == zkClient.getSessionId();
            }
            return zkClient.createEphemeral(zNodePath, "getKey");
        } catch (Exception e) {
            return false;
        }
    }

}
