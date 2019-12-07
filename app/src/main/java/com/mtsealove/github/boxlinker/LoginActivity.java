package com.mtsealove.github.boxlinker;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.mtsealove.github.boxlinker.Design.SystemUiTuner;

public class LoginActivity extends AppCompatActivity {
    EditText idEt, pwEt;
    Button loginBtn;
    TextView signUpTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEt = findViewById(R.id.idEt);
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
    }

    private void SignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
