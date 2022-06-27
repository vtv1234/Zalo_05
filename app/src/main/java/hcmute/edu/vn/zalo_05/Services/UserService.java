package hcmute.edu.vn.zalo_05.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;

public class UserService {
    private static UserService instance;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    private static Context context;


    private UserService() {
        this.database = FirebaseFirestore.getInstance();
        this.preferenceManager = new PreferenceManager(context);
    }

    public static UserService getInstance(Context context) {
        instance.context = context;
        if(instance == null) {
            instance = new UserService();
        }

        return instance;
    }

    public List<Contact> getListContact() {
        List<Contact> contactList = new ArrayList<>();
        User currentUser = getCurrentUser();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUser.getNumberPhone())
                .collection(Constants.KEY_COLLECTION_CONTACTS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            for(DocumentChange dc: querySnapshot.getDocumentChanges()) {
                                if(dc.getType() == DocumentChange.Type.ADDED) {
                                    Log.d(String.valueOf(context), ((Contact)dc.getDocument().toObject(Contact.class)).toString());
                                    contactList.add(dc.getDocument().toObject(Contact.class));
                                }
                            }
                        }
                    }
                });
//        Log.d(String.valueOf(context), String.valueOf(contactList.size()));
        for(Contact contact: contactList) {
            Log.d(String.valueOf(context), String.valueOf(contactList.size()));
        }
        return contactList;
    }

    public List<User> getUserInContacts(List<Contact> contacts) {
        List<User> userList = new ArrayList<>();
        for(Contact contact: contacts) {
            String numberPhone = contact.getNumberPhone();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(numberPhone).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()) {
                                    userList.add(documentSnapshot.toObject(User.class));
                                }
                            }
                            else {
                                Log.d(String.valueOf(context), "Failed with: ", task.getException());
                            }
                        }
                    });
        }
        return  userList;
    }

    public User getCurrentUser() {
        Gson gson = new Gson();
        String jsonToObject = this.preferenceManager.getString(Constants.KEY_CURRENT_USER);
        User currentUser = gson.fromJson(jsonToObject, User.class);
        Log.d(String.valueOf(context), currentUser.toString());
        return currentUser;
    }

    public void updateCurrentUser(User user) {
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put(Constants.KEY_NUMBER_PHONE, user.getNumberPhone());
        updateUser.put(Constants.KEY_USER_NAME, user.getUserName());
        updateUser.put(Constants.KEY_BIRTH_DATE, user.getBirthdate());
        updateUser.put(Constants.KEY_CONVERSATION_LIST, user.getConversationList());
        updateUser.put(Constants.KEY_ONLINE_STATUS, user.isOnlineStatus());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.getNumberPhone())
                .update(updateUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Gson gson = new Gson();
                            String objectToJson = gson.toJson(user);
                            preferenceManager.putString(Constants.KEY_CURRENT_USER, objectToJson);
                        }
                        else {
                            Log.d(String.valueOf(context), "Failed when updateCurrentUser in UserService: ", task.getException());
                            Toast.makeText(context, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateConversationListForUser(User user) {
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put(Constants.KEY_CONVERSATION_LIST, user.getConversationList());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.getNumberPhone())
                .update(updateUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Log.d(String.valueOf(context), "Failed when updateConversationListForUser in UserService: ", task.getException());
                            Toast.makeText(context, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void asyncFromContact(Contact contact) {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(getCurrentUser().getNumberPhone())
                .collection(Constants.KEY_COLLECTION_CONTACTS)
                .add(contact);
    }

}
