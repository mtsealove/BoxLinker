package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.*;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.CursorLoader;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.gson.JsonObject;
import com.mtsealove.github.boxlinker.Design.GoView;
import com.mtsealove.github.boxlinker.Design.SlideView;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ReqOrder;
import com.mtsealove.github.boxlinker.Restful.ResToss;
import com.mtsealove.github.boxlinker.Restful.Success;
import com.mtsealove.github.boxlinker.Restful.TossAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public static final int Start = 100, End = 200, ImageFile = 300, Order = 400, Contact = 500, CAMERA = 600;
    GoView KindView, PhotoView, MessageView;
    TextView stTv, dstTv, msgTv, priceTv;
    EditText name1Et, contact1Et, name2Et, contact2Et;
    ImageView imgIv;
    Button payBtn, getContactBtn;
    SlideView slideView;
    public static DrawerLayout drawerLayout;

    //위치
    Geocoder geocoder;
    LocationManager lm;

    //폼 데이터
    String stAddr = null, dstAddr = null, msg = null, imagePath = null;
    String stName = null, stPhone = null, dstName = null, dstPhone = null;
    int size = 0, weight = 0, price = 0;
    Uri imgUri;

    //결제
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        KindView = findViewById(R.id.KindView);
        PhotoView = findViewById(R.id.PhotoView);
        MessageView = findViewById(R.id.MessageView);
        imgIv = findViewById(R.id.imgIV);
        priceTv = findViewById(R.id.priceTv);
        getContactBtn = findViewById(R.id.getContactBtn);

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
        SetNameEt();
        getMsg();

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInput();
            }
        });
        getContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContact();
            }
        });
        bp = new BillingProcessor(this, getString(R.string.pay_key), this);
        bp.initialize();

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
    }

    //이름 설정
    private void SetNameEt() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String name = pref.getString("name", "");
        name1Et.setText(name);
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
            price = (((int) (distance + 1000 * (size + weight)) / 15) / 100) * 100;
            //쵀대 최소 금액
            if (price < 5000) price = 5000;
            else if (price > 100000) price = 100000;
            priceTv.setText("결제 금액: " + price + "원");
            priceTv.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getMsg() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        msg = pref.getString("msg", "");
        if (msg.length() != 0) {
            msgTv.setVisibility(View.VISIBLE);
            msgTv.setText(msg);
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
        final CheckBox keepCb = view.findViewById(R.id.msgCb);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msgEt.getText().toString();
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                //메시지 저장
                if (keepCb.isChecked()) {
                    editor.putString("msg", msg);
                    editor.commit();
                } else {
                    editor.remove("msg");
                    editor.commit();
                }
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

    //이미지 얻기
    private void ChooseImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진 업로드")
                .setNeutralButton("사진 촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ImageFromCamera();
                    }
                }).setPositiveButton("앨범에서 선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ImageFromAlbum();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intent, "상품 파일 선택");

        startActivityForResult(chooser, ImageFile);
    }

    private void ImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
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

    //연락처 선택
    private void GetContact() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, Contact);
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
        } else { //모든 데이터 입력 완료
            ReqOrder order = new ReqOrder(stPhone, stPhone, stName, stAddr, dstPhone, dstName, dstAddr, size, weight, msg, price);
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("order", order);
            intent.putExtra("uri", imgUri);
            //startActivityForResult(intent, Order);
            String pid = "price" + price;
            bp.purchase(this, pid);
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
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Start:
                    stAddr = data.getStringExtra("address");
                    stTv.setText(stAddr);
                    if (price != 0) SetPrice();
                    break;
                case End:
                    dstAddr = data.getStringExtra("address");
                    dstTv.setText(dstAddr);
                    if (price != 0) SetPrice();
                    break;
                case ImageFile:
                    Uri selectImage = data.getData();
                    imgUri = selectImage;
                    imagePath = getRealPathFromURI(selectImage);
                    Log.e("imagePath", imagePath);
                    break;
                case CAMERA:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgUri = getImageUri(this, imageBitmap);
                    imagePath = getRealPathFromURI(imgUri);
                    break;
                case Order:
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case Contact:
                    SetContact(data);
                    break;
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void SetContact(Intent data) {
        Cursor cursor = getContentResolver().query(data.getData(),
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        cursor.moveToFirst();
        String name = cursor.getString(0);        //0은 이름을 얻어옵니다.
        String number = cursor.getString(1);   //1은 번호를 받아옵니다.
        cursor.close();
        name2Et.setText(name);
        contact2Et.setText(number);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toast.makeText(this, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        ReqOrder order = new ReqOrder(stPhone, stPhone, stName, stAddr, dstPhone, dstName, dstAddr, size, weight, msg, price);
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("uri", imgUri);
        startActivityForResult(intent, Order);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toast.makeText(this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}
