package com.example.dailyselfi;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private ArrayList<PhotoModel> photoList;
    private static final int CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo danh sách ảnh
        photoList = PhotoUtils.loadPhotos(this);
        
        Button btnFilter = findViewById(R.id.btnFilter);
        Button btnDelete = findViewById(R.id.btnDelete);
        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoAdapter = new PhotoAdapter(photoList, this, this::onPhotoClick, this::onDeletePhotos);
        recyclerView.setAdapter(photoAdapter);
        btnFilter.setOnClickListener(v -> {
            // Hiển thị DatePickerDialog
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Tạo DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                // Chuyển ngày đã chọn thành chuỗi theo định dạng yyyy-mm-dd
                String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                filterPhotosByDate(selectedDate);
            }, year, month, day); // Hiển thị ngày hiện tại làm mặc định

            // Hiển thị DatePickerDialog
            datePickerDialog.show();
        });

        btnDelete.setOnClickListener(v -> onDeletePhotos(photoAdapter.getAllSelected()));
        // Button: Chụp ảnh mới
        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền camera
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            // Quyền đã được cấp, mở camera
            PhotoUtils.takePhoto(this);
        }
    }
    private void filterPhotosByDate(String date) {
        ArrayList<PhotoModel> filtered = new ArrayList<>();
        for (PhotoModel photo : PhotoUtils.loadPhotos(this)) {
            if (photo.getDateTime().contains(date)) {
                filtered.add(photo);
            }
        }
        photoList.clear();
        photoList.addAll(filtered);
        photoAdapter.notifyDataSetChanged();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, mở camera
                PhotoUtils.takePhoto(this);
            } else {
                // Người dùng từ chối cấp quyền
                Toast.makeText(this, "Bạn cần cấp quyền Camera để chụp ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoUtils.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoList.clear();
            photoList.addAll(PhotoUtils.loadPhotos(this));
            photoAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Ảnh đã được lưu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void onPhotoClick(PhotoModel photo) {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra("photo", photo);
        startActivity(intent);
    }

    private void onDeletePhotos(ArrayList<PhotoModel> selectedPhotos) {
        if(selectedPhotos.isEmpty()){
            Toast.makeText(this,"Chưa chọn ảnh để xóa", Toast.LENGTH_SHORT).show();
            return ;

        }
        PhotoUtils.deletePhotos(this, selectedPhotos);
        photoList.clear();
        photoList.addAll(PhotoUtils.loadPhotos(this));
        photoAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã xóa ảnh đã chọn!", Toast.LENGTH_SHORT).show();
    }
}
