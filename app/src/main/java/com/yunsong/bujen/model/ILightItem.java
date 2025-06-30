package com.yunsong.bujen.model;

public interface ILightItem {
    String getName();
    String getAuthor();
    String getGdd(); // 功德点信息
    Long getDuration(); // 可选
    Number getRating(); // 可选
    Boolean isCollected(); // 收藏
    Boolean isExchanged(); // 是否已兑换
}
