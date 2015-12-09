package org.zywx.wbpalmstar.plugin.uexLocalNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.zywx.wbpalmstar.engine.EBrowserActivity;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

public class SNotification {

    public static int mId;

    @SuppressWarnings("deprecation")
    public static void notification(Context context, Alerm alerm) {
        Log.e("==notification==", "===notification=start=");
        Intent notyIntent = new Intent(context, EBrowserActivity.class);
        notyIntent.putExtra(Const.KEY_DATA, alerm);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int iconId = EUExUtil.getResDrawableID("icon");
        Notification notification = new Notification(iconId, alerm.title,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND; // 默认铃声
        notification.setLatestEventInfo(context, alerm.title, alerm.content,
                contentIntent);
        NotificationManager mMgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mMgr.notify(mId, notification);
        EUexLocalNotify.addToMap(alerm.notifyId, mId);
        Log.e("==notification==", "===notification=end=");
    }
}