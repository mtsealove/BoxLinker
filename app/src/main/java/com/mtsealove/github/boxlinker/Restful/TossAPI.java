package com.mtsealove.github.boxlinker.Restful;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class TossAPI {
    Retrofit retrofit;
    RetrofitService retrofitService;
    OkHttpClient okHttpClient;
    Context context;

    public TossAPI(Context context) {
        this.context = context;
        //통신 연결 클라이언트
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        //데이터를 받아올 API

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder();

        retrofit = builder
                .baseUrl("https://toss.im")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        retrofitService = retrofit.create(RetrofitService.class);
    }

    //바로 사용할 수 있는 인터페이스 반환
    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
}
