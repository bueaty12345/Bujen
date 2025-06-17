package com.yunsong.bujen.databean;

import com.yunsong.bujen.model.CollectItem;

import java.util.Date;

public class MyMusicBean implements CollectItem {
    public String createBy;
    public Date createTime;
    public String updateBy;
    public Date updateTime;
    public String remark;
    public Long musicId;
    public String resourceType;
    public String musicCover;
    public String musicUrl;
    public String musicName;
    public String singer;
    public Integer duration;
    public Double rating;
    public String description;
    public Date createdAt;
    public Integer requiredMeritPoints;
    public Integer sc;
    public Integer dh;

    @Override
    public String toString() {
        return "MyMusicBean{" +
                "createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                ", remark='" + remark + '\'' +
                ", musicId=" + musicId +
                ", resourceType='" + resourceType + '\'' +
                ", musicCover='" + musicCover + '\'' +
                ", musicUrl='" + musicUrl + '\'' +
                ", musicName='" + musicName + '\'' +
                ", singer='" + singer + '\'' +
                ", duration=" + duration +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMusicId() {
        return musicId;
    }

    public void setMusicId(Long musicId) {
        this.musicId = musicId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getMusicCover() {
        return musicCover;
    }

    public void setMusicCover(String musicCover) {
        this.musicCover = musicCover;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRequiredMeritPoints() {
        return requiredMeritPoints;
    }

    public void setRequiredMeritPoints(Integer requiredMeritPoints) {
        this.requiredMeritPoints = requiredMeritPoints;
    }

    public Integer getSc() {
        return sc;
    }

    public void setSc(Integer sc) {
        this.sc = sc;
    }

    public Integer getDh() {
        return dh;
    }

    public void setDh(Integer dh) {
        this.dh = dh;
    }


    @Override
    public int getType() {
        return 0;
    }
}
