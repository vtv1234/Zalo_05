package hcmute.edu.vn.zalo_05.Event;

import hcmute.edu.vn.zalo_05.Models.Message;

public interface RecordResultListener {
    void getResult(Message message);
    void onCancel();
}
