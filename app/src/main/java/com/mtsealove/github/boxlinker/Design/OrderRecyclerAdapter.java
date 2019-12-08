package com.mtsealove.github.boxlinker.Design;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mtsealove.github.boxlinker.OrderInquireActivity;
import com.mtsealove.github.boxlinker.R;
import com.mtsealove.github.boxlinker.Restful.ResOrderSm;

import java.util.ArrayList;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ItemViewHolder> {
    Context context;

    public OrderRecyclerAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<ResOrderSm> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_order, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(ResOrderSm data) {
        listData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTv, statusTv;
        LinearLayout clickLayout;

        ItemViewHolder(View itemView) {
            super(itemView);
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            clickLayout = itemView.findViewById(R.id.clickLayout);
        }

        void onBind(final ResOrderSm data) {
            orderIdTv.setText("주문번호: " + data.getOrderID());
            statusTv.setText(data.getStatus());

            clickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderInquireActivity.class);
                    intent.putExtra("orderID", data.getOrderID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
