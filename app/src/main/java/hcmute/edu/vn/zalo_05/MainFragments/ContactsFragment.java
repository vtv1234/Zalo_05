package hcmute.edu.vn.zalo_05.MainFragments;

//import static hcmute.edu.vn.zalo_05.GenerateToken.GenerateAccessToken.genAccessToken;

import static hcmute.edu.vn.zalo_05.GenerateToken.GenerateAccessToken.genAccessToken;

import android.content.Context;

import hcmute.edu.vn.zalo_05.Chat.ChatActivity;
import hcmute.edu.vn.zalo_05.Common;
import hcmute.edu.vn.zalo_05.IncomingCall2Activity;
import hcmute.edu.vn.zalo_05.IncomingCallActivity;
import hcmute.edu.vn.zalo_05.MainFragments.Adapter.ContactAdapter;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.OutgoingCall2Activity;
import hcmute.edu.vn.zalo_05.OutgoingCallActivity;
import hcmute.edu.vn.zalo_05.Utils;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hcmute.edu.vn.zalo_05.AddContactActivity;
//import hcmute.edu.vn.zalo_05.Common;
//import hcmute.edu.vn.zalo_05.IncomingCall2Activity;

import hcmute.edu.vn.zalo_05.Event.ContactListener;

//import hcmute.edu.vn.zalo_05.OutgoingCall2Activity;
//import hcmute.edu.vn.zalo_05.OutgoingCallActivity;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
//import hcmute.edu.vn.zalo_05.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener, ContactListener {

    View view;
    List<Contact> listContact= new ArrayList<>();
    List<User> listUsers = new ArrayList<>();
    private ProgressDialog progressDialog;
    public ActivityResultLauncher<Intent> launcher;
    private ContactAdapter adapter;
    private String token = genAccessToken("SKBe3kjKlx9EbIhbgmEBciRKFshrB0QMR","NDZzYXJ4d1hKRmhWMmJzYmdicXh5eDFlRUFEZ3FJRmg=",1728000, UserService.getInstance(getActivity()).getCurrentUser().getNumberPhone());



    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(String userId) {
        ContactsFragment fragment = new ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contacts, container, false);
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        progressDialog = ProgressDialog.show(this.getActivity(), "", "Connecting...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        // register data call back
        /*launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED)
                        if (result.getData() != null) {
                            if (result.getData().getAction() != null && result.getData().getAction().equals("open_app_setting")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                                builder.setTitle("Zalo");
                                builder.setMessage("Permissions must be granted for the call");
                                builder.setPositiveButton("Ok", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                });
                                builder.setNegativeButton("Settings", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                    // open app setting
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", this.getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                                builder.create().show();
                            }
                        }
                });*/
        getLauncher();

        initAndConnectStringee();






        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.layout_contact_in_device);
        linearLayout.setOnClickListener(view -> {
            OpenAddContactActivity(view);
        });

//        listContact = getListContact();
//        listUsers = UserService.getInstance(getActivity()).getUserInContacts(listContact);

        RecyclerView recyclerView = view.findViewById(R.id.rcv_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
//         ContactAdapter adapter = new ContactAdapter(listContact, this.getActivity(), this);
        adapter = new ContactAdapter(listContact, listUsers, this.getActivity(), this,this);
        recyclerView.setAdapter(adapter);

//        Listener Change Event Data From Document Contact In FireStore
        EventChangeContactsListener();
        //Toast.makeText(this.getActivity(),listContact.get(1).getImage(),Toast.LENGTH_SHORT).show();
        return view;

    }

    private void getLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED)
                        if (result.getData() != null) {
                            if (result.getData().getAction() != null && result.getData().getAction().equals("open_app_setting")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                                builder.setTitle("Zalo");
                                builder.setMessage("Permissions must be granted for the call");
                                builder.setPositiveButton("Ok", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                });
                                builder.setNegativeButton("Settings", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                    // open app setting
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", this.getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                                builder.create().show();
                            }
                        }
                });
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rcv_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        ContactAdapter adapter = new ContactAdapter(listContact,listUsers, this.getActivity(),this,this);
        recyclerView.setAdapter(adapter);
    }

    // Listener Change Event Data From Document Contact In FireStore
    private void EventChangeContactsListener() {
        User currentUser = UserService.getInstance(getActivity()).getCurrentUser();
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                .document(currentUser.getNumberPhone())
                .collection(Constants.KEY_COLLECTION_CONTACTS)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Toast.makeText(getActivity(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            Log.d(String.valueOf(getActivity()), error.getMessage());
                            return;
                        }
//                        iterate through documents from collection Contacts by path
//                                exam: 0703179825/Contacts/$document-id/data -> data to be retrieved

                        for(DocumentChange dc: value.getDocumentChanges()) {
                            if(dc.getType() == DocumentChange.Type.ADDED) {
//                                For each document we get, map it to Contact object
                                Contact contact = dc.getDocument().toObject(Contact.class);
                                Log.d(String.valueOf(ContactsFragment.this), "documentChange.Type.ADDED: " + contact.toString());
//                                add to listContact
                                listContact.add(contact);
                                adapter.notifyDataSetChanged();


                            }
                        }
                    }
                });
    }

