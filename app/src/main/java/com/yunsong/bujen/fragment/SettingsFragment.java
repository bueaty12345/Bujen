package com.yunsong.bujen.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.Local;
import com.yunsong.bujen.MyCollect;
import com.yunsong.bujen.MyPray;
import com.yunsong.bujen.MyTutorial;
import com.yunsong.bujen.setting.Setting;
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

    private   TextView txt_setting_gdd;
    private SettingsViewModel sharedViewModel;

    LinearLayout lin_sound,lin_device,lin_collect,lay_music,lay_Tutorial,lay_pray,service,mySetting;

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
        txt_setting_gdd=view.findViewById(R.id.textView47);
        lin_sound=view.findViewById(R.id.lin_sound);
        lin_device=view.findViewById(R.id.lin_device);
        lin_collect=view.findViewById(R.id.lin_collect);
        lay_music=view.findViewById(R.id.lay_music);
        lay_Tutorial=view.findViewById(R.id.lay_Tutorial);
        lay_pray=view.findViewById(R.id.lay_pray);
        service=view.findViewById(R.id.lay_service);
        mySetting=view.findViewById(R.id.mySetting);
        lin_sound.setOnClickListener(this);
        lin_device.setOnClickListener(this);
        lin_collect.setOnClickListener(this);
        lay_music.setOnClickListener(this);
        lay_Tutorial.setOnClickListener(this);
        lay_pray.setOnClickListener(this);
        service.setOnClickListener(this);
        mySetting.setOnClickListener(this);

        // 初始化 ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        sharedViewModel.getGddCont().observe(getViewLifecycleOwner(), value -> {
            txt_setting_gdd.setText(String.valueOf(value));
        });

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
                startActivity(new Intent(getActivity(), MyCollect.class));
                break;
            case R.id.lay_music:
                startActivity(new Intent(getActivity(), Local.class));
                break;
            case R.id.lay_Tutorial:
                startActivity(new Intent(getActivity(), MyTutorial.class));
                break;
            case R.id.lay_pray:
                startActivity(new Intent(getActivity(), MyPray.class));
                break;
            case R.id.lay_service:
//                startActivity(new Intent(getActivity(),service.class));
            case R.id.mySetting:
                startActivity(new Intent(getActivity(), Setting.class));
        }
    }

}