package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

public class MyPrayBean implements CollectItem {
    public Integer blessing_id;
    public String resource_type;
    public String blessing_category;
    public String blessing_background_url;
    public String blessing_theme;
    public String zen_quote;
    public String created_at;
    public Integer required_merit_points;

    @Override
    public String toString() {
        return "MyPrayBean{" +
                "blessing_id=" + blessing_id +
                ", resource_type='" + resource_type + '\'' +
                ", blessing_category='" + blessing_category + '\'' +
                ", blessing_background_url='" + blessing_background_url + '\'' +
                ", blessing_theme='" + blessing_theme + '\'' +
                ", zen_quote='" + zen_quote + '\'' +
                ", created_at='" + created_at + '\'' +
                ", required_merit_points=" + required_merit_points +
                '}';
    }

    public Integer getBlessing_id() {
        return blessing_id;
    }

    public void setBlessing_id(Integer blessing_id) {
        this.blessing_id = blessing_id;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getBlessing_category() {
        return blessing_category;
    }

    public void setBlessing_category(String blessing_category) {
        this.blessing_category = blessing_category;
    }

    public String getBlessing_background_url() {
        return blessing_background_url;
    }

    public void setBlessing_background_url(String blessing_background_url) {
        this.blessing_background_url = blessing_background_url;
    }

    public String getBlessing_theme() {
        return blessing_theme;
    }

    public void setBlessing_theme(String blessing_theme) {
        this.blessing_theme = blessing_theme;
    }

    public String getZen_quote() {
        return zen_quote;
    }

    public void setZen_quote(String zen_quote) {
        this.zen_quote = zen_quote;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getRequired_merit_points() {
        return required_merit_points;
    }

    public void setRequired_merit_points(Integer required_merit_points) {
        this.required_merit_points = required_merit_points;
    }

    @Override
    public int getType() {
        return 3;
    }
}
