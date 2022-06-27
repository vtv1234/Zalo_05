package hcmute.edu.vn.zalo_05.Chat.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.Message;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Utilities.CommonUtils;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ChatMessage chatMessage;
    private List<Message> messages;
    private String senderNumberPhone;
    private String urlImageReceiverContact = null;

    //    Presents string data with the sender being the current user
    public static final int VIEW_TYPE_SENT = 1;

    //    Presents string data with the sender not being the current user
    public static final int VIEW_TYPE_RECEIVED = 2;

    //    Presents audio data with the sender being the current user
    public static final int VIEW_TYPE_SENT_RECORD = 3;

    //    Presents audio data with the sender not being the current user
    public static final int VIEW_TYPE_RECEIVED_RECORD = 4;
    //
    public static final int VIEW_TYPE_SENT_CALL = 5;
    public static final int VIEW_TYPE_RECEIVED_CALL = 6;
    public static final int VIEW_TYPE_SENT_VIDEOCALL = 7;
    public static final int VIEW_TYPE_RECEIVED_VIDEOCALL = 8;


    public ChatAdapter() {
    }

    public ChatAdapter(ChatMessage chatMessage, List<Message> messages, String senderNumberPhone) {
        this.chatMessage = chatMessage;
        this.messages = messages;
        this.senderNumberPhone = senderNumberPhone;
    }

    public ChatAdapter(ChatMessage chatMessage, List<Message> messages, String senderNumberPhone, String urlImageReceiverContact) {
        this.chatMessage = chatMessage;
        this.messages = messages;
        this.senderNumberPhone = senderNumberPhone;
        this.urlImageReceiverContact = urlImageReceiverContact;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        For each viewtype -> return the corresponding view holders to present the data
        View view;
//        switch (viewType) {
//            case VIEW_TYPE_SENT:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message_layout, parent, false);
//                return new SentMessageViewHolder(view);
//            case VIEW_TYPE_RECEIVED:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receiver_message_layout, parent, false);
//                return new ReceivedMessageViewHolder(view);
//            case VIEW_TYPE_SENT_RECORD:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_record_layout, parent, false);
//                return new SentRecordViewHolder(view);
//            case VIEW_TYPE_RECEIVED_RECORD:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receiver_record_layout, parent, false);
//                return new ReceivedRecordViewHolder(view);
//        }
        if(viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message_layout, parent, false);
            return new SentMessageViewHolder(view);
        } else if(viewType == VIEW_TYPE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receiver_message_layout, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else if(viewType == VIEW_TYPE_SENT_RECORD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_record_layout, parent, false);
            return new SentRecordViewHolder(view);
        } else if(viewType == VIEW_TYPE_RECEIVED_RECORD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receiver_record_layout, parent, false);
            return new ReceivedRecordViewHolder(view);
        } else if(viewType == VIEW_TYPE_SENT_CALL){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_call_layout, parent, false);
            return new SentCallViewHolder(view);
        }  else if(viewType == VIEW_TYPE_RECEIVED_CALL){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receive_call_layout, parent, false);
            return new ReceivedCallViewHolder(view);
        } else if(viewType == VIEW_TYPE_SENT_VIDEOCALL){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_call_layout, parent, false);
            return new SentVideoCallViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_receive_call_layout, parent, false);
            return new ReceivedVideoCallViewHolder(view);
        }

