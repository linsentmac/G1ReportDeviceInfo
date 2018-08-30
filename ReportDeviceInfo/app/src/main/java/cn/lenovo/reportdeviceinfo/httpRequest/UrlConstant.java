package cn.lenovo.reportdeviceinfo.httpRequest;

/**
 * @Date 2018-08-22.
 */
public class UrlConstant {
    private static final String BASE_URL = "https://www.lenovo-mr.com/";
    public static final String OPEN_APP = BASE_URL + "api/device/openApp";
    public static final String POWER_ON = BASE_URL + "api/device/powerOn";
    public static final String POWER_OFF = BASE_URL + "api/device/powerOff";
    public static final String ERROR_LOG = BASE_URL + "api/device/errorLog";
    public static final String TO_TOKEN = BASE_URL + "oauth/token?grant_type=client_credentials";

}
