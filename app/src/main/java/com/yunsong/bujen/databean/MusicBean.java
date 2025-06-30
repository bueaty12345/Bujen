package com.yunsong.bujen.databean;

import java.math.BigDecimal;
import java.util.Date;

public class MusicBean {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer musicId;

    /** 资源类型 */
    private String resourceType;
    /** 音乐封面 */
    private String musicCover;

    /** 音乐连接 */
    private String musicUrl;

    /** 音乐名称 */
    private String musicName;

    /** 歌手名称 */
    private String singer;

    /** 音乐时长（秒） */
    private Long duration;

    /** 评分 */
    private Number rating;

    /** 音乐描述 */
    private String description;

    /** 创建时间 */
    private String createdAt;

    /** 所需功德点 */

    private Integer requiredMeritPoints;

    /** 是否收藏 */
    private Boolean isSC;

    /** 是否兑换 */
    private Boolean isDH;

    public void setMusicId(Integer musicId)
    {
        this.musicId = musicId;
    }

    public Integer getMusicId()
    {
        return musicId;
    }
    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    public String getResourceType()
    {
        return resourceType;
    }
    public void setMusicUrl(String musicUrl)
    {
        this.musicUrl = musicUrl;
    }

    public String getMusicUrl()
    {
        return musicUrl;
    }
    public void setMusicName(String musicName)
    {
        this.musicName = musicName;
    }

    public String getMusicName()
    {
        return musicName;
    }
    public void setSinger(String singer)
    {
        this.singer = singer;
    }

    public String getSinger()
    {
        return singer;
    }
    public void setDuration(Long duration)
    {
        this.duration = duration;
    }

    public Long getDuration()
    {
        return duration;
    }
    public void setRating(Number rating)
    {
        this.rating = rating;
    }

    public Number getRating()
    {
        return rating;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }
    public void setRequiredMeritPoints(Integer requiredMeritPoints)
    {
        this.requiredMeritPoints = requiredMeritPoints;
    }

    public Integer getRequiredMeritPoints()
    {
        return requiredMeritPoints;
    }

    public String getMusicCover() {
        return musicCover;
    }

    public void setMusicCover(String musicCover) {
        this.musicCover = musicCover;
    }

    public Boolean getSC() {
        return isSC;
    }

    public void setSC(Boolean SC) {
        isSC = SC;
    }

    public Boolean getDH() {
        return isDH;
    }

    public void setDH(Boolean DH) {
        isDH = DH;
    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "musicId=" + musicId +
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
                ", isSC=" + isSC +
                ", isDH=" + isDH +
                '}';
    }
}