package org.zywx.wbpalmstar.plugin.uexLocalNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.zywx.wbpalmstar.engine.EBrowserActivity;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

public class SNotification {

    public static int mId;
    public static String customSound;


    @SuppressWarnings("deprecation")
    public static void notification(Context context, Alerm alerm) {
        Log.e("==notification==", "===notification=start=");
        Intent notyIntent = new Intent(context, EBrowserActivity.class);
        notyIntent.setAction(Const.ACTION_TAG);
        notyIntent.setPackage(context.getPackageName());
        notyIntent.putExtra(Const.KEY_DATA, alerm);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int iconId = EUExUtil.getResDrawableID("icon");
        Notification notification = new Notification(iconId, alerm.title,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification = initType(notification,alerm.getMode());
        notification.setLatestEventInfo(context, alerm.title, alerm.content,
                contentIntent);
        NotificationManager mMgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mId = alerm.notifyId.hashCode();
        mMgr.notify(mId, notification);
        EUexLocalNotify.addToMap(alerm.notifyId, mId);
        Log.e("==notification==", "===notification=end=");
    }

    /**
     * set notify type
     *
     */
    private static Notification initType(Notification notification, String ringType) {
        if ("sound".equals(ringType)) {
            // sound notification
            if (TextUtils.isEmpty(customSound)) {
                notification.defaults = Notification.DEFAULT_SOUND;
            } else {
                Uri sound = Uri.parse("file://" + customSound);
                if (sound != null) {
                    notification.sound = sound;
                } else {
                    notification.defaults = Notification.DEFAULT_SOUND;
                }
            }
        } else if ("vibrate".equals(ringType)) {
            // vibrate notification
            notification.defaults = Notification.DEFAULT_VIBRATE;
        } else if ("both".equals(ringType)) {
            // vibrate and sound
            if (TextUtils.isEmpty(customSound)) {
                notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
            } else {
                Uri sound = Uri.parse("file://" + customSound);
                if (sound != null) {
                    notification.sound = sound;
                    notification.defaults = Notification.DEFAULT_VIBRATE;
                } else {
                    notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                }
            }
        } else if("mute".equals(ringType)){
            // nothing to do
        } else if("system".equals(ringType) || "default".equals(ringType)){
            notification.defaults |= Notification.DEFAULT_SOUND; // 默认铃声
        }
        return notification;
    }
}