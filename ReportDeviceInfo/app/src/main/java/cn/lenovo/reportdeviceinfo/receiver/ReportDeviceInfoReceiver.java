package cn.lenovo.reportdeviceinfo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cn.lenovo.reportdeviceinfo.DeviceService;

public class ReportDeviceInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e("ReportDeviceInfoReceive", "开机启动了---------------");
            //context.startService(new Intent(context, DeviceService.class));
        }
    }
}
