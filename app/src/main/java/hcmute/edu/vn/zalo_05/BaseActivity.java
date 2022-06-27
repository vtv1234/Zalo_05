package hcmute.edu.vn.zalo_05;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;

public class BaseActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private DocumentReference documentReference;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = UserService.getInstance(getApplicationContext()).getCurrentUser();
        database = FirebaseFirestore.getInstance();

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUser.getNumberPhone());

    }

    @Override
    protected void onPause() {
        super.onPause();
        Map<String, Object> updateStatusOnline = new HashMap<>();
        updateStatusOnline.put(Constants.KEY_LAST_ONLINE, new Date());
        updateStatusOnline.put(Constants.KEY_ONLINE_STATUS, false);
        documentReference.update(updateStatusOnline)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(String.valueOf(getApplicationContext()),
                                String.format("Online status: %s, recent online: %s",
                                        updateStatusOnline.get(Constants.KEY_LAST_ONLINE).toString(),
                                        updateStatusOnline.get(Constants.KEY_ONLINE_STATUS).toString()));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Map<String, Object> updateStatusOnline = new HashMap<>();
        updateStatusOnline.put(Constants.KEY_LAST_ONLINE, new Date());
        updateStatusOnline.put(Constants.KEY_ONLINE_STATUS, true);
        documentReference.update(updateStatusOnline)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(String.valueOf(getApplicationContext()),
                                String.format("Online status: %s, recent online: %s",
                                        updateStatusOnline.get(Constants.KEY_LAST_ONLINE).toString(),
                                        updateStatusOnline.get(Constants.KEY_ONLINE_STATUS).toString()));
                    }
                });
    }
}
