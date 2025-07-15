package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

public class TutorialBundleBean implements CollectItem {
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String remark;

    public Integer id;
    public String name;
    public int level;
    public String description;
    public String tutorialContent;
    public int requiredMeritPoints;
    public int priority;
    public int deleted;

    public boolean sc;
    public boolean dh;
    public String resourceType;
    public String packageUrl;
    public boolean locked;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutorialContent() {
        return tutorialContent;
    }

    public void setTutorialContent(String tutorialContent) {
        this.tutorialContent = tutorialContent;
    }

    public int getRequiredMeritPoints() {
        return requiredMeritPoints;
    }

    public void setRequiredMeritPoints(int requiredMeritPoints) {
        this.requiredMeritPoints = requiredMeritPoints;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
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

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    @Override
    public String toString() {
        return "TutorialBundleBean{" +
                "createBy='" + createBy + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", remark='" + remark + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", description='" + description + '\'' +
                ", tutorialContent='" + tutorialContent + '\'' +
                ", requiredMeritPoints=" + requiredMeritPoints +
                ", priority=" + priority +
                ", deleted=" + deleted +
                ", sc=" + sc +
                ", dh=" + dh +
                ", resourceType='" + resourceType + '\'' +
                ", packageUrl='" + packageUrl + '\'' +
                '}';
    }

    @Override
    public int getType() {
        return 2;
    }

    public String getResourceType() {
        return resourceType;
    }

    @Override
    public int getResourceId() {
        return id!=null?id:0;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
