package com.yunsong.bujen.fragment;

import static com.yunsong.bujen.Homepage.rl_bg;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.MeditationFragment;
import com.yunsong.bujen.R;

public class HFragment extends Fragment {

    public HFragment() {
        super(R.layout.fragment_h);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // 使用 FragmentStateAdapter 来代替 PagerAdapter
        viewPager.setAdapter(new MyPagerAdapter(getActivity()));

        // 设置页面切换动画 (如果需要)
        viewPager.setPageTransformer(new CubeOutTransformer());

        // 添加页面切换监听器
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 根据当前页面切换背景
                switch (position % 3) { // 通过取模操作使得页面切换时循环
                    case 0:
//                        view.setBackgroundColor(Color.RED); // 设置背景色为红色
//                        rl_bg.setBackgroundResource(R.drawable.dg_orange);
                        rl_bg.setBackgroundResource(Homepage.homebg);
                        break;
                    case 1:
//                        view.setBackgroundColor(Color.WHITE); // 设置背景色为绿色
//                        rl_bg.setBackgroundResource(R.drawable.dg_green);
                        rl_bg.setBackgroundColor(Color.parseColor("#796b5e"));
                        break;
                    case 2:
//                        view.setBackgroundColor(Color.BLUE); // 设置背景色为蓝色
//                        rl_bg.setBackgroundResource(R.drawable.dg_blue);
                        rl_bg.setBackgroundColor(Color.parseColor("#CCBDAA"));
                        break;
                    default:
//                        view.setBackgroundColor(Color.WHITE); // 设置默认背景
                        break;
                }
            }
        });
    }

    // 使用 FragmentStateAdapter 来管理 Fragment 页面
    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // 根据 position 返回不同的 Fragment，使用取模操作来循环
            switch (position % 3) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new MeditationFragment();
                case 2:
                    return new PrayFragment();
                default:
                    return new PageFragment("Page " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 10000; // 这里返回一个非常大的数字来模拟循环
        }
    }
}