//    private List<Contact> getListContact(){
//        List<Contact> list = new ArrayList<>();
//        list.add(new Contact(1,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Ba","Vo Tan Mung",true));
//        list.add(new Contact(2,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Dì Hường","Ha Thi Thu Huong",true));
//        list.add(new Contact(3,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Khang Lồn","Huỳnh Hồng Khang",false));
//        list.add(new Contact(4,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Mẹ","Hà Thị Thu Hằng",true));
//        list.add(new Contact(5,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Quang", "Trần Quang",true));
//        list.add(new Contact(6,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Văn Trường","Văn Trường",true));
//        list.add(new Contact(7,"https://trivietphat.net/wp-content/uploads/2021/08/bun-bo-1.jpg", "Vinh Wibu","Quang Vinh",true));
//        list = UserService.getInstance(getActivity()).getListContact();
//
//        if(list.isEmpty()) {
//            Toast.makeText(getActivity(), "Data null", Toast.LENGTH_SHORT).show();
//        }
//        //Collections.sort(list);
//        Comparator<Contact> compareByName = (Contact o1, Contact o2) ->
//                o1.getContactName().compareToIgnoreCase( o2.getContactName() );
//        if (list.size() > 0) {
//            Collections.sort(list,compareByName);
//        }
//        return list;
//    }

    public void OpenAddContactActivity(View view) {
        Intent intent = new Intent(this.getActivity(), AddContactActivity.class);
        startActivity(intent);
    }
    public void initAndConnectStringee() {
        if (Common.client == null) {
            Common.client = new StringeeClient(this.getActivity());
            // Set host
//            List<SocketAddress> socketAddressList = new ArrayList<>();
//            socketAddressList.add(new SocketAddress("YOUR_IP", YOUR_PORT));
//            client.setHost(socketAddressList);
            Common.client.setConnectionListener(new StringeeConnectionListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onConnectionConnected(final StringeeClient stringeeClient, boolean isReconnecting) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onConnectionConnected");
                        progressDialog.dismiss();
                        //tvUserId.setText("Connected as: " + stringeeClient.getUserId());
                        Utils.reportMessage(getActivity(), "StringeeClient connected as " + stringeeClient.getUserId());
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onConnectionDisconnected(StringeeClient stringeeClient, boolean isReconnecting) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onConnectionDisconnected");
                        progressDialog.dismiss();
                        //tvUserId.setText("Disconnected");
                        Utils.reportMessage(getActivity(), "StringeeClient disconnected.");
                    });
                }

                @Override
                public void onIncomingCall(final StringeeCall stringeeCall) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onIncomingCall: callId - " + stringeeCall.getCallId());
                        if (Common.isInCall) {
                            stringeeCall.reject();
                        } else {
                            Common.callsMap.put(stringeeCall.getCallId(), stringeeCall);
                            Intent intent = new Intent(getActivity(), IncomingCallActivity.class);
                            intent.putExtra("call_id", stringeeCall.getCallId());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onIncomingCall2: callId - " + stringeeCall2.getCallId());
                        if (Common.isInCall) {
                            stringeeCall2.reject();
                        } else {
                            Common.calls2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                            Intent intent = new Intent(getActivity(), IncomingCall2Activity.class);
                            intent.putExtra("call_id", stringeeCall2.getCallId());
                            startActivity(intent);
                        }
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onConnectionError(StringeeClient stringeeClient, final StringeeError stringeeError) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onConnectionError: " + stringeeError.getMessage());
                        progressDialog.dismiss();
                        //tvUserId.setText("Connect error: " + stringeeError.getMessage());
                        Utils.reportMessage(getActivity(), "StringeeClient fails to connect: " + stringeeError.getMessage());
                    });
                }

                @Override
                public void onRequestNewToken(StringeeClient stringeeClient) {
                    getActivity().runOnUiThread(() -> {
                        Log.d(Common.TAG, "onRequestNewToken");
                        Common.client.connect(token);
                    });
                    // Get new token here and connect to Stringee server
                }

                @Override
                public void onCustomMessage(String from, JSONObject msg) {
                    getActivity().runOnUiThread(() -> Log.d(Common.TAG, "onCustomMessage: from - " + from + " - msg - " + msg));
                }

                @Override
                public void onTopicMessage(String from, JSONObject msg) {

                }
            });
        }
        Common.client.connect(token);
    }


    @Override
    public void onClick(View view) {
       /* switch (view.getId()) {
            case id.btn_voice_call:
                makeCall(true, false);
                break;
            case id.btn_video_call:
                makeCall(true, true);
                break;
            case id.btn_voice_call2:
                makeCall(false, false);
                break;
            case id.btn_video_call2:
                makeCall(false, true);
                break;
        }
*/
    }
  
//    private void makeCall(boolean isStringeeCall, boolean isVideoCall) {
//        /*to = etTo.getText().toString();
//        if (to.trim().length() > 0) {*/
//        if (Common.client.isConnected()) {
//            Intent intent;
//            if (isStringeeCall) {
//                intent = new Intent(this.getActivity(), OutgoingCallActivity.class);
//            } else {
//                intent = new Intent(this.getActivity(), OutgoingCall2Activity.class);
//            }
//            intent.putExtra("from", Common.client.getUserId());
//            intent.putExtra("to", token);
//            intent.putExtra("is_video_call", isVideoCall);
//            launcher.launch(intent);
//        } else {
//            Utils.reportMessage(this.getActivity(), "Stringee session not connected");
//        }
//    }

    @Override
    public void onContactClicked(Contact contact, User contactProfile) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_CONTACT, contact);
        intent.putExtra(Constants.KEY_CONTACT_PROFILE, contactProfile);
        startActivity(intent);
    }

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
    }
    public ActivityResultLauncher<Intent> getLauncher(){
        return launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_CANCELED)
                        if (result.getData() != null) {
                            if (result.getData().getAction() != null && result.getData().getAction().equals("open_app_setting")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                                builder.setTitle("Zalo");
                                builder.setMessage("Permissions must be granted for the call");
                                builder.setPositiveButton("Ok", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                });
                                builder.setNegativeButton("Settings", (dialogInterface, id) -> {
                                    dialogInterface.cancel();
                                    // open app setting
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", this.getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                                builder.create().show();
                            }
                        }
                });
    }*/

}
