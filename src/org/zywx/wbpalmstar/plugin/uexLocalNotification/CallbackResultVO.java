package org.zywx.wbpalmstar.plugin.uexLocalNotification;

import java.io.Serializable;

public class CallbackResultVO implements Serializable {

    private static final long serialVersionUID = 8827111332400094771L;
    private String id;
    private String message;
    private String extras;

    public CallbackResultVO(String notifyId, String content, String extras) {
        this.id = notifyId;
        this.message = content;
        this.extras = extras;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
