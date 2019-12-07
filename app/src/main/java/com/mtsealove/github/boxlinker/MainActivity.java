package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.icu.text.UnicodeSetSpanner;
import android.location.*;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.loader.content.CursorLoader;
import com.mtsealove.github.boxlinker.Design.GoView;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int Start = 100, End = 200, ImageFile = 300;
    GoView KindView, PhotoView, MessageView, PayView;
    TextView stTv, dstTv, msgTv;
    EditText name1Et, contact1Et, name2Et, contact2Et;
    ImageView imgIv;
    Button payBtn;

    //위치
    Geocoder geocoder;
    LocationManager lm;

    //폼 데이터
    String stAddr = null, dstAddr = null, msg = null, imagePath = null;
    int size = 0, weight = 0;

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
        imgIv = findViewById(R.id.imgIV);

        imgIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(20);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("사진 삭제")
                        .setMessage("사진을 삭제하시겠습니까?")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imagePath = null;
                        imgIv.setVisibility(View.GONE);
                    }
                });
                builder.create().show();
                return false;
            }
        });

        msgTv = findViewById(R.id.msgTv);
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
        PhotoView.getRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });
        MessageView.SetTitle("메세지 입력");
        MessageView.SetCallback("");
        MessageView.getRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMsgDialog();
            }
        });
        PayView.SetTitle("결제 방법 선택");
        PayView.SetCallback("");
        PayView.getRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPayDialog();
            }
        });
    }

    //전화번호 입력 설정
    @SuppressLint("MissingPermission")
    private void SetContactEt() {   //입력 시 전화번호 형식으로 자동 변환
        TextWatcher textWatcher = new PhoneNumberFormattingTextWatcher();
        contact1Et.addTextChangedListener(textWatcher);
        contact2Et.addTextChangedListener(textWatcher);

        //전화번호 읽어 화면에 출력
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        //전화번호 넣고 변환 불가
        contact1Et.setText(PhoneNum);
        contact1Et.setEnabled(false);
    }

    @SuppressLint("MissingPermission")
    private void SetLocation() {
        geocoder = new Geocoder(this);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
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
            lm.removeUpdates(this);
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

    //위경도를 주소로 변환해 출력
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
                case ImageFile:
                    Uri selectImage = data.getData();
                    imagePath = getRealPathFromURI(selectImage);
                    Log.e("imagePath", imagePath);
                    break;
            }
        }
    }

    //크기 입력 다이얼로그 출력
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
                    KindView.SetCallback("크기: " + size + "cm 무게: " + weight + "Kg");
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    //메시지 입력 다이얼로그 출력
    private void ShowMsgDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_msg, null, false);
        final EditText msgEt = view.findViewById(R.id.msgEt);
        if (msg != null && msg.length() != 0) {
            msgEt.setText(msg);
        }
        Button confirmBtn = view.findViewById(R.id.confirmMsgBtn);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msgEt.getText().toString();
                dialog.dismiss();
                //데이터가 입력되면 화면에 표시
                if (msg != null && msg.length() != 0) {
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    msgTv.setText(msg);
                    msgTv.setVisibility(View.VISIBLE);
                    msgTv.setAnimation(animation);
                } else {
                    msgTv.setVisibility(View.GONE);
                }
            }
        });
    }

    //결제 방법 다이얼로그 출력
    private void ShowPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_pay_method, null, false);
        ListView methodLv = view.findViewById(R.id.payLv);
        ArrayList<String> method = new ArrayList<>();

        builder.setView(view);
        method.add("카드 결제");
        method.add("무통장 입금");
        method.add("가상 계좌");

        final AlertDialog dialog = builder.create();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, method);
        methodLv.setAdapter(arrayAdapter);
        methodLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                String pay = "";
                switch (position) {
                    case 0:
                        pay = "카드 결제";
                        break;
                    case 1:
                        pay = "무통장 입금";
                        break;
                    case 2:
                        pay = "가상 계좌";
                        break;
                }
                PayView.SetCallback(pay);
            }
        });
        dialog.show();
    }

    //이미지 얻기
    private void ChooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intent, "상품 파일 선택");

        startActivityForResult(chooser, ImageFile);
    }

    //이미지 경로 불러오기
    private String getRealPathFromURI(Uri contentUri) {
        //이미지 경로를 절대 경로로 변환
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
            imgIv.setImageBitmap(bm);
            imgIv.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }


}
