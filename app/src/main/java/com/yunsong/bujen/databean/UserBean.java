package com.yunsong.bujen.databean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class UserBean
{
    public static final long serialVersionUID = 1L;

    /** $column.columnComment */
    public int id;

    /** 用户昵称 */
    public String nickname;

    /** 用户手机号，唯一 */
    public String phone;

    /** 加密存储的用户密码 */
    public String password;

    /** 涂鸦uid */
    public String uid;

    /** 用户个性签名，可为空 */
    public String signature;

    /** 用户功德值，默认为0 */
    public int virtuePoints;

    /** 用户积分，默认为0 */
    public int points;

    /** 当前背景ID，外键 */
    public int currentBackgroundId;

    /** 当前木鱼样式ID，外键 */
    public int currentMuyuStyleId;

    /** 默认地址ID，外键 */
    public int defaultAddressId;

    /** 角色 */
    public String roles;

    public String createdAt;
    public String updatedAt;
    public String email;
    public String avatar;
    public String gender;

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("nickname", nickname);
        obj.put("phone", phone);
        obj.put("password", password);
        obj.put("uid", uid);
        obj.put("signature", signature);
        obj.put("virtuePoints", virtuePoints);
        obj.put("points", points);
        obj.put("currentBackgroundId", currentBackgroundId);
        obj.put("currentMuyuStyleId", currentMuyuStyleId);
        obj.put("defaultAddressId", defaultAddressId);
        obj.put("createdAt", createdAt);
        obj.put("updatedAt", updatedAt);
        obj.put("roles", roles);
        obj.put("email", email);
        obj.put("avatar", avatar);
        obj.put("gender", gender);
        return obj;
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
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getVirtuePoints() {
        return virtuePoints;
    }

    public void setVirtuePoints(int virtuePoints) {
        this.virtuePoints = virtuePoints;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCurrentBackgroundId() {
        return currentBackgroundId;
    }

    public void setCurrentBackgroundId(int currentBackgroundId) {
        this.currentBackgroundId = currentBackgroundId;
    }

    public int getCurrentMuyuStyleId() {
        return currentMuyuStyleId;
    }

    public void setCurrentMuyuStyleId(int currentMuyuStyleId) {
        this.currentMuyuStyleId = currentMuyuStyleId;
    }

    public int getDefaultAddressId() {
        return defaultAddressId;
    }

    public void setDefaultAddressId(int defaultAddressId) {
        this.defaultAddressId = defaultAddressId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
