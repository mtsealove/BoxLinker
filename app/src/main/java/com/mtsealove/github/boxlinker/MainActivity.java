package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.location.*;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.mtsealove.github.boxlinker.Design.GoView;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    GoView KindView, PhotoView, MessageView, PayView;
    TextView stTv, dstTv;
    EditText name1Et, contact1Et, name2Et, contact2Et;
    Button payBtn;
    String stAddr = null, dstAddr = null;
    public static final int Start = 100, End = 200;
    int size = 0, weight = 0;

    //지오코딩
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        KindView = findViewById(R.id.KindView);
        PhotoView = findViewById(R.id.PhotoView);
        MessageView = findViewById(R.id.MessageView);
        PayView = findViewById(R.id.PayView);

        stTv = findViewById(R.id.stTv);
        dstTv = findViewById(R.id.dstTv);

        stTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAddress(Start);
            }
        });
        dstTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAddress(End);
            }
        });

        name1Et = findViewById(R.id.name1Et);
        contact1Et = findViewById(R.id.contact1Et);
        name2Et = findViewById(R.id.name2Et);
        contact2Et = findViewById(R.id.contact2Et);
        payBtn = findViewById(R.id.payBtn);

        SetLocation();
        initGoViews();
        SetContactEt();
    }

    //클릭 뷰 설정
    private void initGoViews() {
        KindView.SetTitle("화물 종류 입력");
        KindView.SetCallback("");
        KindView.getRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSizeDialog();
            }
        });
        PhotoView.SetTitle("사진 선택");
        PhotoView.SetCallback("");
        MessageView.SetTitle("메세지 입력");
        MessageView.SetCallback("");
        PayView.SetTitle("결제 방법 선택");
        PayView.SetCallback("");
    }

    //전화번호 설정
    @SuppressLint("MissingPermission")
    private void SetContactEt() {
        TextWatcher textWatcher = new PhoneNumberFormattingTextWatcher();
        contact1Et.addTextChangedListener(textWatcher);
        contact2Et.addTextChangedListener(textWatcher);

        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        contact1Et.setText(PhoneNum);
    }

    @SuppressLint("MissingPermission")
    private void SetLocation() {
        geocoder = new Geocoder(this);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
            GeoCode(location);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GeoCode(location);
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

    private void GeoCode(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            if (addresses != null && addresses.size() != 0) {
                stAddr = addresses.get(0).getAddressLine(0);
                stTv.setText(stAddr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //주소 찾기 액티비티로 이동
    private void SetAddress(int requestCode) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Start:
                    stAddr = data.getStringExtra("address");
                    stTv.setText(stAddr);
                    break;
                case End:
                    dstAddr = data.getStringExtra("address");
                    dstTv.setText(dstAddr);
                    break;
            }
        }
    }

    private void ShowSizeDialog() {
        //뷰 inflate
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_item_size, null, false);
        final EditText sizeEt = view.findViewById(R.id.sizeEt), weightEt = view.findViewById(R.id.weightEt);
        Button confirmSizeBtn = view.findViewById(R.id.confirmSizeBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        confirmSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizeEt.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "크기를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (weightEt.getText().toString().length() == 0) {
                    Toast.makeText(MainActivity.this, "무게를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    size = Integer.parseInt(sizeEt.getText().toString());
                    weight = Integer.parseInt(weightEt.getText().toString());
                    KindView.SetCallback("크기: "+size+"cm 무게: "+weight+"Kg");
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
