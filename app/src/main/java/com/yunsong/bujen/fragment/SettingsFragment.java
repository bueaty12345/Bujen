package com.yunsong.bujen.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.ZenbeatSetting;
import com.yunsong.bujen.device.Devices;
import com.yunsong.bujen.init.Login;
import com.yunsong.bujen.R;
import com.yunsong.bujen.init.Register;
import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{
    LinearLayout lay_logout,lay_quit,lay_sblb;
    private ConfirmDialog dialog;

    LinearLayout lin_sound,lin_device,lin_collect;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false);
        lay_logout=view.findViewById(R.id.lay_logout);
        lay_quit=view.findViewById(R.id.lay_quit);
        lay_sblb=view.findViewById(R.id.lay_sblb);
        lay_quit.setOnClickListener(this);
        lay_logout.setOnClickListener(this);
        lay_sblb.setOnClickListener(this);


        lin_sound=view.findViewById(R.id.lin_sound);
        lin_device=view.findViewById(R.id.lin_device);
        lin_collect=view.findViewById(R.id.lin_collect);
        lin_sound.setOnClickListener(this);
        lin_device.setOnClickListener(this);
        lin_collect.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.lin_sound:
                startActivity(new Intent(getActivity(), ZenbeatSetting.class));
                break;
           case R.id.lin_device:
                startActivity(new Intent(getActivity(), Devices.class));
                break;
           case R.id.lin_collect:
//                startActivity(new Intent(getActivity(), Resource.class));
                break;
            case R.id.lay_logout:
                showDialog("注销","一周后才会真正注销，注销前登录则会取消注销，确定注销吗？");
//                startActivity(new Intent(getActivity(), Connect.class));
                break;
            case R.id.lay_quit:
                showDialog("退出","你确定要退出登录吗？");
                break;
                case R.id.lay_sblb:
                    startActivity(new Intent(getActivity(), Devices.class));
                break;
        }
    }
    private void showDialog(String title,String message) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(getContext());
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .setTitle(message)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 处理确定按钮点击
                        switch (title){
                            case "注销":
                                toLogout();
                                break;
                            case "退出":
                                toQuit();
                                break;
                        }
                    }
                })
                .build();
        dialog.show();
    }

    private void toQuit() {
        ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                //退出登录成功
                startActivity(new Intent(getActivity(), Login.class));
                // 销毁当前 Activity
                getActivity().finish();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(getContext(), "退出失败"+errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void toLogout() {
        ThingHomeSdk.getUserInstance().cancelAccount(new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(getContext(), "注销失败"+error, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "注销成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), Register.class));
                // 销毁当前 Activity
                getActivity().finish();
            }
        });

    }
}