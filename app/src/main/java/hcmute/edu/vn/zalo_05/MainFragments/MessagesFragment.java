package hcmute.edu.vn.zalo_05.MainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hcmute.edu.vn.zalo_05.Chat.ChatActivity;
import hcmute.edu.vn.zalo_05.Event.RecentConversationListener;
import hcmute.edu.vn.zalo_05.MainFragments.Adapter.RecentConversationAdapter;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

public class MessagesFragment extends Fragment implements RecentConversationListener {
    private User currentUser;
    private List<ChatMessage> chatMessageList;
    private RecentConversationAdapter recentConversationAdapter;
    private FirebaseFirestore database;

    private RecyclerView conversationsRecyclerView;
    private ProgressBar progressBar;

    public MessagesFragment() {
        // Required empty public constructor
    }

//    init data
    private void init() {
        chatMessageList = new ArrayList<>();
        currentUser = UserService.getInstance(getActivity()).getCurrentUser();

        database = FirebaseFirestore.getInstance();

    }

    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        listenRecentConversations();
    }

//    listen to the event every time a new message arrives to the user
    private void listenRecentConversations() {
        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                .whereEqualTo(String.format("%s.%s", Constants.KEY_USER_JOINED, currentUser.getNumberPhone()), true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Log.d(String.valueOf(getActivity()), error.getMessage());
                            return;
                        }
                        if(value != null) {
                            for(DocumentChange documentChange: value.getDocumentChanges()) {
                                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                    chatMessageList.add(documentChange.getDocument().toObject(ChatMessage.class));
                                }
                                else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                                    ChatMessage temp = documentChange.getDocument().toObject(ChatMessage.class);

                                    for(ChatMessage chatMessage: chatMessageList) {
                                        if(chatMessage.getUserJoined().equals(temp.getUserJoined())) {
                                            chatMessage.setLastMessage(temp.getLastMessage());
                                            chatMessage.setLastSender(temp.getLastSender());
                                            chatMessage.setLastUpdated(temp.getLastUpdated());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(chatMessageList, (obj1, obj2) -> obj2.getLastUpdated().compareTo(obj1.getLastUpdated()));
                        recentConversationAdapter.notifyDataSetChanged();
                        conversationsRecyclerView.smoothScrollToPosition(0);
                        conversationsRecyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        conversationsRecyclerView = view.findViewById(R.id.conversations_recycler_view);
        progressBar = view.findViewById(R.id.fragment_messages_progress_bar);
        recentConversationAdapter = new RecentConversationAdapter(getActivity(),chatMessageList, this);
        conversationsRecyclerView.setAdapter(recentConversationAdapter);
        return view;
    }

    @Override
    public void onRecentConversationClicked(Contact contact, User contactProfile) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_CONTACT, contact);
        intent.putExtra(Constants.KEY_CONTACT_PROFILE, contactProfile);
        startActivity(intent);
    }
}