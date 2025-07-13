package com.example.nguyenthikimchi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nguyenthikimchi.R;
import com.example.nguyenthikimchi.models.CartItem;
import com.example.nguyenthikimchi.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.txtOrderId.setText("Mã đơn: " + order.getOrderId());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.txtOrderDate.setText("Ngày đặt: " + sdf.format(new Date(order.getTimestamp())));

        holder.txtTotalAmount.setText(String.format("Tổng tiền: ₫%.0f", order.getTotalAmount()));

        StringBuilder itemSummary = new StringBuilder("Sản phẩm: ");
        for (CartItem item : order.getItems()) {
            itemSummary.append(item.getName())
                    .append(" x").append(item.getQuantity())
                    .append(", ");
        }

        // Xoá dấu phẩy cuối
        if (itemSummary.length() > 0) {
            itemSummary.setLength(itemSummary.length() - 2);
        }

        holder.txtItems.setText(itemSummary.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtItems, txtTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtItems = itemView.findViewById(R.id.txtItems);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
        }
    }
}
