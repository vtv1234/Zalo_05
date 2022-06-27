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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_05.R.id;
import hcmute.edu.vn.zalo_05.R.drawable;
import hcmute.edu.vn.zalo_05.R.layout;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout vLocal;
    private FrameLayout vRemote;
    private TextView tvState;
    private ImageButton btnEnd;
    private ImageButton btnMute;
    private ImageButton btnSpeaker;
    private ImageButton btnVideo;
    private ImageButton btnSwitch;
    private View vControl;
    private View vIncoming;

    private StringeeCall stringeeCall;
    private SensorManagerUtils sensorManagerUtils;
    private StringeeAudioManager audioManager;
    private boolean isMute = false;
    private boolean isSpeaker = false;
    private boolean isVideo = false;
    private boolean isPermissionGranted = true;

    private StringeeCall.MediaState mMediaState;
    private StringeeCall.SignalingState mSignalingState;
    LocalDateTime endDate;
    LocalDateTime startDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add Flag for show on lockScreen, disable keyguard, keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        setContentView(layout.activity_incoming_call);

        sensorManagerUtils = SensorManagerUtils.getInstance(this);
        sensorManagerUtils.acquireProximitySensor(getLocalClassName());
        sensorManagerUtils.disableKeyguard();

        Common.isInCall = true;

        String callId = getIntent().getStringExtra("call_id");
        stringeeCall = Common.callsMap.get(callId);
        if (stringeeCall == null) {
            sensorManagerUtils.releaseSensor();
            Utils.postDelay(() -> {
                Common.isInCall = false;
                finish();
            }, 1000);
            return;
        }

        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> lstPermissions = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                lstPermissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (stringeeCall.isVideoCall()) {
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

        startRinging();
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
                endCall(false);
            } else {
                isPermissionGranted = true;
                startRinging();
            }
        }
    }

    private void initView() {
        vLocal = findViewById(id.v_local);
        vRemote = findViewById(id.v_remote);

        vControl = findViewById(id.v_control);
        vIncoming = findViewById(id.v_incoming);

        TextView tvFrom = findViewById(id.tv_from);
        tvFrom.setText(stringeeCall.getFrom());
        tvState = findViewById(id.tv_state);

        ImageButton btnAnswer = findViewById(id.btn_answer);
        btnAnswer.setOnClickListener(this);
        btnEnd = findViewById(id.btn_end);
        btnEnd.setOnClickListener(this);
        ImageButton btnReject = findViewById(id.btn_reject);
        btnReject.setOnClickListener(this);
        btnMute = findViewById(id.btn_mute);
        btnMute.setOnClickListener(this);
        btnSpeaker = findViewById(id.btn_speaker);
        btnSpeaker.setOnClickListener(this);
        btnVideo = findViewById(id.btn_video);
        btnVideo.setOnClickListener(this);
        btnSwitch = findViewById(id.btn_switch);
        btnSwitch.setOnClickListener(this);

        isSpeaker = stringeeCall.isVideoCall();
        btnSpeaker.setBackgroundResource(isSpeaker ? drawable.btn_speaker_on : drawable.btn_speaker_off);

        isVideo = stringeeCall.isVideoCall();
        btnVideo.setImageResource(isVideo ? drawable.btn_video : drawable.btn_video_off);

        btnVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        btnSwitch.setVisibility(isVideo ? View.VISIBLE : View.GONE);
    }

    private void startRinging() {
        //create audio manager to control audio device
        audioManager = StringeeAudioManager.create(IncomingCallActivity.this);
        audioManager.start((selectedAudioDevice, availableAudioDevices) ->
                Log.d(Common.TAG, "selectedAudioDevice: " + selectedAudioDevice + " - availableAudioDevices: " + availableAudioDevices));
        audioManager.setSpeakerphoneOn(isVideo);

        stringeeCall.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, final StringeeCall.SignalingState signalingState, String reason, int sipCode, String sipReason) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onSignalingStateChange: " + signalingState);
                    mSignalingState = signalingState;
                    if (signalingState == StringeeCall.SignalingState.ANSWERED) {
                        tvState.setText("Starting");
                        if (mMediaState == StringeeCall.MediaState.CONNECTED) {
                            tvState.setText("Started");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startDate=LocalDateTime.now();
                            }
                            Log.e("start",startDate.toString());
                        }
                    } else if (signalingState == StringeeCall.SignalingState.ENDED) {
                        endCall(true);
                    }
                });
            }

            @Override
            public void onError(StringeeCall stringeeCall, int code, String desc) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onError: " + desc);
                    Utils.reportMessage(IncomingCallActivity.this, desc);
                    tvState.setText("Ended");
                    dismissLayout();
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, final StringeeCall.SignalingState signalingState, String desc) {
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "onHandledOnAnotherDevice: " + desc);
                    if (signalingState != StringeeCall.SignalingState.RINGING) {
                        Utils.reportMessage(IncomingCallActivity.this, desc);
                        tvState.setText("Ended");
                        dismissLayout();
                    }
                });
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
                    } else {
                        tvState.setText("Reconnecting...");
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

        stringeeCall.ringing(new StatusListener() {
            @Override
            public void onSuccess() {
                Log.d("Stringee", "ringing success");
            }

            @Override
            public void onError(StringeeError stringeeError) {
                super.onError(stringeeError);
                runOnUiThread(() -> {
                    Log.d(Common.TAG, "ringing error: " + stringeeError.getMessage());
                    Utils.reportMessage(IncomingCallActivity.this, stringeeError.getMessage());
                    endCall(false);
                });
            }
        });
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
            case id.btn_answer:
                if (stringeeCall != null) {
                    vControl.setVisibility(View.VISIBLE);
                    vIncoming.setVisibility(View.GONE);
                    btnEnd.setVisibility(View.VISIBLE);
                    btnSwitch.setVisibility(stringeeCall.isVideoCall() ? View.VISIBLE : View.GONE);
                    stringeeCall.answer();
                }
                break;
            case id.btn_end:
                endCall(true);
                break;
            case id.btn_reject:
                endCall(false);
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
                                Utils.reportMessage(IncomingCallActivity.this, stringeeError.getMessage());
                            });
                        }
                    });
                }
                break;
        }
    }

    private void endCall(boolean isHangup) {
        tvState.setText("Ended");
        if (stringeeCall != null) {
            if (isHangup) {
                stringeeCall.hangup();
            } else {
                stringeeCall.reject();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            endDate=LocalDateTime.now();
        }
        Log.e("end2",endDate.toString());
        dismissLayout();
    }

    private void dismissLayout() {
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        vControl.setVisibility(View.GONE);
        vIncoming.setVisibility(View.GONE);
        btnEnd.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);
        sensorManagerUtils.releaseSensor();
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
}
