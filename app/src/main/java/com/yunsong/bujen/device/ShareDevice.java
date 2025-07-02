package com.yunsong.bujen.device;

import static com.yunsong.bujen.Homepage.homeId;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.SharedUserInfoBean;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;

import java.util.ArrayList;
import java.util.List;

public class ShareDevice extends AppCompatActivity {
    private String deviceId;
    private long homeId = 0L;

    private TextView btnAdd;
    private EditText etCount;
    private ImageView btnWeChat, btnSms, btnCopy, btnMore;
    private Button btnConfirmCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedevices);

        deviceId = getIntent().getStringExtra("id");
        homeId = getIntent().getLongExtra("homeId", 0);

        btnAdd = findViewById(R.id.btn_add);
        etCount = findViewById(R.id.et_count);
        btnWeChat = findViewById(R.id.btn_wechat);
        btnSms = findViewById(R.id.btn_sms);
        btnCopy = findViewById(R.id.btn_copy);
        btnMore = findViewById(R.id.btn_more);

        btnAdd.setOnClickListener(v -> showAccountInputDialog());
        btnWeChat.setOnClickListener(v -> shareInvite("微信"));
        btnSms.setOnClickListener(v -> shareInvite("短信"));
        btnCopy.setOnClickListener(v -> copyInviteToClipboard());
        btnMore.setOnClickListener(v -> shareInvite("更多"));

        showInviteCodeConfirmDialog(); // 初始化时弹出确认框（也可以绑定按钮）
    }

    private void showAccountInputDialog() {
        EditText input = new EditText(this);
        input.setHint("请输入对方账号（手机号或邮箱）");

        new AlertDialog.Builder(this)
                .setTitle("添加账号分享")
                .setView(input)
                .setPositiveButton("确认", (dialogInterface, i) -> {
                    String userAccount = input.getText().toString().trim();
                    if (!userAccount.isEmpty()) {
                        addShareWithAccount(userAccount);
                    } else {
                        Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void addShareWithAccount(String userAccount) {
        List<String> devIds = new ArrayList<>();
        devIds.add(deviceId);

        ThingHomeSdk.getDeviceShareInstance().addShareWithHomeId(
                homeId,
                "86",
                userAccount,
                devIds,
                new IThingResultCallback<SharedUserInfoBean>() {
                    @Override
                    public void onSuccess(SharedUserInfoBean result) {
                        Toast.makeText(ShareDevice.this, "共享成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(ShareDevice.this, "共享失败: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void shareInvite(String type) {
        String userAccount = "temp@share.com";

        ThingHomeSdk.getDeviceShareInstance().inviteShare(deviceId, userAccount, "86", new IThingResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer shareId) {
                String content = "我邀请你共享设备，邀请码是：" + shareId + "，请在 App 中输入确认绑定。";

                switch (type) {
                    case "微信":
                        shareViaWeChat(content);
                        break;
                    case "短信":
                        shareViaSms(content);
                        break;
                    case "更多":
                        shareViaText(content);
                        break;
                }
            }

            @Override
            public void onError(String code, String error) {
                Toast.makeText(ShareDevice.this, "生成邀请失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareViaWeChat(String content) {
        Intent wechatIntent = new Intent(Intent.ACTION_SEND);
        wechatIntent.setType("text/plain");
        wechatIntent.putExtra(Intent.EXTRA_TEXT, content);
        wechatIntent.setPackage("com.tencent.mm");
        try {
            startActivity(wechatIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "未安装微信", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareViaSms(String content) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", content);
        startActivity(intent);
    }

    private void shareViaText(String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        Intent chooser = Intent.createChooser(intent, "通过应用分享");
        startActivity(chooser);
    }

    private void copyInviteToClipboard() {
        ThingHomeSdk.getDeviceShareInstance().inviteShare(deviceId, "temp@share.com", "86",
                new IThingResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer shareId) {
                        String content = "我邀请你共享设备，邀请码是：" + shareId;
                        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("share", content));
                        Toast.makeText(ShareDevice.this, "已复制分享内容", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(ShareDevice.this, "生成邀请码失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmInvite(int shareId) {
        ThingHomeSdk.getDeviceShareInstance().confirmShareInviteShare(shareId, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(ShareDevice.this, "确认失败: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(ShareDevice.this, "已确认设备共享", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryMyReceivedShares() {
        ThingHomeSdk.getDeviceShareInstance().queryShareReceivedUserList(new IThingResultCallback<List<SharedUserInfoBean>>() {
            @Override
            public void onSuccess(List<SharedUserInfoBean> sharedUsers) {
                Toast.makeText(ShareDevice.this, "已收到 " + sharedUsers.size() + " 个共享", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String code, String error) {
                Toast.makeText(ShareDevice.this, "查询失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInviteCodeConfirmDialog() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("请输入邀请码");

        new AlertDialog.Builder(this)
                .setTitle("确认设备共享")
                .setView(input)
                .setPositiveButton("确认", (dialog, which) -> {
                    try {
                        int shareId = Integer.parseInt(input.getText().toString().trim());
                        confirmInvite(shareId);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "邀请码格式错误", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
