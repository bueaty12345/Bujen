package com.yunsong.bujen.setting;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.AvatarUpdateEvent;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.UserBean;
import com.yunsong.bujen.fragment.SettingsViewModel;
import com.yunsong.bujen.ui.GenderBottomDialog;
import com.yunsong.bujen.ui.NicknameDialog;
import com.yunsong.bujen.ui.PhotoOptionDialog;
import com.yunsong.bujen.ui.SignatureDialog;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileEditor extends AppCompatActivity implements View.OnClickListener{
    TextView tv_signature_info,et_nickname,tv_gender_info;
    ImageView img_back,img_icon_headPortrait;

    LinearLayout lin_headPortrait,lin_gender;

    private final String USER_INFO_URL= BuildConfig.API_SERVER+"/system/users";
    private final String FILE_URL=BuildConfig.API_SERVER+"/common/upload";
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private static final int REQUEST_PERMISSION = 2000;

    private UserBean userInfo = new UserBean();

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
        loadAvatar();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        et_nickname=findViewById(R.id.et_nickname);
        tv_signature_info=findViewById(R.id.tv_signature_info);
        lin_headPortrait=findViewById(R.id.lin_headPortrait);
        img_icon_headPortrait=findViewById(R.id.img_icon_headPortrait);
        tv_gender_info=findViewById(R.id.tv_gender_info);

        img_back.setOnClickListener(this);

        et_nickname.setText(UserInfoUtils.getUserNickname(this));
        tv_signature_info.setText(UserInfoUtils.getUserSignature(this));
        tv_gender_info.setText(UserInfoUtils.getUserGender(this));
        lin_headPortrait.setOnClickListener(this);
        img_icon_headPortrait.setOnClickListener(this);
        tv_gender_info.setOnClickListener(this);
        et_nickname.setOnClickListener(this);
        tv_signature_info.setOnClickListener(this);

        userInfo.id = UserInfoUtils.getUserId(this);
        userInfo.nickname = UserInfoUtils.getUserNickname(this);
        userInfo.signature = UserInfoUtils.getUserSignature(this);
        userInfo.avatar = "";
        userInfo.gender = tv_gender_info.getText().toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_icon_headPortrait:
                showPhotoOptions();
                break;
            case R.id.tv_gender_info:
                showGenderDialog();
                break;
            case R.id.et_nickname:
                showNickNameDialog();
                break;
            case R.id.tv_signature_info:
                showSignatureDialog();
                break;
        }
    }

    private void showNickNameDialog(){
        NicknameDialog dialog = new NicknameDialog(this);
        dialog.setOnConfirmListener(nickname -> {
            et_nickname.setText(nickname);
            userInfo.nickname = nickname;
            UserInfoUtils.saveUserNickname(this, nickname);
            UserInfoUtils.saveNicknameModifyTime(this);
            updateUserInfoToServer();
        });
        dialog.show();
    }

    private void showSignatureDialog(){
        SignatureDialog dialog = new SignatureDialog(this);
        dialog.setOnConfirmListener(signature -> {
            tv_signature_info.setText(signature);
            userInfo.signature = signature;
//            UserInfoUtils.saveUserSignature(this, signature);
            updateUserInfoToServer();
        });
        dialog.show();
    }


    private void showGenderDialog() {
        String currentGender = tv_gender_info.getText().toString();
        GenderBottomDialog dialog = new GenderBottomDialog(this, currentGender);
        dialog.setOnGenderSelectedListener(gender -> {
            tv_gender_info.setText(gender);
            userInfo.gender = gender;
            updateUserInfoToServer();
        });
        dialog.show();
    }


    private void loadAvatar() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String base64 = sp.getString("avatar", null);
        if (base64 != null) {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(img_icon_headPortrait);
        }
    }

    private void showPhotoOptions() {
        PhotoOptionDialog dialog = new PhotoOptionDialog(this);
        dialog.setOnOptionSelectedListener(new PhotoOptionDialog.OnOptionSelectedListener() {
            @Override
            public void onTakePhoto() {
                openCamera();
            }

            @Override
            public void onChoosePhoto() {
                openGallery();
            }
        });
        dialog.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
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

    // 图片返回处理并上传
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;

            if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == REQUEST_GALLERY) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                Glide.with(this)
                        .load(bitmap)
                        .circleCrop()
                        .into(img_icon_headPortrait);
                uploadAvatar(bitmap);
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private void uploadAvatar(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        RequestBody fileBody = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "avatar.jpg", fileBody)
                .build();

        String token=UserInfoUtils.getToken(this);

        Request request = new Request.Builder()
                .url(FILE_URL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AvatarUpload", "上传失败：" + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(ProfileEditor.this, "头像上传失败", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("AvatarUpload", "头像上传结果：" + result);

                try {
                    JSONObject json = new JSONObject(result);
                    if (json.optInt("code") == 200) {
                        String url = json.optString("url");
                        userInfo.avatar = url;

                        runOnUiThread(() -> Glide.with(ProfileEditor.this)
                                .load(url)
                                .circleCrop()
                                .into(img_icon_headPortrait));

                        updateUserInfoToServer();
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(ProfileEditor.this, "上传失败：" + json.optString("msg"), Toast.LENGTH_SHORT).show()
                        );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateUserInfoToServer() {
        JSONObject json;
        try {
            json = userInfo.toJson();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String token=UserInfoUtils.getToken(this);
        RequestBody body = RequestBody.create(
                json.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(USER_INFO_URL)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ProfileEditor.this, "信息更新失败", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    UserInfoUtils.saveUserNickname(ProfileEditor.this, userInfo.nickname);
                    UserInfoUtils.saveUserSignature(ProfileEditor.this, userInfo.signature);
                    UserInfoUtils.saveUserGender(ProfileEditor.this, userInfo.gender);
                    UserInfoUtils.saveUserAvatarUrl(ProfileEditor.this, userInfo.avatar);

                    // 如果头像是网络地址，也可以保存
                    if (!TextUtils.isEmpty(userInfo.avatar)) {
                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                        sp.edit().putString("avatarUrl", userInfo.avatar).apply();
                    }
                    runOnUiThread(() ->
                            Toast.makeText(ProfileEditor.this, "信息更新成功", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(ProfileEditor.this, "服务器异常：" + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


}
