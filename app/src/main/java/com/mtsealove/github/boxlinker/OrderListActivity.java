package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.mtsealove.github.boxlinker.Design.OrderRecyclerAdapter;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ResOrderSm;
import com.mtsealove.github.boxlinker.Restful.RestAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    String Phone;
    RecyclerView orderRv;
    ProgressDialog progressDialog;
    static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        orderRv = findViewById(R.id.orderRv);
        drawerLayout = findViewById(R.id.drawerLayout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRv.setLayoutManager(layoutManager);

        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        Phone = getPhone();
        setAdapter();
    }

    @SuppressLint("MissingPermission")
    private String getPhone() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        String s1 = PhoneNum.substring(0, 3);
        String s2 = PhoneNum.substring(3, 7);
        String s3 = PhoneNum.substring(7, 11);
        PhoneNum = s1 + "-" + s2 + "-" + s3;

        return PhoneNum;
    }


    private void setAdapter() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();
        RestAPI restAPI = new RestAPI(this);
        Call<List<ResOrderSm>> call = restAPI.getRetrofitService().GetOrderList(Phone);
        call.enqueue(new Callback<List<ResOrderSm>>() {
            @Override
            public void onResponse(Call<List<ResOrderSm>> call, Response<List<ResOrderSm>> response) {
                if (response.isSuccessful()) {
                    List<ResOrderSm> list = response.body();
                    OrderRecyclerAdapter adapter = new OrderRecyclerAdapter(OrderListActivity.this);
                    for (ResOrderSm orderSm : list) {
                        adapter.addItem(orderSm);
                    }
                    orderRv.setAdapter(adapter);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<ResOrderSm>> call, Throwable t) {

            }
        });
    }

    public static void OpenDrawer() {
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
