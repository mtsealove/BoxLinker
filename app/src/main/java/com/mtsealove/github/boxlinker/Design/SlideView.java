package com.mtsealove.github.boxlinker.Design;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mtsealove.github.boxlinker.LoginActivity;
import com.mtsealove.github.boxlinker.R;

public class SlideView extends LinearLayout {
    Context context;
    LinearLayout loginLayout;
    TextView loginTv, inquireTv, nameTv, contentTv;

    public SlideView(Context context) {
        super(context);
        init(context);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_slide, this, false);
        loginLayout = view.findViewById(R.id.loginLayout);
        nameTv = view.findViewById(R.id.nameTv);
        contentTv = view.findViewById(R.id.contentTv);
        loginTv = view.findViewById(R.id.loginTv);
        loginTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        inquireTv = view.findViewById(R.id.inquireTv);
        addView(view);
    }

    private void Login() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void CheckLogin() {
        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        String phone = pref.getString("phone", "");
        String name = pref.getString("name", "");
        if(name.length()!=0) {

        }
    }
}
