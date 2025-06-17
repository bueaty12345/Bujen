package com.yunsong.bujen.setting;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.utils.UserInfoUtils;

public class ProfileEditor extends AppCompatActivity implements View.OnClickListener{
    TextView tv_nickname_info,tv_signature_info;
    ImageView img_back;

    LinearLayout lin_headPortrait;

    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private static final int REQUEST_PERMISSION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profileeditor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        tv_nickname_info=findViewById(R.id.tv_nickname_info);
        tv_signature_info=findViewById(R.id.tv_signature_info);
        lin_headPortrait=findViewById(R.id.lin_headPortrait);

        img_back.setOnClickListener(this);

        tv_nickname_info.setText(UserInfoUtils.getUserNickname(this));
        tv_signature_info.setText(UserInfoUtils.getUserSignature(this));
        lin_headPortrait.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_headPortrait:
                showPhotoOptions();
                break;
        }
    }

    private void showPhotoOptions() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_photo_options, null);
        dialog.setContentView(view);

        TextView takePhoto = view.findViewById(R.id.tv_take_photo);
        TextView choosePhoto = view.findViewById(R.id.tv_choose_photo);
        TextView cancel = view.findViewById(R.id.tv_cancel);

        takePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            openCamera();
        });

        choosePhoto.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, "需要相机权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // 设置头像
                ImageView imgHead = findViewById(R.id.img_icon_headPortrait);
                imgHead.setImageBitmap(photo);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri uri = data.getData();
                ImageView imgHead = findViewById(R.id.img_icon_headPortrait);
                imgHead.setImageURI(uri);
            }
        }
    }


}
