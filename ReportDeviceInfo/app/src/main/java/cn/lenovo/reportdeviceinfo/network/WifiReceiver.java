package cn.lenovo.reportdeviceinfo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import cn.lenovo.reportdeviceinfo.DeviceService;
import cn.lenovo.reportdeviceinfo.DeviceStatus;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){
            //signal strength changed
        }
        else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){//wifi连接上与否
            Log.e("WifiReceiver", "网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){
                Log.e("WifiReceiver", "wifi网络连接断开");
            }
            else if(info.getState().equals(NetworkInfo.State.CONNECTED)){

                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                //获取当前wifi名称
                System.out.println("连接到网络 " + wifiInfo.getSSID());
                Log.e("WifiReceiver", "连接到网络 " + wifiInfo.getSSID());
                if(!DeviceStatus.getInstance(context).isReportPowerOn()){
                    sendKetEventToLcService(context);
                }
            }

        }
        else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){//wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

            if(wifistate == WifiManager.WIFI_STATE_DISABLED){
                System.out.println("系统关闭wifi");
                Log.e("WifiReceiver", "系统关闭wifi");
            }
            else if(wifistate == WifiManager.WIFI_STATE_ENABLED){
                Log.e("WifiReceiver", "系统开启wifi");
                System.out.println();
            }
        }

    }


    private void sendKetEventToLcService(Context context) {
        Intent response = new Intent(context, DeviceService.class);
        response.putExtra(DeviceService.MSG_CMD, DeviceService.MSG_POWER_ON);
        context.startService(response);
    }
}
