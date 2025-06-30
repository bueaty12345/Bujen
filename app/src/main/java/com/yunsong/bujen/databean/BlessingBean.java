package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

import java.io.Serializable;

public class BlessingBean implements Serializable, CollectItem {
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String remark;                  // 备注信息（可为空）

    public Integer blessingId;                 // 祝福 ID
    public String resourceType;            // 资源类型（例如 "Blessing"）
    public String blessingCategory;        // 分类标识（如 "6"）
    public String blessingBackgroundUrl;   // 背景图片地址或编号
    public String blessingTheme;           // 主题文字
    public String zenQuote;                // 禅语内容
    public String createdAt;               // 创建时间（yyyy-MM-dd）

    public int requiredMeritPoints;        // 所需功德值
    public boolean sc;                     // 是否收藏
    public boolean dh;                     // 是否兑换
    public String blessingMethod;

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

    public Integer getBlessingId() {
        return blessingId;
    }

    public void setBlessingId(Integer blessingId) {
        this.blessingId = blessingId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getBlessingCategory() {
        return blessingCategory;
    }

    public void setBlessingCategory(String blessingCategory) {
        this.blessingCategory = blessingCategory;
    }

    public String getBlessingBackgroundUrl() {
        return blessingBackgroundUrl;
    }

    public void setBlessingBackgroundUrl(String blessingBackgroundUrl) {
        this.blessingBackgroundUrl = blessingBackgroundUrl;
    }

    public String getBlessingTheme() {
        return blessingTheme;
    }

    public void setBlessingTheme(String blessingTheme) {
        this.blessingTheme = blessingTheme;
    }

    public String getZenQuote() {
        return zenQuote;
    }

    public void setZenQuote(String zenQuote) {
        this.zenQuote = zenQuote;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getRequiredMeritPoints() {
        return requiredMeritPoints;
    }

    public void setRequiredMeritPoints(int requiredMeritPoints) {
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

    public String getBlessingMethod() {
        return blessingMethod;
    }

    public void setBlessingMethod(String blessingMethod) {
        this.blessingMethod = blessingMethod;
    }

    @Override
    public String toString() {
        return "BlessingBean{" +
                "createBy='" + createBy + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", remark='" + remark + '\'' +
                ", blessingId=" + blessingId +
                ", resourceType='" + resourceType + '\'' +
                ", blessingCategory='" + blessingCategory + '\'' +
                ", blessingBackgroundUrl='" + blessingBackgroundUrl + '\'' +
                ", blessingTheme='" + blessingTheme + '\'' +
                ", zenQuote='" + zenQuote + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", requiredMeritPoints=" + requiredMeritPoints +
                ", sc=" + sc +
                ", dh=" + dh +
                ", blessingMethod='" + blessingMethod + '\'' +
                '}';
    }

    @Override
    public int getType() {
        return 3;
    }
}
