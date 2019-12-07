package com.mtsealove.github.boxlinker.Restful;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class RestAPI {
    Retrofit retrofit;
    RetrofitService retrofitService;
    OkHttpClient okHttpClient;
    Context context;

    public RestAPI(Context context) {
        this.context = context;
        //통신 연결 클라이언트
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .build();
        //데이터를 받아올 API

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder();

        retrofit = builder
                .baseUrl(GetIP())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        retrofitService = retrofit.create(RetrofitService.class);
    }

    //바로 사용할 수 있는 인터페이스 반환
    public RetrofitService getRetrofitService() {
        return retrofitService;
    }

    private String GetIP() {
        /*
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        String ip = "http://" + pref.getString("ip", "");
        if (ip == null || ip.length() == 0) {
            ip = "172.30.1.28";
        }
        ip+=":3700";
         */
        String ip="http://172.30.1.28:3700";
        return ip;
    }
}
