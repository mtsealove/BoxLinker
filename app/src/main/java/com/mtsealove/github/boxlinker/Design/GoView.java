package com.mtsealove.github.boxlinker.Design;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mtsealove.github.boxlinker.R;

public class GoView extends RelativeLayout {
    Context context;
    TextView titleTv, callbackTv;
    RelativeLayout rootLayout;

    public GoView(Context context) {
        super(context);
        init(context);
    }

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_go, null, false);
        titleTv = view.findViewById(R.id.titleTv);
        callbackTv = view.findViewById(R.id.callbackTv);
        rootLayout = view.findViewById(R.id.rootLayout);
        addView(view);
    }

    public void SetTitle(String title) {
        titleTv.setText(title);
    }

    public void SetCallback(String callback) {
        if (callback.length() > 0) {
            callbackTv.setVisibility(VISIBLE);
            callbackTv.setText(callback);
        } else {
            callbackTv.setVisibility(GONE);
        }
    }

    public RelativeLayout getRootLayout() {
        return rootLayout;
    }
}
