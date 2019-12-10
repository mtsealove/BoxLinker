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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.CursorLoader;
import com.mtsealove.github.boxlinker.Design.GoView;
import com.mtsealove.github.boxlinker.Design.SlideView;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ReqOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int Start = 100, End = 200, ImageFile = 300, Order = 400;
    GoView KindView, PhotoView, MessageView, PayView;
    TextView stTv, dstTv, msgTv, priceTv;
    EditText name1Et, contact1Et, name2Et, contact2Et;
    ImageView imgIv;
    Button payBtn;
    SlideView slideView;
    public static DrawerLayout drawerLayout;

    //위치
    Geocoder geocoder;
    LocationManager lm;

    //폼 데이터
    String stAddr = null, dstAddr = null, msg = null, imagePath = null;
    String stName = null, stPhone = null, dstName = null, dstPhone = null, payMethod = null;
    int size = 0, weight = 0, price = 0;
    Uri imgUri;

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
        priceTv = findViewById(R.id.priceTv);

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

        slideView = findViewById(R.id.slideView);
        slideView.CheckLogin();
        drawerLayout = findViewById(R.id.drawerLayout);
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

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInput();
            }
        });
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

    //현재 위치 찾기
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

    //현재 위치 찾기 리스너
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

    //크기 입력 다이얼로그 출력
    private void ShowSizeDialog() {
        if (stAddr == null) {
            Toast.makeText(this, "출발지를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        } else if (dstAddr == null) {
            Toast.makeText(this, "도착지를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }
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
                    SetPrice();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void SetPrice() {
        double lat1, lon1, lat2, lon2;
        geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(stAddr, 2);
            lat1 = addressList.get(0).getLatitude();
            lon1 = addressList.get(0).getLongitude();
            addressList = geocoder.getFromLocationName(dstAddr, 2);
            lat2 = addressList.get(0).getLatitude();
            lon2 = addressList.get(0).getLongitude();

            double distance = DistanceManager.distance(lat1, lon1, lat2, lon2);
            price = (((int) (distance + size + weight) / 12) / 100) * 100;
            priceTv.setText("결제 금액: " + price + "원");
            priceTv.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                payMethod = pay;
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

    //네비바 열기
    public static void OpenDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    //네비바 닫기
    public static void CloseDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void CheckInput() {
        stName = name1Et.getText().toString();
        stPhone = contact1Et.getText().toString();
        dstName = name2Et.getText().toString();
        dstPhone = contact2Et.getText().toString();
        if (stAddr == null || stAddr.length() == 0) {
            Toast.makeText(this, "출발지를 선택하세요", Toast.LENGTH_SHORT).show();
        } else if (stName.length() == 0) {
            Toast.makeText(this, "보내는 사람 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (stPhone.length() == 0) {
            Toast.makeText(this, "보내는 사람 연락처를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (dstAddr == null || dstAddr.length() == 0) {
            Toast.makeText(this, "도착지를 선택하세요", Toast.LENGTH_SHORT).show();
        } else if (dstName.length() == 0) {
            Toast.makeText(this, "받는 사람 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (dstPhone.length() == 0) {
            Toast.makeText(this, "받는 사람 연락처를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (size <= 0) {
            Toast.makeText(this, "화물 크기를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (weight <= 0) {
            Toast.makeText(this, "무게를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (imgUri == null) {
            Toast.makeText(this, "상품 사진을 선택하세요", Toast.LENGTH_SHORT).show();
        } else if (payMethod == null) {
            Toast.makeText(this, "결제 방법을 선택하세요", Toast.LENGTH_SHORT).show();
        } else { //모든 데이터 입력 완료
            ReqOrder order = new ReqOrder(stPhone, stPhone, stName, stAddr, dstPhone, dstName, dstAddr, payMethod, size, weight, msg, price);
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("order", order);
            intent.putExtra("uri", imgUri);
            startActivityForResult(intent, Order);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //로그인 확인
        slideView.CheckLogin();
    }

    @Override
    public void onBackPressed() {
        //네비바 닫기
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                    imgUri = selectImage;
                    imagePath = getRealPathFromURI(selectImage);
                    Log.e("imagePath", imagePath);
                    break;
                case Order:
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    }
}
