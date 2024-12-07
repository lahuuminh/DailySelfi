package com.example.dailyselfi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PhotoUtils {
    public static final int REQUEST_TAKE_PHOTO = 1;
    private static String currentPhotoPath;

    public static void takePhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(activity);
            } catch (IOException ex) {
                Toast.makeText(activity, "Lỗi tạo file ảnh!", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        activity,
                        activity.getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        } else {
            Toast.makeText(activity, "Không tìm thấy ứng dụng camera!", Toast.LENGTH_SHORT).show();
        }
    }

    private static File createImageFile(Context context) throws IOException {
        // Tạo tên file ảnh duy nhất
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Tạo file ảnh
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Lưu đường dẫn để dùng sau
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static String getCurrentPhotoPath() {
        return currentPhotoPath;
    }


    public static ArrayList<PhotoModel> loadPhotos(Context context) {
        ArrayList<PhotoModel> photos = new ArrayList<>();
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null && storageDir.isDirectory()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".jpg")) {
                        String filePath = file.getAbsolutePath();
                        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(new Date(file.lastModified()));
                        photos.add(new PhotoModel(filePath, dateTime));
                    }
                }
            }
        }
        return photos;
    }

    public static void deletePhotos(Context context, ArrayList<PhotoModel> selectedPhotos) {
        for (PhotoModel photo : selectedPhotos) {
            File file = new File(photo.getPath());
            if (file.exists()) {
                if (!file.delete()) {
                    Toast.makeText(context, "Không thể xóa ảnh: " + photo.getPath(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        Toast.makeText(context, "Ảnh đã được xóa!", Toast.LENGTH_SHORT).show();
    }

  
}
