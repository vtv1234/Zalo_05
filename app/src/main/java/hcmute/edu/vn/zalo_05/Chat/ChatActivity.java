package hcmute.edu.vn.zalo_05.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.zalo_05.BaseActivity;
import hcmute.edu.vn.zalo_05.Chat.Adapter.ChatAdapter;
import hcmute.edu.vn.zalo_05.Chat.Fragments.RecordFragment;
import hcmute.edu.vn.zalo_05.Event.RecordResultListener;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.Message;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

public class ChatActivity extends BaseActivity implements RecordResultListener {

    private Contact receiverContact;
    private User contactProfile;
    private ChatMessage chatMessage;
    private List<Message> messages;

    private MaterialToolbar toolbar;
    private FrameLayout actionContentFrame;
    private AppCompatImageView sendHandlerIcon;
    private EditText inputMessageText;
    private ProgressBar progressBar;
    private LinearLayout actionsMenuLayout;
    private TextView recordAction;
    boolean isKeyboardShowing = false;


    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private User currentUser;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        View rootView = findViewById(R.id.activity_chat_root_view);

        this.toolbar = findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(this.toolbar);

        this.actionsMenuLayout = findViewById(R.id.activity_chat_actions_menu_layout);
        this.progressBar = findViewById(R.id.activity_chat_progressBar);
        this.inputMessageText = findViewById(R.id.activity_chat_input_message_text);
        this.sendHandlerIcon = findViewById(R.id.activity_chat_send_image_view);
        this.recordAction = findViewById(R.id.activity_chat_record_action);
        this.actionContentFrame = findViewById(R.id.activity_chat_action_content_frame);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }

                    }
                }
        );
        setListeners();

        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void onKeyboardVisibilityChanged(boolean opened) {
//        if(actionContentFrame.getVisibility() == View.VISIBLE) {
//            actionContentFrame.setVisibility(View.GONE);
//
//        }
        Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.activity_chat_action_content_frame);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(opened == true && actionContentFrame.getVisibility() == View.VISIBLE) {
            Log.d(String.valueOf(ChatActivity.this), "keyboard changed to open and actionContentFrame being visible");
            if(recordFragment != null) {
                fragmentTransaction.remove(recordFragment);
                fragmentTransaction.commit();
            }
            actionContentFrame.setVisibility(View.GONE);
            Log.d(String.valueOf(ChatActivity.this), "set actionContentFrame to gone");

        }
    }

    private void init() {
        currentUser = UserService.getInstance(getApplicationContext()).getCurrentUser();
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessage,
                messages,
                currentUser.getNumberPhone(),
                contactProfile.getAvatarImageUrl()
        );

        chatRecyclerView = findViewById(R.id.activity_chat_chat_recycler_view);
        chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
        inputMessageText.requestFocus();
        actionsMenuLayout.setVisibility(View.VISIBLE);
        sendHandlerIcon.setVisibility(View.GONE);
    }
    private void sendMessage(Message message) {
        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                .whereEqualTo(String.format("%s.%s", Constants.KEY_USER_JOINED, currentUser.getNumberPhone()), true)
                .whereEqualTo(String.format("%s.%s", Constants.KEY_USER_JOINED, contactProfile.getNumberPhone()), true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()) {
                                for(QueryDocumentSnapshot snapshot: querySnapshot) {
                                    ChatMessage chatMessageGet = snapshot.toObject(ChatMessage.class);
                                    if(chatMessageGet.getUserJoined().size() == 2) {
//                                        Update last message for conversation
                                        Map<String, Object> updateConversation = new HashMap<>();
                                        updateConversation.put(Constants.KEY_LAST_MESSAGE, message.getMessage());
                                        updateConversation.put(Constants.KEY_LAST_UPDATED, message.getSendAt());
                                        updateConversation.put(Constants.KEY_LAST_SENDER, message.getSenderNumberPhone());
                                        updateConversation.put(Constants.KEY_TYPE_MESSAGE, message.getTypeMessage());

                                        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                .document(snapshot.getId())
                                                .update(updateConversation)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                                .document(snapshot.getId())
                                                                .collection(Constants.KEY_COLLECTION_MESSAGE)
                                                                .add(message);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(String.valueOf(ChatActivity.this), "Failed when update last message for exists conversation: ", e);
                                                        Toast.makeText(ChatActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                        break;
                                    }
                                }
                            }
                            else {
                                ChatMessage newConversation = new ChatMessage();
                                newConversation.setCreatedAt(new Date());
                                newConversation.setLastUpdated(new Date());

                                Map<String, Boolean> userJoined = new HashMap<>();
                                userJoined.put(currentUser.getNumberPhone(), true);
                                userJoined.put(contactProfile.getNumberPhone(), true);

                                newConversation.setUserJoined(userJoined);
                                newConversation.setLastMessage(message.getMessage());
                                newConversation.setTypeMessage(message.getTypeMessage());
                                newConversation.setLastSender(message.getSenderNumberPhone());
                                database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                        .add(newConversation)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentReference documentReference = task.getResult();
                                                    documentReference.collection(Constants.KEY_COLLECTION_MESSAGE)
                                                            .add(message);

                                                    currentUser.getConversationList().add(documentReference.getId());
                                                    UserService.getInstance(ChatActivity.this).updateCurrentUser(currentUser);
//
                                                    contactProfile.getConversationList().add(documentReference.getId());
                                                    UserService.getInstance(ChatActivity.this).updateConversationListForUser(contactProfile);


                                                } else {
                                                    Log.d(String.valueOf(ChatActivity.this), "Failed when create new conversation: ", task.getException());
                                                    Toast.makeText(ChatActivity.this, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                        else {
                            Log.d(String.valueOf(ChatActivity.this), "Failed when finding conversation: ", task.getException());
                            Toast.makeText(ChatActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendTextMessage() {
        Message message = new Message();
        message.setSenderNumberPhone(currentUser.getNumberPhone());
        message.setMessage(inputMessageText.getText().toString().trim());
        message.setTypeMessage(Constants.KEY_TYPE_TEXT);
        message.setSendAt(new Date());

        sendMessage(message);
        inputMessageText.setText(null);

    }

    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                .whereEqualTo(String.format("%s.%s", Constants.KEY_USER_JOINED, currentUser.getNumberPhone()), true)
                .whereEqualTo(String.format("%s.%s", Constants.KEY_USER_JOINED, contactProfile.getNumberPhone()), true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()) {
                                for(QueryDocumentSnapshot snapshot: querySnapshot) {
                                    ChatMessage chatMessageGet = snapshot.toObject(ChatMessage.class);
                                    if(chatMessageGet.getUserJoined().size() == 2) {
                                        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                .document(snapshot.getId())
                                                .collection(Constants.KEY_COLLECTION_MESSAGE)
                                                .whereEqualTo(Constants.KEY_SENDER_NUMBER_PHONE, currentUser.getNumberPhone())
                                                .addSnapshotListener(evenListener);

                                        database.collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                .document(snapshot.getId())
                                                .collection(Constants.KEY_COLLECTION_MESSAGE)
                                                .whereEqualTo(Constants.KEY_SENDER_NUMBER_PHONE, contactProfile.getNumberPhone())
                                                .addSnapshotListener(evenListener);

                                    }
                                }
                            }
                        }
                    }
                });
    }

    private final EventListener<QuerySnapshot> evenListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            int count = messages.size();
            for(DocumentChange dc: value.getDocumentChanges()) {
                if(dc.getType() == DocumentChange.Type.ADDED) {
                    Message message = new Message();
                    message = dc.getDocument().toObject(Message.class);
                    messages.add(message);
                }
            }
            Collections.sort(messages, (obj1, obj2) -> obj1.getSendAt().compareTo(obj2.getSendAt()));
            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(messages.size(), messages.size());
                chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
            }
            chatRecyclerView.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    };

    private void loadReceiverDetails() {
        receiverContact = (Contact) getIntent().getSerializableExtra(Constants.KEY_CONTACT);
        contactProfile = (User) getIntent().getSerializableExtra(Constants.KEY_CONTACT_PROFILE);
        if(receiverContact == null) {
            this.getSupportActionBar().setTitle(contactProfile.getZaloName());
        }
        else {
            this.getSupportActionBar().setTitle(receiverContact.getContactName());
        }
    }

    private void setListeners() {
        this.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        this.inputMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputMessage = inputMessageText.getText().toString().trim();
                if(inputMessage.length() > 0) {
                    actionsMenuLayout.setVisibility(View.GONE);
                    sendHandlerIcon.setVisibility(View.VISIBLE);
                }
                else {
                    sendHandlerIcon.setVisibility(View.GONE);
                    actionsMenuLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.recordAction.setOnClickListener(v -> {
            Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.activity_chat_action_content_frame);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if(actionContentFrame.getVisibility() != View.VISIBLE) {
                Log.d(String.valueOf(ChatActivity.this), "actionContentFrame is invisible");
                if(isKeyboardShowing) {
                    Log.d(String.valueOf(ChatActivity.this), "keyboard is showing");
                    View view = getCurrentFocus();
                    if(view != null) {
                        Log.d(String.valueOf(ChatActivity.this), "hide keyboard");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                }
                if(recordFragment == null) {
                    fragmentTransaction.add(R.id.activity_chat_action_content_frame, new RecordFragment(this));
                    fragmentTransaction.commit();
                }
                Log.d(String.valueOf(ChatActivity.this), "set actionContentFrame visible");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actionContentFrame.setVisibility(View.VISIBLE);
                    }
                },350);

            } else {
                Log.d(String.valueOf(ChatActivity.this), "actionContentFrame is visible");

                if(recordFragment != null) {
                    fragmentTransaction.remove(recordFragment);
                    Log.d(String.valueOf(ChatActivity.this), "Remove recordFragment existing");
                    fragmentTransaction.commit();
                }
                Log.d(String.valueOf(ChatActivity.this), "set contentFrame is gone");

                actionContentFrame.setVisibility(View.GONE);
            }

        });
        this.sendHandlerIcon.setOnClickListener(v -> sendTextMessage());

    }

    public void closeRecordFragment() {
        Fragment recordFragment = getSupportFragmentManager().findFragmentById(R.id.activity_chat_action_content_frame);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(recordFragment);
        fragmentTransaction.commit();
        actionContentFrame.setVisibility(View.GONE);
    }

    @Override
    public void getResult(Message message) {
        if(message != null) {
            sendMessage(message);
            closeRecordFragment();
        }
    }

    @Override
    public void onCancel() {
        closeRecordFragment();

    }
}