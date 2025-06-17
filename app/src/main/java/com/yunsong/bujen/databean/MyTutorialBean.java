package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

public class MyTutorialBean implements CollectItem {
    public String createBy;
    public String createTime;
    public String updateBy;
    public String updateTime;
    public String remark;
    public int tutorialId;
    public String resourceType;
    public String tutorialCategory;
    public String videoUrl;
    public String backgroundMusicUrl;
    public String tutorialName;
    public String tutorialContent;
    public String author;
    public int rating;
    public String description;
    public String createdAt;
    public int requiredMeritPoints;
    public Integer sc;

    public Integer getDh() {
        return dh;
    }

    public void setDh(Integer dh) {
        this.dh = dh;
    }

    public Integer dh;

    public Integer getSc() {
        return sc;
    }

    public void setSc(Integer sc) {
        this.sc = sc;
    }

    @Override
    public String toString() {
        return "MyTutorial{" +
                "createBy='" + createBy + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", remark='" + remark + '\'' +
                ", tutorialId=" + tutorialId +
                ", resourceType='" + resourceType + '\'' +
                ", tutorialCategory='" + tutorialCategory + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundMusicUrl='" + backgroundMusicUrl + '\'' +
                ", tutorialName='" + tutorialName + '\'' +
                ", tutorialContent='" + tutorialContent + '\'' +
                ", author='" + author + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", requiredMeritPoints=" + requiredMeritPoints +
                ", sc='" + sc + '\'' +
                ", dh='" + dh + '\'' +
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

    public int getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(int tutorialId) {
        this.tutorialId = tutorialId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getTutorialCategory() {
        return tutorialCategory;
    }

    public void setTutorialCategory(String tutorialCategory) {
        this.tutorialCategory = tutorialCategory;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getBackgroundMusicUrl() {
        return backgroundMusicUrl;
    }

    public void setBackgroundMusicUrl(String backgroundMusicUrl) {
        this.backgroundMusicUrl = backgroundMusicUrl;
    }

    public String getTutorialName() {
        return tutorialName;
    }

    public void setTutorialName(String tutorialName) {
        this.tutorialName = tutorialName;
    }

    public String getTutorialContent() {
        return tutorialContent;
    }

    public void setTutorialContent(String tutorialContent) {
        this.tutorialContent = tutorialContent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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

    public int getRequiredMeritPoints() {
        return requiredMeritPoints;
    }

    public void setRequiredMeritPoints(int requiredMeritPoints) {
        this.requiredMeritPoints = requiredMeritPoints;
    }


    @Override
    public int getType() {
        return 2;
    }
}
