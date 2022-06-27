package hcmute.edu.vn.zalo_05;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_05.Adapter.AddContactAdapter;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

public class AddContactActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 10;

    private AddContactAdapter adapter;
    private MaterialToolbar toolbar;

    List<Contact> listContact = new ArrayList<>();
    List<User> existAccount = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        toolbar = findViewById(R.id.activity_add_contact_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.rcv_add_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new AddContactAdapter(listContact, existAccount, this);
        recyclerView.setAdapter(adapter);
        //Toast.makeText(this.getActivity(),listContact.get(1).getImage(),Toast.LENGTH_SHORT).show();
        checkPermissionBeforeGetContacts();
    }

    private void checkPermissionBeforeGetContacts() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
        ) {
            listenAsyncFromContact();

        } else {
            String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listenAsyncFromContact();
            } else {
                Toast.makeText(this, "Bạn phải cấp quyền cho phép truy cập vào danh bạ mói sử dụng được tính năng này", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private List<Contact> getContactList() {
        List<Contact> arrayList_Android_Contacts = new ArrayList<Contact>();

        //--< get all Contacts >--
        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }
        //--</ get all Contacts >--

        //----< Check: hasContacts >----
        if (cursor_Android_Contacts.getCount() > 0) {
        //----< has Contacts >----
        //----< @Loop: all Contacts >----
            while (cursor_Android_Contacts.moveToNext()) {
        //< init >
                Contact android_contact = new Contact();
                @SuppressLint("Range") String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String contact_display_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        //</ init >

        //----< set >----
                android_contact.setContactName(contact_display_name);


        //----< get phone number >----
                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);

                    while (phoneCursor.moveToNext()) {
                        @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        //< set >
                        android_contact.setNumberPhone(phoneNumber);
        //</ set >
                    }
                    phoneCursor.close();
                }
        //----</ set >----
        //----</ get phone number >----

        // Add the contact to the ArrayList
                arrayList_Android_Contacts.add(android_contact);
            }
        }
        return arrayList_Android_Contacts;
    }

    private void listenAsyncFromContact() {
        List<Contact> getFromLocalContacts = getContactList();

        for(Contact contact: getFromLocalContacts) {
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                    .document(contact.getNumberPhone())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists() && documentSnapshot != null) {
                                    User foundFromContact = documentSnapshot.toObject(User.class);
                                    if(!foundFromContact.getNumberPhone().equals(UserService.getInstance(getApplicationContext()).getCurrentUser().getNumberPhone())) {
                                        listContact.add(contact);
                                        existAccount.add(foundFromContact);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    });
//            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
//                    .document(contact.getNumberPhone())
//                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                            if(error != null) {
//                                Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
//                                Log.d(String.valueOf(AddContactActivity.this), "Failed when find user from list contacts in AddContactAdapter: " + error.getMessage());
//                                return;
//                            }
//                            if(value.exists() && value != null) {
//                                User foundFromContact = value.toObject(User.class);
//                                if(!foundFromContact.getNumberPhone().equals(UserService.getInstance(getApplicationContext()).getCurrentUser().getNumberPhone())) {
//                                    listContact.add(contact);
//                                    existAccount.add(foundFromContact);
//                                    adapter.notifyDataSetChanged();
//                                }
//
//                            }
//                        }
//                    });
        }
    }
}

        /*private List<Contact> getListContact () {
            List<Contact> list = new ArrayList<>();
            list.add(new Contact(1, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Ba", "Vo Tan Mung", true));
            list.add(new Contact(2, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Dì Hường", "Ha Thi Thu Huong", true));
            list.add(new Contact(3, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Khang Lồn", "Huỳnh Hồng Khang", false));
            list.add(new Contact(4, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Mẹ", "Hà Thị Thu Hằng", true));
            list.add(new Contact(5, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Quang", "Trần Quang", true));
            list.add(new Contact(6, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Văn Trường", "Văn Trường", true));
            list.add(new Contact(7, "https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Vinh Wibu", "Quang Vinh", true));
            //Collections.sort(list);
            Comparator<Contact> compareByName = (Contact o1, Contact o2) ->
                    o1.getContactName().compareToIgnoreCase(o2.getContactName());
            if (list.size() > 0) {
                Collections.sort(list, compareByName);
            }
            return list;
        }*/


    /*private void CheckPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},100);
        } else {
            getContactList();
        }
    }*/

    /*private List<Contact> getContactList() {
        List<Contact> list= new ArrayList<>();
        //Initialize uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Sort by ascending
        String sort =ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        //Initialize cursor
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
        //Check condition
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                //Cursor move to next
                //Get contact id
                @SuppressLint("Range") String id= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //Get contact name
                @SuppressLint("Range") String name= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //Initialize phone uri
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                //Initialize selection
                String selection= ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                //Initialize phone cursor
                Cursor phoneCursor=getContentResolver().query(uriPhone,null,selection,new String[]{id},null);
                //Check condition
                if(phoneCursor.moveToNext()){
                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Contact contact = new Contact();
                    contact.setContactName(name);
                    //contact.setNumber(number);
                    listContact.add(contact);
                    phoneCursor.close();
                }
                cursor.close();


            }
        }
        return list;
    }*/

