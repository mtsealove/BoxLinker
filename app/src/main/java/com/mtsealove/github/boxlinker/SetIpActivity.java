package com.mtsealove.github.boxlinker;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SetIpActivity extends AppCompatActivity {
    EditText ipEt;
    Button ipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        ipBtn = findViewById(R.id.ipBtn);
        ipEt = findViewById(R.id.ipEt);
        ipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIp();
            }
        });
    }

    private void setIp() {
        String ip = "http://" + ipEt.getText().toString() + ":3700";
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ip", ip);
        editor.commit();
    }
}
