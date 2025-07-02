package com.yunsong.bujen.pary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.MyPray;
import com.yunsong.bujen.R;
import com.yunsong.bujen.adapter.MyPrayAdapter;
import com.yunsong.bujen.databean.MyPrayBean;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Owned extends AppCompatActivity {
    Button btn_write,btn_write2,btn_write3;
    ListView listView;
    TextView new_date;

    private final String PRAY_INFO_URL = BuildConfig.API_SERVER + "/system/blessing/app/myList";
    private List<MyPrayBean> prayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owned);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intiView();
    }

    private void intiView() {
        listView=findViewById(R.id.list_local);
        new_date=findViewById(R.id.new_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.CHINA);
        String currentDate = sdf.format(new Date());
        new_date.setText(currentDate);

        fetchAllPrays();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            MyPrayBean selected = prayList.get(position);
            Intent intent = new Intent(Owned.this, ParyWrite.class);

            switch (selected.blessingMethod) {
                case "Text":
                    intent.putExtra("type", "纯文");
                    break;
                case "ImageText":
                    intent.putExtra("type", "图文");
                    break;
                case "Audio":
                    intent.putExtra("type", "语音");
                    break;
                default:
                    Toast.makeText(this, "未知类型", Toast.LENGTH_SHORT).show();
                    return;
            }

            // 可选：传 blessingId 到目标页
            intent.putExtra("blessingId", selected.blessing_id);
            intent.putExtra("blessingMethod",selected.blessingMethod);
            startActivity(intent);
            Log.d("ListItemClick", "点击了：" + selected.blessing_theme);

        });

    }

    private void fetchAllPrays() {
        String token = UserInfoUtils.getToken(this);
        new FetchPraysTask(this).execute(token);
    }
    private static class FetchPraysTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Owned> activityRef;

        public FetchPraysTask(Owned activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            try {
                URL url = new URL(BuildConfig.API_SERVER + "/system/blessing/app/myList");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.connect();

                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Error: 请求失败，状态码：" + code;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Owned activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.startsWith("Error:")) {
                Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rows = jsonObject.getJSONArray("rows");
                activity.prayList.clear();

                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyPrayBean bean = new MyPrayBean();
                    bean.blessing_id = obj.optInt("blessingId");
                    bean.resource_type = obj.optString("resourceType");
                    bean.blessing_category = obj.optString("blessingCategory");
                    bean.blessing_background_url = obj.optString("blessingBackgroundUrl");
                    bean.blessing_theme = obj.optString("blessingTheme");
                    bean.zen_quote = obj.optString("zenQuote");
                    bean.created_at = obj.optString("createdAt");
                    bean.required_merit_points = obj.optInt("requiredMeritPoints");
                    bean.blessingMethod = obj.optString("blessingMethod");
                    bean.exchangeQuantity=obj.getInt("exchangeQuantity");
                    activity.prayList.add(bean);
                }

                MyPrayAdapter adapter = new MyPrayAdapter(activity, activity.prayList);
                activity.listView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(activity, "解析数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


}