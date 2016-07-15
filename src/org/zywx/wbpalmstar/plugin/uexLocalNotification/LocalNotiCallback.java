package org.zywx.wbpalmstar.plugin.uexLocalNotification;

public interface LocalNotiCallback {
    void onMessage(CallbackResultVO resultVO);
    void onActive(CallbackResultVO resultVO);
}
