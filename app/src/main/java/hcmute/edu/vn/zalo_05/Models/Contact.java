package hcmute.edu.vn.zalo_05.Models;

import java.io.Serializable;
import java.util.Date;

public class Contact implements Serializable {

    private String numberPhone;
    private String contactName;
    private boolean isFriend;
    private Date createdAt;

    public Contact() {
    }

    public Contact(String numberPhone, String contactName, boolean isFriend, Date createdAt) {
        this.numberPhone = numberPhone;
        this.contactName = contactName;
        this.isFriend = isFriend;
        this.createdAt = createdAt;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "numberPhone='" + numberPhone + '\'' +
                ", contactName='" + contactName + '\'' +
                ", isFriend=" + isFriend +
                ", createdAt=" + createdAt +
                '}';
    }
}
