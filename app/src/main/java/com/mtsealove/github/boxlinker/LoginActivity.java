package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ReqLogin;
import com.mtsealove.github.boxlinker.Restful.ResLogin;
import com.mtsealove.github.boxlinker.Restful.RestAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText idEt, pwEt;
    Button loginBtn;
    TextView signUpTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEt = findViewById(R.id.idEt);
        idEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        pwEt = findViewById(R.id.pwEt);
        loginBtn = findViewById(R.id.loginBtn);
        signUpTv = findViewById(R.id.signUpTv);
        SystemUiTuner tuner = new SystemUiTuner(this);
        tuner.setStatusBarWhite();

        signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInput();
            }
        });

        SetPhone();
    }

    //자동 전화번호
    @SuppressLint("MissingPermission")
    private void SetPhone() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String PhoneNum = telManager.getLine1Number();
        if (PhoneNum.startsWith("+82")) {
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        idEt.setText(PhoneNum);
        idEt.setEnabled(false);
    }

    private void SignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void CheckInput() {
        if (idEt.getText().toString().length() == 0) {
            Toast.makeText(this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (pwEt.getText().toString().length() == 0) {
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        } else {
            Login();
        }
    }

    private void Login() {
        String phone = idEt.getText().toString();
        String pw = pwEt.getText().toString();
        String token = FirebaseInstanceId.getInstance().getToken();
        ReqLogin reqLogin = new ReqLogin(phone, pw, token);
        RestAPI restAPI = new RestAPI(this);
        Call<ResLogin> call = restAPI.getRetrofitService().Login(reqLogin);
        call.enqueue(new Callback<ResLogin>() {
            @Override
            public void onResponse(Call<ResLogin> call, Response<ResLogin> response) {
                if (response.isSuccessful()) {
                    ResLogin resLogin = response.body();
                    if (resLogin.getName() == null || resLogin.getPhone() == null) {
                        Toast.makeText(LoginActivity.this, "전화번호와 비밀번호를 확인학세요", Toast.LENGTH_SHORT).show();
                    } else {    //로그인 성공
                        SetAccount(resLogin.getPhone(), resLogin.getName());
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResLogin> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //계정 저장
    private void SetAccount(String phone, String name) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phone", phone);
        editor.putString("name", name);
        editor.commit();
    }
}
