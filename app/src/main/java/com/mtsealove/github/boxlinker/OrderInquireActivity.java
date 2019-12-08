package com.mtsealove.github.boxlinker;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ResOrder;
import com.mtsealove.github.boxlinker.Restful.RestAPI;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class OrderInquireActivity extends AppCompatActivity {
    Intent intent;
    String OrderID;
    String tag = getClass().getSimpleName();
    TextView currentLocationTv, statusTv, stTv, dstTv, itemTv, orderIdTv;
    RelativeLayout container;
    static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_inquire);
        orderIdTv = findViewById(R.id.orderIdTv);
        currentLocationTv = findViewById(R.id.currentLocationTv);
        statusTv = findViewById(R.id.statusTv);
        stTv = findViewById(R.id.stTv);
        dstTv = findViewById(R.id.dstTv);
        itemTv = findViewById(R.id.itemTv);
        container = findViewById(R.id.mapContainer);
        drawerLayout = findViewById(R.id.drawerLayout);

        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        intent = getIntent();
        OrderID = intent.getStringExtra("orderID");
        orderIdTv.setText("주문번호: " + OrderID);
        GetOrder();
    }

    private void GetOrder() {
        RestAPI restAPI = new RestAPI(this);
        Call<ResOrder> call = restAPI.getRetrofitService().GetOrderById(OrderID);
        call.enqueue(new Callback<ResOrder>() {
            @Override
            public void onResponse(Call<ResOrder> call, Response<ResOrder> response) {
                if (response.isSuccessful()) {
                    ResOrder resOrder = response.body();
                    SetMap(resOrder.getLatitude(), resOrder.getLongitude());
                    statusTv.setText(resOrder.getStatusName());
                    stTv.setText("출발: " + resOrder.getStdAddr() + " (" + resOrder.getStdName() + ")");
                    dstTv.setText("도착: " + resOrder.getDstAddr() + " (" + resOrder.getDstName() + ")");
                    itemTv.setText("크기: " + resOrder.getSize() + "cm 무게: " + resOrder.getWeight() + "kg");
                    Log.e(tag, resOrder.toString());
                } else {
                    Log.e(tag, response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResOrder> call, Throwable t) {
                Log.e(tag, t.toString());
            }
        });
    }

    private void SetMap(double latitude, double longitude) {
        MapView mapView = new MapView(this);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        MapPOIItem marker = new MapPOIItem();
        marker.setTag(0);
        marker.setItemName("현재 위치");
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
        container.addView(mapView);

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 10);
            if (addressList != null && addressList.size() != 0) {
                String addr = addressList.get(0).getAddressLine(0);
                currentLocationTv.setText("현재 위치: " + addr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void OpenDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
