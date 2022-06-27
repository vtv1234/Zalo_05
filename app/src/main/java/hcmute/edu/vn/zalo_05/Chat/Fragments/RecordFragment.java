package hcmute.edu.vn.zalo_05.Chat.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import hcmute.edu.vn.zalo_05.Event.RecordResultListener;
import hcmute.edu.vn.zalo_05.Models.ChatMessage;
import hcmute.edu.vn.zalo_05.Models.Message;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    private RecordButton recordButton;
    private RecordView recordView;
    private static Boolean permissionRecordOk = false;
    private static Boolean permissionAccessStorageOk = false;
    private MediaRecorder mediaRecorder;
    private String audioPath;
    private String currentConversationId;
    private User currentUser;

    private RecordResultListener recordResultListener;

    public RecordFragment() {
        // Required empty public constructor
    }

    public RecordFragment(RecordResultListener recordResultListener) {
        this.recordResultListener = recordResultListener;
    }


    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        handlePermissionBeforeStart();
        currentUser = UserService.getInstance(getActivity()).getCurrentUser();
    }

//    private boolean checkRecordPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            }
//            else {
//                requestRecordPermission();
//                return false;
//            }
//        }
//        return false;
//    }

    private void handlePermissionBeforeStart() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[] {Manifest.permission.RECORD_AUDIO},
                        101
                );
            } else if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100
                );
            }else {
                permissionRecordOk = true;
                permissionAccessStorageOk = true;
            }
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void requestRecordPermission() {
//        String[] permissions = {Manifest.permission.RECORD_AUDIO};
//        requestPermissions(permissions, 101);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case 100:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccessStorageOk = true;
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    if(getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                    }
                }
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionRecordOk = true;
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                    if(getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                    }
                }

        }
    }

    private boolean isRecordingOk(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestRecording(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
    }

    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Zalo05/Media/Recording");
        if(!file.exists())
            file.mkdirs();
        audioPath = file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".3gp";
        mediaRecorder.setOutputFile(audioPath);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        recordButton = view.findViewById(R.id.fragment_record_record_button);
        recordView = view.findViewById(R.id.fragment_record_record_view);

        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(false);

        recordButton.setOnClickListener(v -> {
            if(permissionRecordOk && permissionAccessStorageOk) {
                recordButton.setListenForRecord(true);
            }
        });

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");
                setUpRecording();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if(file.exists())
                    file.delete();
                recordResultListener.onCancel();

            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                //limitReached to determine if the Record was finished when time limit reached.
                Log.d("RecordView", "onFinish");
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sendRecordingMessage(audioPath);
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if(file.exists())
                    file.delete();
                recordResultListener.onCancel();
            }
        });
        view.setVisibility(View.VISIBLE);
        return view;
    }

    private void sendRecordingMessage(String audioPath) {
        String storagePath = currentUser.getNumberPhone() +
                "/Media/Recording/" +
                System.currentTimeMillis();
        Uri audioFile = Uri.fromFile(new File(audioPath));

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference();

        storageReference.child(storagePath).putFile(audioFile)
                .addOnSuccessListener(success -> {
                    Task<Uri> audioUri = success.getStorage().getDownloadUrl();
                    audioUri.addOnCompleteListener(path -> {
                        if(path.isSuccessful()) {
                            String url = path.getResult().toString();

                            Message message = new Message();
                            message.setSenderNumberPhone(currentUser.getNumberPhone());
                            message.setTypeMessage(Constants.KEY_TYPE_RECORD);
                            message.setMessage(url);
                            message.setSendAt(new Date());
                            recordResultListener.getResult(message);

                        }
                    });
                });
    }
}