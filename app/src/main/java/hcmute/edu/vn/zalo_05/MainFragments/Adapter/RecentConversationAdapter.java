package hcmute.edu.vn.zalo_05.MainFragments.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Event.RecentConversationListener;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.CommonUtils;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversationViewHolder>{
    private List<ChatMessage> chatMessageList;
    private Context context;
    private final RecentConversationListener recentConversationListener;

    public RecentConversationAdapter(Context context, List<ChatMessage> chatMessageList, RecentConversationListener recentConversationListener) {
        this.chatMessageList = chatMessageList;
        this.context = context;
        this.recentConversationListener = recentConversationListener;
    }


    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_recent_conversation_layout, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

        ChatMessage chatMessage = chatMessageList.get(position);
        User currentUser = UserService.getInstance(context).getCurrentUser();
//        With the per-chat data retrieved from the Firestore, we iterate over the hash map
//        list of the users who joined the chat
//        where the key is the phone number, the value has a boolean value
        for(Map.Entry<String, Boolean> entry: chatMessage.getUserJoined().entrySet()) {
//            If the key is different from the current user's phone number, that's the person we are contacting

            if(!entry.getKey().equals(currentUser.getNumberPhone())) {

//                from firestore access "Users" collection, query to document whose id is phone number
//                where the document id we need to find here has the value entry.getKey()
//                query path example: User/0703179825/... -> data to get

                FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                        .document(entry.getKey())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
//                                    DocumentSnapshot is a read-only data set stored at the document we are getting
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    if(documentSnapshot.exists() && documentSnapshot != null) {
//                                        If the data set is non-empty, we map this data to the User object (model-class)
                                        User contactProfile = documentSnapshot.toObject(User.class);

//                                        From the found data, we go to the user collection, get the contact list of the current user,
//                                        check if the data of the contactProfile found above exists

//                                        by path: User/${currentUser.get Number Phone()}/Contacts/
                                        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                                                .document(currentUser.getNumberPhone())
                                                .collection(Constants.KEY_COLLECTION_CONTACTS)
                                                .whereEqualTo(Constants.KEY_NUMBER_PHONE, contactProfile.getNumberPhone())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            if(!querySnapshot.isEmpty()) {
                                                                for(QueryDocumentSnapshot dc: querySnapshot) {
                                                                    if(dc.exists() && dc != null) {
//                                                                        If the data is non-empty, we map the data to a Contact object (model-class).
                                                                        Contact contact = new Contact();
                                                                        contact = dc.toObject(Contact.class);
                                                                        holder.setData(chatMessage, contact, contactProfile);
                                                                    }
                                                                    else {
                                                                        holder.setData(chatMessage, null, contactProfile);
                                                                    }

                                                                }
                                                            } else {
                                                                holder.setData(chatMessage, null, contactProfile);
                                                            }
                                                        } else {
                                                            Log.d(String.valueOf(context), "Failed when get contact in RecentConversationAdapter: ", task.getException());
                                                            Toast.makeText(context, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        Toast.makeText(context, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(String.valueOf(context), "Failed when get contactProfile in RecentConversationAdapter: ", task.getException());
                                    Toast.makeText(context, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout containerLayout;
        private TextView contactNameText;
        private TextView dateTimeText;
        private TextView recentMessageText;
        private ShapeableImageView contactImageView;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            containerLayout = itemView.findViewById(R.id.recent_conversation_layout_container);
            contactNameText = itemView.findViewById(R.id.recent_conversation_layout_text_contact_name);
            dateTimeText = itemView.findViewById(R.id.recent_conversation_layout_text_datetime);
            recentMessageText = itemView.findViewById(R.id.recent_conversation_layout_text_recent_message);
            contactImageView = itemView.findViewById(R.id.recent_conversation_layout_contact_image);
        }

        public void setData(ChatMessage chatMessage, Contact contact, User contactProfile) {
            if(contact != null) {
//                If contact is non-empty, set contactNameText with contactName
                this.contactNameText.setText(contact.getContactName());
            } else {
//                set contactNameText with zaloName
                this.contactNameText.setText(contactProfile.getZaloName());
            }

            if(chatMessage.getTypeMessage().equals(Constants.KEY_TYPE_RECORD)) {
//                if the last message has data type as recording,
//                  set recentMessageText is "[Tin nhắn thoại]"
                this.recentMessageText.setText(context.getResources().getString(R.string.recent_message_type_record));
            }
            else {
                this.recentMessageText.setText(chatMessage.getLastMessage());
            }

            this.dateTimeText.setText(CommonUtils.getReadableDateTime(chatMessage.getLastUpdated()));
            if(contactProfile.getAvatarImageUrl() != null && contactProfile.getAvatarImageUrl() != "") {
                Picasso.get().load(contactProfile.getAvatarImageUrl()).into(contactImageView);
            }

//            set event listener for each item in recycler view when user click on it will open chat box
            this.itemView.setOnClickListener(v -> {
                recentConversationListener.onRecentConversationClicked(contact, contactProfile);
            });
        }
    }
}
