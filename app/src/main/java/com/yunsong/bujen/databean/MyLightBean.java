package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

public class MyLightBean implements CollectItem {
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String remark;

    public Integer backgroundId;
    public String resourceType;
    public String backgroundImageUrl;
    public String backgroundName;
    public String author;

    public Double rating;
    public String description;
    public String createdAt;

    public Integer requiredMeritPoints;
    public boolean sc;
    public boolean dh;

    @Override
    public String toString() {
        return "MyLightBean{" +
                "createBy='" + createBy + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", remark='" + remark + '\'' +
                ", backgroundId=" + backgroundId +
                ", resourceType='" + resourceType + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", backgroundName='" + backgroundName + '\'' +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", requiredMeritPoints=" + requiredMeritPoints +
                ", sc=" + sc +
                ", dh=" + dh +
                '}';
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(Integer backgroundId) {
        this.backgroundId = backgroundId;
    }

    public String getResourceType() {
        return resourceType;
    }

    @Override
    public int getResourceId() {
        return backgroundId != null ? backgroundId : 0;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRequiredMeritPoints() {
        return requiredMeritPoints;
    }

    public void setRequiredMeritPoints(Integer requiredMeritPoints) {
        this.requiredMeritPoints = requiredMeritPoints;
    }

    public boolean isSc() {
        return sc;
    }

    public void setSc(boolean sc) {
        this.sc = sc;
    }

    public boolean isDh() {
        return dh;
    }

    public void setDh(boolean dh) {
        this.dh = dh;
    }

    @Override
    public int getType() {
        return 1;
    }
}
