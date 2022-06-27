package hcmute.edu.vn.zalo_05.Event;

import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.Message;
import hcmute.edu.vn.zalo_05.Models.User;

public interface CallListener {
    public void getHistoryAfterCall(User currentUser, User contactProfile, Message message);
}
