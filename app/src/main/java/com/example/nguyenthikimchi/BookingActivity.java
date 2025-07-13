package com.example.nguyenthikimchi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.nguyenthikimchi.models.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtDate, edtTime, edtPeople;
    Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // ✅ Thiết lập Toolbar có nút quay lại
        Toolbar toolbar = findViewById(R.id.toolbarBooking);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Đặt bàn");
        }

        // Ánh xạ view
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        edtPeople = findViewById(R.id.edtPeople);
        btnBook = findViewById(R.id.btnBook);

        // Xử lý chọn ngày và giờ
        edtDate.setOnClickListener(v -> showDatePicker());
        edtTime.setOnClickListener(v -> showTimePicker());

        // Xử lý nút đặt bàn
        btnBook.setOnClickListener(v -> {
            String name = edtFullName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String date = edtDate.getText().toString().trim();
            String time = edtTime.getText().toString().trim();
            String people = edtPeople.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || time.isEmpty() || people.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng Booking
            Booking booking = new Booking(name, phone, date, time, people);

            // Gửi lên Firebase
            DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
            String bookingId = bookingsRef.push().getKey();
            if (bookingId != null) {
                booking.setId(bookingId);
                bookingsRef.child(bookingId).setValue(booking)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Không thể tạo ID đặt bàn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị DatePicker
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            edtDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Hiển thị TimePicker
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            edtTime.setText(String.format("%02d:%02d", hour, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    // Xử lý khi bấm nút quay lại (trên toolbar)
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
