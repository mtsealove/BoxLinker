package com.mtsealove.github.boxlinker;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.loader.content.CursorLoader;
import com.google.gson.JsonObject;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.*;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity{
    Intent intent;
    ReqOrder order;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        intent = getIntent();
        order = (ReqOrder) intent.getSerializableExtra("order");
        imageUri = intent.getParcelableExtra("uri");

        GetLatLng();
    }

    //기본 위경도 설정
    private void GetLatLng() {
        String defaultAddr = order.getStdAddr();
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(defaultAddr, 10);
            if (addressList != null && addressList.size() != 0) {
                order.setLatitude(addressList.get(0).getLatitude());
                order.setLongitude(addressList.get(0).getLongitude());
                MakeOrder();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //주문 만들기
    private void MakeOrder() {
        MultipartBody.Part body1 = prepareFilePart("imgFile", imageUri);
        RestAPI restAPI = new RestAPI(this);
        Call<Res> call = restAPI.getRetrofitService().CreateOrder(order, body1);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if (response.isSuccessful()) {
                    if (response.body().isResult()) {    //주문 성공
                        Toast.makeText(OrderActivity.this, "주문이 신청되었습니다", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(OrderActivity.this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {
                Log.e("orderF", t.toString());
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(getRealPathFromURI(fileUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void GetToss(int amount) {
        TossAPI tossAPI = new TossAPI(this);
        JsonObject object = new JsonObject();
        object.addProperty("apiKey", "0e40ad7c7ddb4693bc186a63942cd924");
        object.addProperty("bankName", "국민");
        object.addProperty("bankAccountNo", "05100204189909");
        object.addProperty("amount", amount);
        object.addProperty("message", "토스입금");
        Call<ResToss> call = tossAPI.getRetrofitService().getToss(object);
        call.enqueue(new Callback<ResToss>() {
            @Override
            public void onResponse(Call<ResToss> call, Response<ResToss> response) {
                if (response.isSuccessful()) {
                    Log.e("toss", response.body().toString());
                    String url = response.body().getSuccess().getLink();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Log.e("toss", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResToss> call, Throwable t) {
                Log.e("toss", t.toString());
            }
        });
    }
}
