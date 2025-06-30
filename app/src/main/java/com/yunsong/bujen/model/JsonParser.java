package com.yunsong.bujen.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public interface JsonParser<T> {
    List<T> parse(JSONArray rows) throws JSONException;
}
