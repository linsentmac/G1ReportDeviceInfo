package cn.lenovo.reportdeviceinfo;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceStatus {
    private static DeviceStatus mInstance;
    private DeviceInfo deviceInfo;
    private boolean isReportPowerOn;

    public static DeviceStatus getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceStatus(context);
        }
        return mInstance;
    }

    private DeviceStatus(Context context) {
        deviceInfo = DeviceInfo.getInstance(context);
    }

    DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    void setReportPowerOn(boolean isReportPowerOn) {
        this.isReportPowerOn = isReportPowerOn;
    }

    public boolean isReportPowerOn() {
        return isReportPowerOn;
    }

    public String toPowerOnStatus(String coordinate) {
        JSONObject powerOnObj = new JSONObject();
        try {
            powerOnObj.put("deviceSN", deviceInfo.getSerial());
            powerOnObj.put("deviceName", "Lenovo AR G1");
            powerOnObj.put("osVersion", "Android " + deviceInfo.getAndroidVersion());
            powerOnObj.put("coordinate", coordinate);
            powerOnObj.put("networkType", deviceInfo.getNetWorkType());
            powerOnObj.put("clientTime", System.currentTimeMillis() + "");
            powerOnObj.put("others", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return powerOnObj.toString();
    }

}
