package cn.lenovo.reportdeviceinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppManager {
    private static final String TAG = "chao";

    private Context mContext;
    private Handler mHandler;
    private PackageManager pm;

    private ActivityManager manager;
    private String mCurrentPkgName;

    public AppManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        pm = mContext.getPackageManager();
        getCurrentApplicationInfo();
    }

    /**
     * get current app info
     */
    private void getCurrentApplicationInfo() {
        manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        CurrentAppTimerTask mAppTimerTask = new CurrentAppTimerTask();
        Timer mAppTimer = new Timer();
        mAppTimer.schedule(mAppTimerTask, 0, 3000);
    }

    class CurrentAppTimerTask extends TimerTask {
        @Override
        public void run() {
            List<ActivityManager.RunningTaskInfo> runningTask = manager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTask.get(0);
            String packageName = runningTaskInfo.baseActivity.getPackageName();
            //Log.d(TAG, "CurrentAppTimerTask packageName = " + packageName);
            if (mCurrentPkgName == null) {
                mCurrentPkgName = packageName;
            } else if (!mCurrentPkgName.equals(packageName)) {
                mCurrentPkgName = packageName;

                Message message = new Message();
                message.what = DeviceService.MSG_OPEN_APP;
                message.obj = packageName;
                mHandler.sendMessage(message);
            }
        }
    }

    private String getProgramNameByPackageName(String packageName) {
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
