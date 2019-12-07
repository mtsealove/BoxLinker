package com.mtsealove.github.boxlinker.Restful;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {
    @POST("/SignUp")
    Call<Res> SignUp(@Body ReqSignUp reqSignUp);

    @POST("Login")
    Call<ResLogin> Login(@Body ReqLogin reqLogin);
}
