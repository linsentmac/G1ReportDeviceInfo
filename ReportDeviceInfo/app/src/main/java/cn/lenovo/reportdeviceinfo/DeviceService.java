package cn.lenovo.reportdeviceinfo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.lenovo.reportdeviceinfo.httpRequest.OkHttpClientUtil;
import cn.lenovo.reportdeviceinfo.httpRequest.UrlConstant;
import cn.lenovo.reportdeviceinfo.network.NetState;
import okhttp3.Request;

public class DeviceService extends Service {
    public static String MSG_CMD = "MSG_CMD";
    public final static int MSG_POWER_ON = 1;
    public final static int MSG_OPEN_APP = 2;
    public final static int MSG_DELAY_REPORT_POWER_ON = 3;

    private OkHttpClientUtil okHttpClientUtil;
    private DeviceStatus mDeviceStatus;
    private LocationService locationService;
    private MyHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("chao", "cn.lenovo.reportdeviceinfo.DeviceService onCreate");

        okHttpClientUtil = OkHttpClientUtil.getInstance();
        mDeviceStatus = DeviceStatus.getInstance(this);
        //在AppManager中加入TimerTask轮询
        mHandler = new MyHandler();
        new AppManager(this, mHandler);

        initLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initLocation() {
        //获取LocationService实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = new LocationService(getApplicationContext());
        locationService.registerListener(mListener);//注册监听
        int type = 0;
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();//开始定位
    }

    //定位结果回调
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError
                    && location.getLocType() != BDLocation.TypeNetWorkException
                    && location.getCountry() != null) {
                double latitude = location.getLatitude();// 纬度
                double longitude = location.getLongitude();// 经度
                getLocation(location);
                Log.d("chao", "BD LocationListener, latitude =  " + latitude + ", longitude = " + longitude);
                //定位成功，上传信息
                reportPowerOnInfo(longitude + "," + latitude);
                locationService.stop();
            }
        }
    };

    private void getLocation(BDLocation location){
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        sb.append(location.getTime());
        sb.append("\nlocType : ");// 定位类型
        sb.append(location.getLocType());
        sb.append("\nlocType description : ");// *****对应的定位类型说明*****
        sb.append(location.getLocTypeDescription());
        sb.append("\nlatitude : ");// 纬度
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");// 经度
        sb.append(location.getLongitude());
        sb.append("\nradius : ");// 半径
        sb.append(location.getRadius());
        sb.append("\nCountryCode : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\nCountry : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\ncitycode : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\ncity : ");// 城市
        sb.append(location.getCity());
        sb.append("\nDistrict : ");// 区
        sb.append(location.getDistrict());
        sb.append("\nStreet : ");// 街道
        sb.append(location.getStreet());
        sb.append("\naddr : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\nDirection(not all devices have value): ");
        sb.append(location.getDirection());// 方向
        sb.append("\nlocationdescribe: ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        Log.d("chao", "BD Location"  + sb.toString());

    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OPEN_APP:
                    String packageName = (String) msg.obj;
                    //在已经上报开机信息的情况下 && 有WiFi的情况下再上报打开App信息
                    if (NetState.getInstance().hasWifiConnection(DeviceService.this)) {
                        reportOpenApp(packageName);
                    }
                    break;

                case MSG_DELAY_REPORT_POWER_ON:
                    String Coordinate = (String) msg.obj;
                    reportPowerOnInfo(Coordinate);
                    break;
            }
        }
    }

    //上报开机与定位信息
    private void reportPowerOnInfo(final String Coordinate) {
        if (mDeviceStatus.isReportPowerOn()) return;
        Log.d("chao", "reportPowerOnInfo start");

        okHttpClientUtil._postAsyn(UrlConstant.POWER_ON, new OkHttpClientUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, final Exception e) {
                Log.d("chao", "reportPowerOnInfo onError : " + e.getMessage());
                Message message = new Message();
                message.what = DeviceService.MSG_DELAY_REPORT_POWER_ON;
                message.obj = Coordinate;
                mHandler.sendMessageDelayed(message, 2000);
            }

            @Override
            public void onResponse(final String response) {
                Log.d("chao", "reportPowerOnInfo onResponse : " + response);
                mDeviceStatus.setReportPowerOn(true);
            }
        }, mDeviceStatus.toPowerOnStatus(Coordinate));
    }

    //TODO 上报关机信息
    private void reportPowerOffInfo() {
        Log.d("chao", "reportPowerOffInfo start");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceSN", mDeviceStatus.getDeviceInfo().getSerial());
            jsonObject.put("clientTime", System.currentTimeMillis() + "");
            jsonObject.put("others", "poweroff");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        okHttpClientUtil._postAsyn(UrlConstant.POWER_OFF, new OkHttpClientUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, final Exception e) {
                Log.d("chao", "reportPowerOffInfo onError : " + e.getMessage());
            }

            @Override
            public void onResponse(final String response) {
                Log.d("chao", "reportPowerOffInfo onResponse : " + response);
            }
        }, jsonObject.toString());
    }

    //上报打开App信息
    private void reportOpenApp(String packageName) {
        Log.d("chao", "reportOpenApp start");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceSN", mDeviceStatus.getDeviceInfo().getSerial());
            jsonObject.put("appName", packageName);
            jsonObject.put("clientTime", "" + System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        okHttpClientUtil._postAsyn(UrlConstant.OPEN_APP, new OkHttpClientUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, final Exception e) {
                Log.d("chao", "reportOpenApp onError : " + e.getMessage());
            }

            @Override
            public void onResponse(final String response) {
                Log.d("chao", "reportOpenApp onResponse : " + response);
            }
        }, jsonObject.toString());
    }

    //TODO 上报错误信息
    private void reportErrorLog(String error) {
        Log.d("chao", "reportErrorLog start");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceSN", mDeviceStatus.getDeviceInfo().getSerial());
            jsonObject.put("error", error);
            jsonObject.put("clientTime", System.currentTimeMillis() + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        okHttpClientUtil._postAsyn(UrlConstant.ERROR_LOG, new OkHttpClientUtil.ResultCallback<String>() {
            @Override
            public void onError(Request request, final Exception e) {
                Log.d("chao", "reportErrorLog onError : " + e.getMessage());
            }

            @Override
            public void onResponse(final String response) {
                Log.d("chao", "reportErrorLog onResponse : " + response);
            }
        }, jsonObject.toString());
    }

}
