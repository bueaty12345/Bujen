package com.yunsong.bujen.pary;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ParyWrite extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_main, img_zp, img_xj;
    private LinearLayout lin_write;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int OPEN_GALLERY_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int REQUEST_TAKE_PHOTO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pary_write);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img_main = findViewById(R.id.img_main);
        img_zp = findViewById(R.id.img_zp);
        img_xj = findViewById(R.id.img_xj);
        lin_write = findViewById(R.id.lin_write);

        img_main.setOnClickListener(this);
        img_zp.setOnClickListener(this);
        img_xj.setOnClickListener(this);
    }

    // 申请存储权限
    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    // 用户选择是否开启权限操作后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "你拒绝使用存储权限！", Toast.LENGTH_SHORT).show();
                Log.d("HL", "你拒绝使用存储权限！");
            }
        }
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 打开相册
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
    }

    // 请求相机权限
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            takePhoto();
        }
    }

    // 启动相机并拍照
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY_REQUEST_CODE && data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    updateImageView(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    updateImageView(imageBitmap);
                }
            }
        }
    }

    // 更新 ImageView 或添加新的 ImageView
    private void updateImageView(Bitmap bitmap) {
        // 获取最后一个子视图
        View lastChild = lin_write.getChildAt(lin_write.getChildCount() - 1);
        // 判断最后一个子视图是否为 ImageView 类型
        if (lastChild instanceof ImageView) {
            ((ImageView) lastChild).setImageBitmap(bitmap);
        } else {
            ImageView mImg = new ImageView(this);
            // 获取屏幕宽度
            int screenWidth = getResources().getDisplayMetrics().widthPixels;  // 单位为像素

            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();

// 根据屏幕宽度计算高度
            int height = (int) (screenWidth / aspectRatio);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
            mImg.setLayoutParams(params);
            mImg.setImageBitmap(bitmap);
            lin_write.addView(mImg);
            mImg.setOnLongClickListener(view -> {
                lin_write.removeView(mImg);
                return true;
            });
        }
    }

    // 动画效果
    private void animZP() {
        if (img_xj.getVisibility() == View.VISIBLE) {
            img_zp.setVisibility(View.INVISIBLE);
            img_xj.setVisibility(View.INVISIBLE);
            return;
        }

        img_zp.setVisibility(View.VISIBLE);
        img_xj.setVisibility(View.VISIBLE);

        // 动画效果
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(img_zp, "translationY", 0f, -250f);
        animator1.setDuration(700);

        ObjectAnimator animator2X = ObjectAnimator.ofFloat(img_xj, "translationX", 0f, 200f);
        ObjectAnimator animator2Y = ObjectAnimator.ofFloat(img_xj, "translationY", 0f, -200f);
        animator2X.setDuration(700);
        animator2Y.setDuration(700);

        animator1.start();
        animator2X.start();
        animator2Y.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_main:
                animZP();
                break;
            case R.id.img_zp:
                applyPermission();
                break;
            case R.id.img_xj:
                requestCameraPermission();
                break;
        }
    }
}
