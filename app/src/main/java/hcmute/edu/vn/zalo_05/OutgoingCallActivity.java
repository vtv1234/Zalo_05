package hcmute.edu.vn.zalo_05;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.Message;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R.id;
import hcmute.edu.vn.zalo_05.R.drawable;
import hcmute.edu.vn.zalo_05.R.layout;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;


public class OutgoingCallActivity extends BaseActivity implements View.OnClickListener {
    private FrameLayout vLocal;
    private FrameLayout vRemote;
    private TextView tvState;
    private ImageButton btnMute;
    private ImageButton btnSpeaker;
    private ImageButton btnVideo;
    private ImageButton btnEnd;
    private ImageButton btnSwitch;
    private View vControl;

    private StringeeCall stringeeCall;
    private SensorManagerUtils sensorManagerUtils;
    private StringeeAudioManager audioManager;
    private String from;
    private String to;
    private boolean isVideoCall;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;
    private boolean isPermissionGranted = true;

    private StringeeCall.MediaState mMediaState;
    private StringeeCall.SignalingState mSignalingState;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private long differentInSeconds;
    private User currentUser;
    private Contact receiverContact;
    private User contactProfile;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = UserService.getInstance(getApplicationContext()).getCurrentUser();
        loadReceiverDetails();
        //add Flag for show on lockScreen and disable keyguard
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        setContentView(layout.activity_outgoing_call);

        sensorManagerUtils = SensorManagerUtils.getInstance(this);
        sensorManagerUtils.acquireProximitySensor(getLocalClassName());
        sensorManagerUtils.disableKeyguard();

