package com.agui.zk.client.register;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import static com.agui.zk.client.register.ServerRegister.isRegistered;

/**
 * Created by gui.a on 2018/3/27.
 *
 * @author xiaowei.li
 */
public class RegisterWatcher implements Watcher{

    private ServerCloseCallBack serverClose;

    private String serverNode;

    public RegisterWatcher(String serverNode,ServerCloseCallBack serverClose){
        this.serverNode = serverNode;
        this.serverClose = serverClose;
    }

    public ServerCloseCallBack getServerClose() {
        return serverClose;
    }

    public RegisterWatcher setServerClose(ServerCloseCallBack serverClose) {
        this.serverClose = serverClose;
        return this;
    }

    public String getServerNode() {
        return serverNode;
    }

    public RegisterWatcher setServerNode(String serverNode) {
        this.serverNode = serverNode;
        return this;
    }

    @Override
    public void process(WatchedEvent event) {

        if (event == null || event.getType() == null){
            return;
        }

        if (!isRegistered(this)){
            this.serverClose.close();
        }
    }
}
