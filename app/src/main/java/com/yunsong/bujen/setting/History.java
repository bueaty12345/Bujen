package com.yunsong.bujen.setting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.R;
import com.yunsong.bujen.adapter.CalendarAdapter;
import com.yunsong.bujen.databean.BlessingRecordBean;
import com.yunsong.bujen.databean.CalendarDay;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class History extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout pray_history, weekHeaderLayout;
    private TextView tvYearMonth,tvUnAchievedCount;
    private ImageView img_back, btnPrevMonth, btnNextMonth, btnSwitchYear;
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter adapter;

    private int year, month;
    private final Set<LocalDate> prayDateSet = new HashSet<>();
    private ArrayList<BlessingRecordBean> fullBlessingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        setupCalendar();
        int userId = UserInfoUtils.getUserId(this);
        fetchPrayHistoryFromServer(userId);

    }
    private void init(){
        img_back = findViewById(R.id.img_back);
        pray_history = findViewById(R.id.pray_history);
        tvYearMonth = findViewById(R.id.tv_year_month);
        btnPrevMonth = findViewById(R.id.btn_prev_month);
        btnNextMonth = findViewById(R.id.btn_next_month);
        btnSwitchYear = findViewById(R.id.btn_switch_year);
        weekHeaderLayout = findViewById(R.id.weekHeaderLayout);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        tvUnAchievedCount=findViewById(R.id.tv_title3);

        img_back.setOnClickListener(this);
        pray_history.setOnClickListener(this);
        btnPrevMonth.setOnClickListener(v -> changeMonth(-1));
        btnNextMonth.setOnClickListener(v -> changeMonth(1));
        btnSwitchYear.setOnClickListener(v -> showBottomYearMonthPickerDialog());

        updateRegisterTimeText();
    }

    private void updateRegisterTimeText() {
        TextView tvRegisterTime = findViewById(R.id.registerTime);
        String registerTimeStr = UserInfoUtils.getUserRegisterTime(this);

        if (!TextUtils.isEmpty(registerTimeStr)) {
            try {
                LocalDate registerDate;
                if (registerTimeStr.contains("T")) {
                    registerDate = LocalDate.parse(registerTimeStr.split("T")[0]);
                } else {
                    registerDate = LocalDate.parse(registerTimeStr);
                }

                LocalDate today = LocalDate.now();
                long daysBetween = ChronoUnit.DAYS.between(registerDate, today) + 1;

                String text = "今天是使用不卷的第 " + daysBetween + " 天";
                tvRegisterTime.setText(text);

            } catch (Exception e) {
                tvRegisterTime.setText("使用时间计算失败");
                e.printStackTrace();
            }
        } else {
            tvRegisterTime.setText("未获取到注册时间");
        }
    }


    private void setupCalendar() {
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        adapter = new CalendarAdapter();
        calendarRecyclerView.setAdapter(adapter);

        year = LocalDate.now().getYear();
        month = LocalDate.now().getMonthValue();

        setupWeekHeader();
        updateCalendarView();
    }

    private void setupWeekHeader() {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        weekHeaderLayout.removeAllViews();

        for (String day : weekDays) {
            TextView tv = new TextView(this);
            tv.setText(day);
            tv.setTextSize(12);
            tv.setTextColor(Color.parseColor("#7C7C7C"));
            tv.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(lp);
            weekHeaderLayout.addView(tv);
        }
    }
    private void updateCalendarView() {
        tvYearMonth.setText(year + "年" + month + "月");
        List<CalendarDay> days = generateCalendar(year, month, prayDateSet);
        adapter.setData(days);
    }

    private void changeMonth(int delta) {
        month += delta;
        if (month > 12) {
            month = 1;
            year++;
        } else if (month < 1) {
            month = 12;
            year--;
        }
        updateCalendarView();
    }

    private List<CalendarDay> generateCalendar(int year, int month, Set<LocalDate> prayDateSet) {
        List<CalendarDay> days = new ArrayList<>();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue();
        if (startDayOfWeek == 7) startDayOfWeek = 0;

        int daysInMonth = firstDay.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < startDayOfWeek; i++) {
            days.add(new CalendarDay(0, false, false, null));
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            boolean isToday = date.equals(today);
            boolean hasPray = prayDateSet.contains(date);
            days.add(new CalendarDay(day, isToday, hasPray, date));
        }

        return days;
    }



    private void fetchPrayHistoryFromServer(int userId) {
        final String url = BuildConfig.API_SERVER+"/system/recordb/"+userId;
        String token = UserInfoUtils.getToken(this);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(History.this, "加载历史失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resStr = response.body().string();
                        JSONObject json = new JSONObject(resStr);
                        JSONArray array = json.getJSONArray("data");

                        prayDateSet.clear();
                        fullBlessingList.clear();

                        AtomicInteger unAchievedCount = new AtomicInteger(0);

                        LocalDate today = LocalDate.now();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            BlessingRecordBean bean = new BlessingRecordBean();
                            bean.recordId = obj.optInt("recordId");
                            bean.userId = obj.optInt("userId");
                            bean.blessingTime = obj.optString("blessingTime");
                            bean.blessingId = obj.optInt("blessingId");
                            bean.blessingTitle = obj.optString("blessingTitle");
                            bean.blessingMethod = obj.optString("blessingMethod");
                            bean.blessingContent = obj.optString("blessingContent");
                            bean.blessingAudioUrl = obj.optString("blessingAudioUrl");
                            bean.blessingImageUrl = obj.optString("blessingImageUrl");
                            bean.wishTime = obj.optString("wishTime");
                            bean.achieveTime = obj.optString("achieveTime");

                            fullBlessingList.add(bean);

                            String dateStr = bean.achieveTime;
                            if (!TextUtils.isEmpty(dateStr)) {
                                try {
                                    LocalDate date = LocalDate.parse(dateStr);
                                    prayDateSet.add(date);

                                    if (date.isAfter(today)) {
                                        unAchievedCount.incrementAndGet();
                                    }
                                } catch (Exception ex) {
                                    Log.e("fetchPrayHistory", "日期解析失败：" + dateStr);
                                }
                            }else{
                                unAchievedCount.incrementAndGet();
                            }
                        }

                        runOnUiThread(() -> {
                            updateCalendarView();

                            String text = "您有 " + unAchievedCount.get() + " 个许愿签未实现";
                            tvUnAchievedCount.setText(text);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(History.this, "服务器错误", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showBottomYearMonthPickerDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_year_month, null);
        NumberPicker yearPicker = view.findViewById(R.id.yearPicker);
        NumberPicker monthPicker = view.findViewById(R.id.monthPicker);
        Button btnConfirm = view.findViewById(R.id.btn_confirm);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);

//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false); // 禁止返回键关闭

        int currentYear = LocalDate.now().getYear();

        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(year);
        yearPicker.setWrapSelectorWheel(false);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month);
        monthPicker.setWrapSelectorWheel(false);

        setNumberPickerDividerColor(yearPicker, Color.TRANSPARENT);
        setNumberPickerDividerColor(monthPicker, Color.TRANSPARENT);

        btnConfirm.setOnClickListener(v -> {
            year = yearPicker.getValue();
            month = monthPicker.getValue();

            updateCalendarView();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker, int color) {
        try {
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    pf.set(numberPicker, new ColorDrawable(color));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.pray_history:
                Intent intent = new Intent(History.this, PrayHistory.class);
                intent.putExtra("blessingList", fullBlessingList);
                startActivity(intent);
                break;
        }
    }
}
