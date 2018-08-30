package cn.lenovo.reportdeviceinfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import cn.lenovo.reportdeviceinfo.network.NetState;

public class DeviceInfo {

    private static final String TAG = "RDI-DeviceInfo";
    private static DeviceInfo mInstance;

    private LocationManager locationManager;
    private Context mContext;

    private DeviceInfo(Context context){
        mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

    }

    public static DeviceInfo getInstance(Context context){
        if(mInstance == null){
            mInstance = new DeviceInfo(context);
        }
        return mInstance;
    }

    /**
     * @return 设备序列号
     */
    public String getSerial() {
        return setSerialNumber();
    }

    private String setSerialNumber(){
        String serialNum = "unknow";
        String filePath = Environment.getExternalStorageDirectory() + "/sn/sc_serialno.txt";
        try {
            File file = new File(filePath);//定义一个file对象，用来初始化FileReader
            if(!file.exists()){
                return serialNum;
            }
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s =bReader.readLine()) != null) {
                //逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s + "\n");
                //将读取的字符串添加换行符后累加存放在缓存中
                System.out.println(s);
            }
            bReader.close();
            serialNum = sb.toString();
            Log.d(TAG, "serialN = " + serialNum);
        }catch (Exception e){
            e.printStackTrace();
        }
        return serialNum;
    }

    /**
     * @return A build ID string meant for displaying to the user
     */
    public String getBuildNumber() {
        return Build.VERSION.INCREMENTAL;
    }

    /**
     * @return The end-user-visible name for the end product.
     */
    public String getModel() {
        return Build.MODEL;
    }

    /**
     * @return Android 版本号
     */
    public String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }




    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        Log.d("Tmac", "provide = " + prodiverlist.toString());
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;//网络定位
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;//GPS定位
        }else{
            return LocationManager.PASSIVE_PROVIDER;
            //Toast.makeText(mContext,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        //return null;
    }

    private String provider;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public Location beginLocatioon() {
        //获得位置服务
        //locationManager = mContext.getLocationManager();
        provider = judgeProvider(locationManager);
        //有位置提供器的情况
        if (provider != null) {
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Tmac", "location change = " + location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    Log.d("Tmac", "location onStatusChanged = ");
                }

                @Override
                public void onProviderEnabled(String s) {
                    Log.d("Tmac", "location onProviderEnabled = ");
                }

                @Override
                public void onProviderDisabled(String s) {
                    Log.d("Tmac", "location onProviderDisabled = ");
                }
            });

            return locationManager.getLastKnownLocation(provider);
        }else{
            //不存在位置提供器的情况
            Toast.makeText(mContext,"不存在位置提供器的情况",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public String getNetWorkType(){
        String netType = "unknow";
        int type = NetState.getNetWorkConnectionType(mContext);
        if(type == 0){
            netType = "4g";
        }else if(type == 1){
            netType = "wifi";
        }
        return netType;
    }


}