//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        switch (getItemViewType(position)) {
//            case VIEW_TYPE_SENT:
//                ((SentMessageViewHolder) holder).setData(messages.get(position));
//            case VIEW_TYPE_RECEIVED:
//                ((ReceivedMessageViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
//            case VIEW_TYPE_SENT_RECORD:
//                ((SentMessageViewHolder) holder).setData(messages.get(position));
//            case VIEW_TYPE_RECEIVED_RECORD:
//                ((ReceivedMessageViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
//        }
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(messages.get(position));
        } else if(getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
        } else if(getItemViewType(position) == VIEW_TYPE_SENT_RECORD){
            ((SentRecordViewHolder) holder).setData(messages.get(position));
        } else if(getItemViewType(position) == VIEW_TYPE_RECEIVED_RECORD){
            ((ReceivedRecordViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
        } else if(getItemViewType(position) == VIEW_TYPE_SENT_CALL){
            ((SentCallViewHolder) holder).setData(messages.get(position));
        } else if(getItemViewType(position) == VIEW_TYPE_RECEIVED_CALL) {
            ((ReceivedCallViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
        } else if(getItemViewType(position) == VIEW_TYPE_SENT_VIDEOCALL){
            ((SentVideoCallViewHolder) holder).setData(messages.get(position));
        } else {
            ((ReceivedVideoCallViewHolder) holder).setData(messages.get(position), urlImageReceiverContact);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        Log.d("ChatAdapter", message.toString());
//        if user is sender and typeMessage is text/string
        if(message.getSenderNumberPhone().equals(senderNumberPhone) &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_TEXT)) {
            return VIEW_TYPE_SENT;
        }

//        if user is not sender and typeMessage is text/string
        else if(message.getSenderNumberPhone() != senderNumberPhone &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_TEXT)){
            return VIEW_TYPE_RECEIVED;
        }

//        if user is sender and typeMessage is audio/record
        else if(message.getSenderNumberPhone().equals(senderNumberPhone) &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_RECORD)
        ) {
            return VIEW_TYPE_SENT_RECORD;
        }
//        if user is not sender and typeMessage is audio/record
        else if(message.getSenderNumberPhone() != senderNumberPhone &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_RECORD)){
            return VIEW_TYPE_RECEIVED_RECORD;
        }
        // if user is sender and typeMessage is call
        else if(message.getSenderNumberPhone().equals(senderNumberPhone) &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_CALL)) {
            return VIEW_TYPE_SENT_CALL;
        }
        // if user is not sender and typeMessage is call
        else if(message.getSenderNumberPhone() != senderNumberPhone &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_CALL)) {
            return VIEW_TYPE_RECEIVED_CALL;
        }
        // if user is sender and typeMessage is videocall
        else if(message.getSenderNumberPhone().equals(senderNumberPhone) &&
                message.getTypeMessage().equals(Constants.KEY_TYPE_VIDEO_CALL)) {
            return VIEW_TYPE_SENT_VIDEOCALL;
        }
        // if user is not sender and typeMessage is videocall
        return VIEW_TYPE_RECEIVED_VIDEOCALL;
    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView sentMessageText;
        private TextView sentDatetimeText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageText = itemView.findViewById(R.id.sent_message_layout_text_message);
            sentDatetimeText = itemView.findViewById(R.id.sent_message_layout_text_datetime);
        }

        public void setData(Message message) {
            this.sentMessageText.setText(message.getMessage());
            this.sentDatetimeText.setText(CommonUtils.getReadableTime(message.getSendAt()));
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView receivedMessageText;
        private TextView receivedDatetimeText;
        private ShapeableImageView imageProfileView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMessageText = itemView.findViewById(R.id.receiver_message_layout_text_message);
            receivedDatetimeText = itemView.findViewById(R.id.receiver_message_layout_text_datetime);
            imageProfileView = itemView.findViewById(R.id.imageProfile);
        }

        public void setData(Message message, String imageProfile) {
            this.receivedMessageText.setText(message.getMessage());
            this.receivedDatetimeText.setText(CommonUtils.getReadableTime(message.getSendAt()));
            if(imageProfile != null && imageProfile != "") {
                Picasso.get().load(imageProfile).into(imageProfileView);
            }
        }
    }

    static class SentRecordViewHolder extends RecyclerView.ViewHolder {
        private VoicePlayerView sentVoicePlayerView;
        private TextView sentDatetimeText;

        public SentRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            sentVoicePlayerView = itemView.findViewById(R.id.sent_record_layout_voicePlayerView);
            sentDatetimeText = itemView.findViewById(R.id.sent_record_layout_text_datetime);
        }

        public void setData(Message message) {
            this.sentVoicePlayerView.setAudio(message.getMessage());
            this.sentDatetimeText.setText(CommonUtils.getReadableTime(message.getSendAt()));
        }

    }

    static class ReceivedRecordViewHolder extends RecyclerView.ViewHolder {
        private VoicePlayerView receivedVoicePlayerView;
        private TextView receivedDatetimeText;
        private ShapeableImageView imageProfileView;

        public ReceivedRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedVoicePlayerView = itemView.findViewById(R.id.receiver_record_layout_voicePlayerView);
            receivedDatetimeText = itemView.findViewById(R.id.receiver_record_layout_text_datetime);
            imageProfileView = itemView.findViewById(R.id.receiver_record_layout_imageProfile);
        }

        public void setData(Message message, String imageProfile) {
            this.receivedVoicePlayerView.setAudio(message.getMessage());
            this.receivedDatetimeText.setText(CommonUtils.getReadableTime(message.getSendAt()));
            if(imageProfile != null && imageProfile != "") {
                Picasso.get().load(imageProfile).into(imageProfileView);
            }
        }
    }
    static class SentCallViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCall;
        private TextView timeCall;

        public SentCallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCall = itemView.findViewById(R.id.sent_call_layout_tv_call);
            timeCall = itemView.findViewById(R.id.sent_call_layout_text_datetime);
        }

        public void setData(Message message) {
            this.tvCall.setText("Cuộc gọi đi");
            this.timeCall.setText(message.getMessage());
        }
    }
    static class ReceivedCallViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCall;
        private TextView timeCall;
        private ShapeableImageView imageProfileView;

        public ReceivedCallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCall = itemView.findViewById(R.id.receive_call_layout_text_call);
            timeCall = itemView.findViewById(R.id.receive_call_layout_text_datetime);
            imageProfileView = itemView.findViewById(R.id.imageProfile);
        }

        public void setData(Message message, String imageProfile) {
            this.tvCall.setText("Cuộc gọi đến ");
            this.timeCall.setText(message.getMessage());
            if(imageProfile != null && imageProfile != "") {
                Picasso.get().load(imageProfile).into(imageProfileView);
            }
        }
    }
    static class SentVideoCallViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoCall;
        private TextView timeVideoCall;

        public SentVideoCallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoCall = itemView.findViewById(R.id.sent_call_layout_tv_call);
            timeVideoCall = itemView.findViewById(R.id.sent_call_layout_text_datetime);
        }

        public void setData(Message message) {
            this.tvVideoCall.setText("Cuộc gọi video đi");
            this.timeVideoCall.setText(message.getMessage());
        }
    }
    static class ReceivedVideoCallViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoCall;
        private TextView timeVideoCall;
        private ShapeableImageView imageProfileView;

        public ReceivedVideoCallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVideoCall = itemView.findViewById(R.id.receive_call_layout_text_call);
            timeVideoCall = itemView.findViewById(R.id.receive_call_layout_text_datetime);
            imageProfileView = itemView.findViewById(R.id.imageProfile);
        }

        public void setData(Message message, String imageProfile) {
            this.tvVideoCall.setText("Cuộc gọi video đến ");
            this.timeVideoCall.setText(message.getMessage());
            if(imageProfile != null && imageProfile != "") {
                Picasso.get().load(imageProfile).into(imageProfileView);
            }
        }
    }

}