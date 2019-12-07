package com.mtsealove.github.boxlinker;

import android.annotation.SuppressLint;
import android.icu.text.UnicodeSetSpanner;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;
import com.mtsealove.github.boxlinker.Restful.ReqSignUp;
import com.mtsealove.github.boxlinker.Restful.Res;
import com.mtsealove.github.boxlinker.Restful.RestAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText idEt, pwEt, pwConfirmEt, nameEt;
    Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SystemUiTuner tuner=new SystemUiTuner(this);
        tuner.setStatusBarWhite();
        idEt = findViewById(R.id.idEt);
        idEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        SetPhone();
        nameEt = findViewById(R.id.nameEt);
        pwEt = findViewById(R.id.pwEt);
        pwConfirmEt = findViewById(R.id.pwConfirmEt);
        signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInput();
            }
        });
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

    //입력 체크
    private void CheckInput() {
        if (idEt.getText().toString().length() == 0) {
            Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (nameEt.getText().toString().length() == 0) {
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (pwEt.getText().toString().length() == 0) {
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        } else if (pwConfirmEt.getText().toString().length() == 0) {
            Toast.makeText(this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
        } else if (!pwEt.getText().toString().equals(pwConfirmEt.getText().toString())) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        } else {
            SignUp();
        }
    }

    //서버에 데이터 전달
    private void SignUp() {
        String Phone = idEt.getText().toString();
        String name = nameEt.getText().toString();
        String pw = pwEt.getText().toString();
        ReqSignUp signUp = new ReqSignUp(Phone, name, pw);
        RestAPI restAPI = new RestAPI(this);
        Call<Res> call = restAPI.getRetrofitService().SignUp(signUp);
        call.enqueue(new Callback<Res>() {
            @Override
            public void onResponse(Call<Res> call, Response<Res> response) {
                if(response.isSuccessful()) {
                    //회원가입 성공
                    if(response.body().isResult()) {
                        Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Res> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "서버 연결에 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
