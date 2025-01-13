package com.yunsong.bujen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.yunsong.bujen.R;

public class PageFragment extends Fragment {
    private String pageTitle;

    public PageFragment(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);
        TextView textView = rootView.findViewById(R.id.pageTitle);
        textView.setText(pageTitle); // 显示页面标题
        return rootView;
    }
}
