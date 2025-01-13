package com.yunsong.bujen.databean;

import java.util.Date;

public class UserBean
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户昵称 */
    private String nickname;

    /** 用户手机号，唯一 */
    private String phone;

    /** 加密存储的用户密码 */
    private String password;

    /** 涂鸦uid */
    private String uid;

    /** 用户个性签名，可为空 */
    private String signature;

    /** 用户功德值，默认为0 */
    private Long virtuePoints;

    /** 用户积分，默认为0 */
    private Long points;

    /** 当前背景ID，外键 */
    private Long currentBackgroundId;

    /** 当前木鱼样式ID，外键 */
    private Long currentMuyuStyleId;

    /** 默认地址ID，外键 */
    private Long defaultAddressId;

    /** 角色 */
    private String roles;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getNickname()
    {
        return nickname;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUid()
    {
        return uid;
    }
    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getSignature()
    {
        return signature;
    }
    public void setVirtuePoints(Long virtuePoints)
    {
        this.virtuePoints = virtuePoints;
    }

    public Long getVirtuePoints()
    {
        return virtuePoints;
    }
    public void setPoints(Long points)
    {
        this.points = points;
    }

    public Long getPoints()
    {
        return points;
    }
    public void setCurrentBackgroundId(Long currentBackgroundId)
    {
        this.currentBackgroundId = currentBackgroundId;
    }

    public Long getCurrentBackgroundId()
    {
        return currentBackgroundId;
    }
    public void setCurrentMuyuStyleId(Long currentMuyuStyleId)
    {
        this.currentMuyuStyleId = currentMuyuStyleId;
    }

    public Long getCurrentMuyuStyleId()
    {
        return currentMuyuStyleId;
    }
    public void setDefaultAddressId(Long defaultAddressId)
    {
        this.defaultAddressId = defaultAddressId;
    }

    public String getRoles()
    {
        return roles;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", uid='" + uid + '\'' +
                ", signature='" + signature + '\'' +
                ", virtuePoints=" + virtuePoints +
                ", points=" + points +
                ", currentBackgroundId=" + currentBackgroundId +
                ", currentMuyuStyleId=" + currentMuyuStyleId +
                ", defaultAddressId=" + defaultAddressId +
                ", roles='" + roles + '\'' +
                '}';
    }
}
