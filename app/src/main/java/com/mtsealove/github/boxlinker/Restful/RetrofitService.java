package com.mtsealove.github.boxlinker.Restful;

import com.google.gson.JsonObject;
import okhttp3.MultipartBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface RetrofitService {
    @POST("/SignUp")
    Call<Res> SignUp(@Body ReqSignUp reqSignUp);

    @POST("/Login")
    Call<ResLogin> Login(@Body ReqLogin reqLogin);

    @Multipart
    @POST("/MakeOrder")
    Call<Res> CreateOrder(@Part("info") ReqOrder order, @Part MultipartBody.Part imgFile);

    @GET("/OrderList")
    Call<List<ResOrderSm>> GetOrderList(@Query("phone") String phone);

    @GET("/Order")
    Call<ResOrder> GetOrderById(@Query("OrderID") String orderID);

    @GET("/Recent")
    Call<ResRecent> GetRecent(@Query("phone") String phone);

    @POST("/transfer-web/linkgen-api/link")
    Call<ResToss> getToss(@Body JsonObject jsonObject);
}
