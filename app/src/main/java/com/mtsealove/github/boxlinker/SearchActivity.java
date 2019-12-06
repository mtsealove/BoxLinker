package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText searchEt;
    int requestCode;
    Intent intent;
    RelativeLayout mapContainer;
    MapView mapView;
    LocationManager locationManager;
    TextView resultTv;
    ImageView searchIv;
    ListView resultLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEt = findViewById(R.id.searchEt);
        mapContainer = findViewById(R.id.mapContainer);
        resultTv = findViewById(R.id.resultTv);
        searchIv = findViewById(R.id.searchIv);
        resultLv = findViewById(R.id.resultLv);

        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);
        if (requestCode == MainActivity.Start) {
            searchEt.setHint("출발지 검색");
        } else if (requestCode == MainActivity.End) {
            searchEt.setHint("도착지 검색");
        }

        initMap();
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });

        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEt.getWindowToken(), 0);
                        Search();
                        break;
                }
                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);

        mapView = new MapView(this);
        mapContainer.addView(mapView);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            SetPoint(location);
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void SetPoint(Location location) {
        Geocoder geocoder = new Geocoder(this);
        //맵뷰 위치 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            if (addresses != null && addresses.size() != 0) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SetPoint(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        //맵뷰 위치 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses != null && addresses.size() != 0) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Search() {
        locationManager.removeUpdates(locationListener);
        String addr = searchEt.getText().toString();
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(addr, 10);
            if (addresses != null && addresses.size() != 0) {
                final ArrayList<String> name = new ArrayList<>();
                final ArrayList<Double> latitudes = new ArrayList<>();
                final ArrayList<Double> longitudes = new ArrayList<>();
                for (Address address : addresses) {
                    name.add(address.getAddressLine(0));
                    latitudes.add(address.getLatitude());
                    longitudes.add(address.getLongitude());
                }
                ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, name);
                resultLv.setAdapter(adapter);
                resultLv.setVisibility(View.VISIBLE);
                resultTv.setVisibility(View.GONE);
                resultLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        SetPoint(latitudes.get(position), longitudes.get(position));
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("주소 선택")
                                .setMessage(name.get(position) + " 을(를) 선택하시겠습니까?")
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("address", name.get(position));
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            } else {
                resultLv.setVisibility(View.GONE);
                resultTv.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
