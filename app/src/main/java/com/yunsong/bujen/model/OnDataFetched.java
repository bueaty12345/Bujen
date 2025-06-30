package com.yunsong.bujen.model;

import java.util.List;

public interface OnDataFetched<T> {
    void onFetched(List<T> data);
}