        Common.isInCall = true;

        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        isVideoCall = getIntent().getBooleanExtra("is_video_call", false);

        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> lstPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                lstPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (isVideoCall) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(Manifest.permission.CAMERA);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    lstPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
                }
            }

            if (lstPermissions.size() > 0) {
                String[] permissions = new String[lstPermissions.size()];
                for (int i = 0; i < lstPermissions.size(); i++) {
                    permissions[i] = lstPermissions.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, Common.REQUEST_PERMISSION_CALL);
                return;
            }
        }

        makeCall();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = false;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                } else {
                    isGranted = true;
                }
            }
        }
        if (requestCode == Common.REQUEST_PERMISSION_CALL) {
            if (!isGranted) {
                isPermissionGranted = false;
                tvState.setText("Ended");
                dismissLayout();
            } else {
                isPermissionGranted = true;
                makeCall();
            }
        }
    }

    private void initView() {
        vLocal = findViewById(id.v_local);
        vRemote = findViewById(id.v_remote);

        vControl = findViewById(id.v_control);

        TextView tvTo = findViewById(id.tv_to);
        tvTo.setText(to);
        tvState = findViewById(id.tv_state);

        btnMute = findViewById(id.btn_mute);
        btnMute.setOnClickListener(this);
        btnSpeaker = findViewById(id.btn_speaker);
        btnSpeaker.setOnClickListener(this);
        btnVideo = findViewById(id.btn_video);
        btnVideo.setOnClickListener(this);
        btnSwitch = findViewById(id.btn_switch);
        btnSwitch.setOnClickListener(this);
        btnEnd = findViewById(id.btn_end);
        btnEnd.setOnClickListener(this);

        isSpeaker = isVideoCall;
        btnSpeaker.setBackgroundResource(isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off);

        isVideo = isVideoCall;
        btnVideo.setImageResource(isVideo ? drawable.btn_video : drawable.btn_video_off);

        btnVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);
    }

    private void makeCall() {
        //create audio manager to control audio device
        audioManager = StringeeAudioManager.create(OutgoingCallActivity.this);
        audioManager.start((selectedAudioDevice, availableAudioDevices) ->
                Log.d(Common.TAG, "selectedAudioDevice: " + selectedAudioDevice + " - availableAudioDevices: " + availableAudioDevices));
        audioManager.setSpeakerphoneOn(isVideoCall);

        //make new call
        stringeeCall = new StringeeCall(Common.client, from, to);
        stringeeCall.setVideoCall(isVideoCall);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, final StringeeCall.SignalingState signalingState, String reason, int sipCode, String sipReason) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onSignalingStateChange: " + signalingState);
                    mSignalingState = signalingState;
                    switch (signalingState) {
                        case CALLING:
                            tvState.setText("Outgoing call");
                            break;
                        case RINGING:
                            tvState.setText("Ringing");
                            break;
                        case ANSWERED:
                            tvState.setText("Starting");
                            if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                                tvState.setText("Started");
                            }
                            break;
                        case BUSY:
                            tvState.setText("Busy");
                            endCall();
                            break;
                        case ENDED:
                            tvState.setText("Ended");
                            endCall();
                            break;
                    }
                });
            }

            @Override
            public void onError(StringeeCall stringeeCall, int code, String desc) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onError: " + desc);
                    Utils.reportMessage(OutgoingCallActivity.this, desc);
                    tvState.setText("Ended");
                    dismissLayout();
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String desc) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, final StringeeCall.MediaState mediaState) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onMediaStateChange: " + mediaState);
                    mMediaState = mediaState;
                    if (mediaState == StringeeCall.MediaState.CONNECTED) {
                        if (mSignalingState == StringeeCall.SignalingState.ANSWERED) {
                            tvState.setText("Started");
                        }
                    }
                });
            }

            @Override
            public void onLocalStream(final StringeeCall stringeeCall) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onLocalStream");
                    if (stringeeCall.isVideoCall()) {
                        vLocal.removeAllViews();
                        vLocal.addView(stringeeCall.getLocalView());
                        stringeeCall.renderLocalView(true);
                    }
                });
            }

            @Override
            public void onRemoteStream(final StringeeCall stringeeCall) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onRemoteStream");
                    if (stringeeCall.isVideoCall()) {
                        vRemote.removeAllViews();
                        vRemote.addView(stringeeCall.getRemoteView());
                        stringeeCall.renderRemoteView(false);
                    }
                });
            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, final JSONObject jsonObject) {
                runOnUiThread(() -> Log.d(Common.TAG, "onCallInfo: " + jsonObject.toString()));
            }
        });

        stringeeCall.makeCall();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startDate=LocalDateTime.now();
        }
        Log.e("start",startDate.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case id.btn_mute:
                isMute = !isMute;
                btnMute.setBackgroundResource(isMute ? drawable.btn_mute : drawable.btn_mic);
                if (stringeeCall != null) {
                    stringeeCall.mute(isMute);
                }
                break;
            case id.btn_speaker:
                isSpeaker = !isSpeaker;
                btnSpeaker.setBackgroundResource(isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off);
                if (audioManager != null) {
                    audioManager.setSpeakerphoneOn(isSpeaker);
                }
                break;
            case id.btn_end:
                tvState.setText("Ended");
                endCall();
                break;
            case id.btn_video:
                isVideo = !isVideo;
                btnVideo.setImageResource(isVideo ? drawable.btn_video : drawable.btn_video_off);
                if (stringeeCall != null) {
                    stringeeCall.enableVideo(isVideo);
                }
                break;
            case id.btn_switch:
                if (stringeeCall != null) {
                    stringeeCall.switchCamera(new StatusListener() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(StringeeError stringeeError) {
                            super.onError(stringeeError);
                            runOnUiThread(() -> {
                                Log.d(Common.TAG, "switchCamera error: " + stringeeError.getMessage());
                                Utils.reportMessage(OutgoingCallActivity.this, stringeeError.getMessage());
                            });
                        }
                    });
                }
                break;
        }
    }


    private void endCall() {
        stringeeCall.hangup();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            endDate = LocalDateTime.now();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            differentInSeconds = Duration.between(startDate, endDate).getSeconds();
        }
        Log.e("end",endDate.toString());
        Message message= new Message();
        message.setSenderNumberPhone(currentUser.getNumberPhone());
        message.setTypeMessage(Constants.KEY_TYPE_CALL);
        message.setMessage(differentInSeconds+"");
        message.setSendAt(new Date());
        sendMessage(message);
        dismissLayout();
    }

    private void dismissLayout() {
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        sensorManagerUtils.releaseSensor();
        vControl.setVisibility(View.GONE);
        btnEnd.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
        Utils.postDelay(() -> {
            Common.isInCall = false;
            if (!isPermissionGranted) {
                Intent intent = new Intent();
                intent.setAction("open_app_setting");
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        }, 1000);
    }
    public void sendMessage(Message message) {
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
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
                                        updateConversation.put(Constants.KEY_TYPE_CALL,message.getTypeMessage());

                                        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                .document(snapshot.getId())
                                                .update(updateConversation)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                                                .document(snapshot.getId())
                                                                .collection(Constants.KEY_COLLECTION_MESSAGE)
                                                                .add(message);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(String.valueOf(this), "Failed when update last message for exists conversation: ", e);
                                                        Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
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
                                newConversation.setLastSender(message.getSenderNumberPhone());
                                newConversation.setTypeMessage(message.getTypeMessage());
                                FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_CHAT_MESSAGE)
                                        .add(newConversation)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentReference documentReference = task.getResult();
                                                    documentReference.collection(Constants.KEY_COLLECTION_MESSAGE)
                                                            .add(message);

                                                    currentUser.getConversationList().add(documentReference.getId());
                                                    UserService.getInstance(getApplicationContext()).updateCurrentUser(currentUser);
//
                                                    contactProfile.getConversationList().add(documentReference.getId());
                                                    UserService.getInstance(getApplicationContext()).updateConversationListForUser(contactProfile);


                                                } else {
                                                    Log.d(String.valueOf(getApplicationContext()), "Failed when create new conversation: ", task.getException());
                                                    Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                        else {
                            Log.d(String.valueOf(getApplicationContext()), "Failed when finding conversation: ", task.getException());
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void loadReceiverDetails() {
        receiverContact = (Contact) getIntent().getSerializableExtra(Constants.KEY_CONTACT);
        contactProfile = (User) getIntent().getSerializableExtra(Constants.KEY_CONTACT_PROFILE);

    }
}
