package org.zywx.wbpalmstar.plugin.uexLocalNotification;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.engine.EBrowserActivity;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EUexLocalNotify extends EUExBase {
    private static final String TAG = "jsonData";
    private static final String TAG_LOG = "uexLocalNotify";
    private NotificationManager notificationManager;
    private static Map<String, Integer> map = new HashMap<String, Integer>();
    public static LocalNotiCallback mLocalNotiCallback;

    public EUexLocalNotify(Context context, EBrowserView inParent) {
        super(context, inParent);
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        initLocalNotiCallback();
    }

    public static void onActivityCreate(Context context) {
        if (context instanceof EBrowserActivity){
            Intent intent = ((Activity) context).getIntent();
            if (intent != null){
                ((Activity) context).setIntent(intent);//将该intent传递到onResume生命周期处理，此时插件已初始化完成
            }
        }
    }

    public static void onActivityResume(Context context){
        if (context instanceof EBrowserActivity){
            Intent intent = ((Activity) context).getIntent();
            if (intent != null){
                handleNotificationIntent(context, intent);
            }
        }
    }

    public static void onActivityNewIntent(Context context, Intent intent) {
        if (context instanceof EBrowserActivity && intent != null){
            handleNotificationIntent(context, intent);
        }
    }

    private static void handleNotificationIntent(Context context, Intent intent) {
        String action = intent.getAction();
        if (Const.ACTION_TAG.equals(action) &&
                intent.getPackage().equals(context.getPackageName())){
            Alerm result = (Alerm) intent.getSerializableExtra(Const.KEY_DATA);
            if (result != null && EUexLocalNotify.mLocalNotiCallback != null){
                final CallbackResultVO resultVO = new CallbackResultVO(result.notifyId,
                        result.content, result.extras);
                mLocalNotiCallback.onActive(resultVO);
            }
        }
    }

    private void initLocalNotiCallback() {
        mLocalNotiCallback = new LocalNotiCallback(){

            @Override
            public void onMessage(CallbackResultVO resultVO) {
                callbackPlugin2Js(Const.ON_MESSAGE, resultVO);
            }

            @Override
            public void onActive(CallbackResultVO resultVO) {
                callbackPlugin2Js(Const.ON_ACTIVE, resultVO);
            }
        };
    }

    public static void addToMap(String notifyId, int id) {
        map.put(notifyId, id);
    }

    public void add(String[] parm) {
        if (parm.length < 7) {
            return;
        }
        try {
            String nId = parm[0];//notification ID
            String time = parm[1];//首次提醒时间
            String msg = parm[3];//通知显示的内容
            String mode = parm[5];//提示音，对应参数ringPath
            Log.i(TAG, "mode=" + mode);
            String interval = parm[6];//循环周期
            Log.i(TAG, "interval=" + interval);
            String extras = null;
            if (parm.length > 8){
                extras = parm[8];
            }
            int appNameId = EUExUtil.getResStringID("app_name");
            String title = mContext.getString(appNameId);
            long startTime = Long.parseLong(time);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(startTime);
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);
            int month = c.get(Calendar.MONTH);
            int day_of_month = c.get(Calendar.DAY_OF_MONTH);
            int day_of_week = c.get(Calendar.DAY_OF_WEEK);
            int day_of_year = c.get(Calendar.DAY_OF_YEAR);
            Alerm alerm = new Alerm();
            alerm.mode = mode;
            alerm.title = title;
            alerm.content = msg;
            alerm.notifyId = nId;
            alerm.minute = m;
            alerm.hour = h;
            alerm.month = month;
            alerm.start_time = startTime;
            alerm.day_of_month = day_of_month;
            alerm.day_of_week = day_of_week;
            alerm.day_of_year = day_of_year;
            alerm.interval = interval;
            alerm.extras = extras;
            EAlarmReceiver.addAlerm(mContext, alerm);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void remove(String[] parm) {
        String nId = parm[0];
        EAlarmReceiver.cancelAlerm(mContext, nId);
        if (map != null && map.containsKey(nId)) {
            int id = map.get(nId);
            map.remove(nId);
            if (notificationManager != null) {
                notificationManager.cancel(id);
            }
        }
    }

    public void removeAll(String[] parm) {
        EAlarmReceiver.cancelAlermAll(mContext);
        if (notificationManager != null) {
            notificationManager.cancelAll();
            if (map != null) {
                map.clear();
            }
        }
    }

    @Override
    protected boolean clean() {
        if (map != null) {
            map.clear();
        }
        return false;
    }

    private void callbackPlugin2Js(String methodName, CallbackResultVO resultVO) {
        String js = SCRIPT_HEADER + "if(" + methodName + "){" + methodName +
                "('" + resultVO.getId() + "','" + resultVO.getMessage() + "','" +
                resultVO.getExtras() + "'" + SCRIPT_TAIL;
        BDebug.i(TAG_LOG, "callbackPlugin2Js:" + js);
        evaluateScript("root", 0, js);
    }

}