package hcmute.edu.vn.zalo_05.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String zaloName;
    private String userName;
    private boolean onlineStatus;
    private String numberPhone;
    private String gender;
    private String birthdate;
    private String avatarImageUrl;
    private Date createdAt;
    private List<String> conversationList;
    private Date lastOnline;

    public User() {
    }

    public User(String zaloName, String userName, boolean onlineStatus, String numberPhone, String gender, String birthdate, String avatarImageUrl, Date createdAt, List<String> conversationList, Date lastOnline) {
        this.zaloName = zaloName;
        this.userName = userName;
        this.onlineStatus = onlineStatus;
        this.numberPhone = numberPhone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.avatarImageUrl = avatarImageUrl;
        this.createdAt = createdAt;
        this.conversationList = conversationList;
        this.lastOnline = lastOnline;
    }

    public String getZaloName() {
        return zaloName;
    }

    public void setZaloName(String zaloName) {
        this.zaloName = zaloName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getConversationList() {
        if(conversationList == null) {
            conversationList = new ArrayList<>();
        }
        return conversationList;
    }

    public void setConversationList(List<String> conversationList) {
        this.conversationList = conversationList;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    @Override
    public String toString() {
        return "User{" +
                "zaloName='" + zaloName + '\'' +
                ", userName='" + userName + '\'' +
                ", onlineStatus=" + onlineStatus +
                ", numberPhone='" + numberPhone + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", avatarImageUrl='" + avatarImageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", conversationList=" + conversationList +
                ", lastOnline=" + lastOnline +
                '}';
    }
}
