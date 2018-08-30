package cn.lenovo.reportdeviceinfo.httpRequest;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import cn.lenovo.reportdeviceinfo.json.Token;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private final String TAG = "SF-RD-Token";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Log.d(TAG, "response.code=" + response.code());

        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            Log.d(TAG, "静默自动刷新Token,然后重新请求数据");
            //同步请求方式，获取最新的Token
            String newToken = getNewToken();
            //使用新的Token，创建新的请求
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", newToken)
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        if (response.code() == 400) {
            return true;
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String getNewToken() {
        final String[] newToken = {""};
        // 通过一个特定的接口获取新的token
        OkHttpClientUtil util = OkHttpClientUtil.getInstance();
        util._postAsyn(UrlConstant.TO_TOKEN, new OkHttpClientUtil.ResultCallback<Token>() {
            @Override
            public void onError(Request request, Exception e) {
                newToken[0] = "";
                Log.d(TAG, "get Token Error = " + e.getMessage());
            }

            @Override
            public void onResponse(Token response) {
                newToken[0] = response.token_type + " " + response.access_token;
                Log.d(TAG, "get Token success = " + newToken[0]);
            }
        });

        //等token获取到再返回
        while (true) {
            if (!TextUtils.isEmpty(newToken[0])) {
                return newToken[0];
            }
        }
    }
}