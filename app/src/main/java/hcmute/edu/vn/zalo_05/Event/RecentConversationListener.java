package hcmute.edu.vn.zalo_05.Event;

import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.User;

public interface RecentConversationListener {
    void onRecentConversationClicked(Contact contact, User contactProfile);
}
