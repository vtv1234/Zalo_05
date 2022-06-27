package hcmute.edu.vn.zalo_05.Models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatMessage {
    private Map<String, Boolean> userJoined;
    private Date createdAt;
    private String lastMessage;
    private String typeMessage;
    private String lastSender;
    private Date lastUpdated;

    public ChatMessage() {
    }

    public ChatMessage(Map<String, Boolean> userJoined, Date createdAt, String lastMessage, String typeMessage, String lastSender, Date lastUpdated) {
        this.userJoined = userJoined;
        this.createdAt = createdAt;
        this.lastMessage = lastMessage;
        this.typeMessage = typeMessage;
        this.lastSender = lastSender;
        this.lastUpdated = lastUpdated;
    }

    public Map<String, Boolean> getUserJoined() {
        return userJoined;
    }

    public void setUserJoined(Map<String, Boolean> userJoined) {
        this.userJoined = userJoined;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "userJoined=" + userJoined +
                ", createdAt=" + createdAt +
                ", lastMessage='" + lastMessage + '\'' +
                ", typeMessage='" + typeMessage + '\'' +
                ", lastSender='" + lastSender + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
