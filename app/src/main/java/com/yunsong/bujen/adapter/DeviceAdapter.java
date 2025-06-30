package com.yunsong.bujen.adapter;

import static com.thingclips.smart.android.tangram.utils.AppUtils.runOnUiThread;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.yunsong.bujen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceAdapter extends BaseAdapter {
    private Context context;
    private List<DeviceBean> deviceList;
    private Map<String, Integer> batteryMap  = new HashMap<>();

    public DeviceAdapter(Context context, List<DeviceBean> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    public void updateBattery(String devId, int battery) {
        Log.d("BatteryUpdate", "设备：" + devId + " 电量：" + battery);
        batteryMap.put(devId, battery);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_devices, parent, false);
            holder = new ViewHolder();
            holder.txtName = convertView.findViewById(R.id.textView2);
            holder.txtBattery = convertView.findViewById(R.id.textViewBattery);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DeviceBean device = deviceList.get(position);
        holder.txtName.setText(device.getName());

        Log.d("AdapterCheck", "getView deviceId=" + device.getDevId());
        Log.d("AdapterCheck", "batteryMap keys=" + batteryMap.keySet());

        // 设置电量显示
        Integer battery = batteryMap.get(device.getDevId());
        if(battery != null){
            holder.txtBattery.setText("电量：" + battery + "%");
        } else {
            holder.txtBattery.setText("电量：未知");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtBattery;
    }

}
