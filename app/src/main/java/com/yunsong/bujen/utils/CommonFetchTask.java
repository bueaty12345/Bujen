package com.yunsong.bujen.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.yunsong.bujen.model.JsonParser;
import com.yunsong.bujen.model.OnDataFetched;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CommonFetchTask<T> extends AsyncTask<String, Void, String> {
    private final String url;
    private final Context context;
    private final JsonParser<T> parser;
    private final OnDataFetched<T> callback;
    private final String tag;

    public CommonFetchTask(Context context, String url, JsonParser<T> parser, OnDataFetched<T> callback, String tag) {
        this.context = context;
        this.url = url;
        this.parser = parser;
        this.callback = callback;
        this.tag = tag;
    }

    @Override
    protected String doInBackground(String... params) {
        String token = params[0];
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            if (token != null && !token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + token);
            }
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return "Error: Request failed with code " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.startsWith("Error:")) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            Log.e(tag, result);
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray rows = jsonObject.getJSONArray("rows");
            List<T> parsedList = parser.parse(rows);
            Log.d("data","data==="+rows);
            callback.onFetched(parsedList);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "解析数据失败", Toast.LENGTH_SHORT).show();
        }
    }
}
