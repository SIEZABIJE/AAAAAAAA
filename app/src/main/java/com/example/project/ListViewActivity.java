package com.example.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ListViewActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView orderListView;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        dbHelper = new DatabaseHelper(this);
        orderListView = findViewById(R.id.orderListView);
        loadOrders();
    }

    private void loadOrders() {
        Cursor cursor = dbHelper.getAllOrders();
        adapter = new OrderAdapter(this, cursor);
        orderListView.setAdapter(adapter);
    }

    private static class OrderAdapter extends CursorAdapter {
        public OrderAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView orderIdText = view.findViewById(R.id.orderIdText);
            TextView orderDateText = view.findViewById(R.id.orderDateText);
            TextView orderPriceText = view.findViewById(R.id.orderPriceText);

            int orderId = cursor.getInt(cursor.getColumnIndexOrThrow("order_id"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
            float price = cursor.getFloat(cursor.getColumnIndexOrThrow("order_price"));
            String userLogin = cursor.getString(cursor.getColumnIndexOrThrow("user_login"));
            String equipmentList = cursor.getString(cursor.getColumnIndexOrThrow("equipment_list"));

            // Format the equipment list with prices
            String[] equipment = equipmentList != null ? equipmentList.split(", ") : new String[0];
            StringBuilder formattedEquipment = new StringBuilder();
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            for (String item : equipment) {
                if (!item.isEmpty()) {
                    // Get price for each equipment item using dbHelper
                    Cursor equipCursor = dbHelper.getEquipmentByName(item);
                    if (equipCursor != null && equipCursor.moveToFirst()) {
                        @SuppressLint("Range") float itemPrice = equipCursor.getFloat(equipCursor.getColumnIndex("equipment_price"));
                        formattedEquipment.append(item)
                                .append(" - ")
                                .append(String.format("%.2f zł", itemPrice))
                                .append("\n");
                    }
                    equipCursor.close();
                }
            }

            orderIdText.setText(String.format("Zamówienie #%d od %s", orderId, userLogin));
            orderDateText.setText(date);
            orderPriceText.setText(String.format("Suma: %.2f zł\n%s", price, formattedEquipment.toString()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}