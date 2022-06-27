package hcmute.edu.vn.zalo_05.MainFragments.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
//import hcmute.edu.vn.zalo_05.Common;
import hcmute.edu.vn.zalo_05.Common;
import hcmute.edu.vn.zalo_05.Event.ContactListener;
import hcmute.edu.vn.zalo_05.Event.ItemClickListener;
//import hcmute.edu.vn.zalo_05.Models.Contact;
//import hcmute.edu.vn.zalo_05.OutgoingCall2Activity;
//import hcmute.edu.vn.zalo_05.OutgoingCallActivity;
import hcmute.edu.vn.zalo_05.MainFragments.ContactsFragment;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.OutgoingCall2Activity;
import hcmute.edu.vn.zalo_05.OutgoingCallActivity;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Utilities.CommonUtils;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utils;
//import hcmute.edu.vn.zalo_05.Utils;
//import hcmute.edu.vn.zalo_05.Variable.GlobalVariable;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final String TAG = ContactAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    List<Contact> contactArrayList;
    List<User> userList;
//    GlobalVariable globalVariable= new GlobalVariable();
    private ActivityResultLauncher<Intent> launcher;
    private ContactListener contactListener;
    private final FragmentActivity fragmentActivity;
    private ContactsFragment contactsFragment;

    @SuppressLint("NotifyDataSetChanged")
    public ContactAdapter(List<Contact> contactArrayList, List<User> userList, FragmentActivity fragmentActivity, ContactListener contactListener,ContactsFragment contactsFragment) {
        this.contactArrayList = contactArrayList;
        this.userList = userList;
        this.fragmentActivity = fragmentActivity;
        this.contactListener = contactListener;
        this.contactsFragment= contactsFragment;
        notifyDataSetChanged();
        //sortContactList();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title_contact_by_alphabet, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_recyclerview, parent, false);
            return new ContactViewHolder(view);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Contact contact= contactArrayList.get(position);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                .document(contact.getNumberPhone())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Toast.makeText(fragmentActivity, "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();
                            Log.d(String.valueOf(fragmentActivity), error.getMessage());
                            return;
                        }
                        if(value != null && value.exists()) {
//                            map value to User object
                            User contactProfile = value.toObject(User.class);
                            userList.add(contactProfile);

                            if(holder instanceof HeaderViewHolder) {
                                String alphabet = String.valueOf(CommonUtils.removeAccent(contact.getContactName().toString()).charAt(0));
                                ((HeaderViewHolder) holder).tvHeaderAlphabet.setText(alphabet);
                                ((HeaderViewHolder) holder).tvContactName.setText(contact.getContactName());
                                if(contactProfile.getAvatarImageUrl() != null && contactProfile.getAvatarImageUrl() != "") {
                                    Picasso.get().load(contactProfile.getAvatarImageUrl()).into(((HeaderViewHolder) holder).civContactImage);
                                }
                                if(contactProfile.isOnlineStatus()) {
                                    ((HeaderViewHolder) holder).civStatus.setVisibility(View.VISIBLE);
                                } else {
                                    ((HeaderViewHolder) holder).civStatus.setVisibility(View.GONE);
                                }
                                ((HeaderViewHolder) holder).contactItemLayout.setOnClickListener(v -> contactListener.onContactClicked(contact, contactProfile));

                                ((HeaderViewHolder) holder).tvCall.setOnClickListener(view -> makeCall(false, false, contact,contactProfile));
                                ((HeaderViewHolder) holder).tvVideoCall.setOnClickListener(view -> makeCall(false, true, contact,contactProfile));

                            } else if (holder instanceof ContactViewHolder) {
                                ((ContactViewHolder) holder).tvContactName.setText(contact.getContactName());
                                if(contactProfile.getAvatarImageUrl() != null && contactProfile.getAvatarImageUrl() != "") {
                                    Picasso.get().load(contactProfile.getAvatarImageUrl()).into(((ContactViewHolder) holder).civContactImage);
                                }
                                if(contactProfile.isOnlineStatus()) {
                                    ((ContactViewHolder) holder).civStatus.setVisibility(View.VISIBLE);
                                } else {
                                    ((ContactViewHolder) holder).civStatus.setVisibility(View.GONE);
                                }

                                ((ContactViewHolder) holder).tvCall.setOnClickListener(view -> makeCall(false,false, contact,contactProfile));
                                ((ContactViewHolder) holder).tvVideoCall.setOnClickListener(view -> makeCall(false,true, contact,contactProfile));

                            }

                        }
                    }
                });

    }
    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return TYPE_HEADER;
        else
        {
//            String a = String.valueOf(contactArrayList.get(position).getZaloName().charAt(0));
//            String b = String.valueOf(contactArrayList.get(position-1).getZaloName().charAt(0));
//            if (a.compareToIgnoreCase(b) == 0) {
//                return TYPE_ITEM;
//            }
//            else return TYPE_HEADER;

            String a = String.valueOf(contactArrayList.get(position).getContactName().charAt(0));
            String b = String.valueOf(contactArrayList.get(position-1).getContactName().charAt(0));
            if (a.compareToIgnoreCase(b) == 0) {
                return TYPE_ITEM;
            }
            else return TYPE_HEADER;
        }
/*
        if(position<contactArrayList.size()-1) {
            String a = contactArrayList.get(position).getZaloName();
            String b = contactArrayList.get(position + 1).getZaloName();
            if (a.compareToIgnoreCase(b) < 0) {
                return TYPE_HEADER;
            }
            else return TYPE_ITEM;
        }
        else if(position<contactArrayList.size()){
            return TYPE_ITEM;
        }
        else {
            return TYPE_ITEM;
        }*/
        //return TYPE_HEADER;
    }
        /*if(position<contactArrayList.size()) {

            String a = contactArrayList.get(position).getZaloName();

            String b = contactArrayList.get(position + 1).getZaloName();
            if (a.compareToIgnoreCase(b) < 0) {
                return TYPE_HEADER;
            }
        }
        else if(position==contactArrayList.size()){
            return TYPE_ITEM;
        }
        return TYPE_ITEM;*//*
        *//*if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;*//*
    }*/


    private void sortContactList() {
        /*ArrayList<String> contactName= new;
        for (int i = 0; i < contactArrayList.size(); i++) {
            contactName.add(contactArrayList.get(i).getZaloName());
        }*/
        Comparator<Contact> compareByName = (Contact o1, Contact o2) ->
                o1.getContactName().compareToIgnoreCase( o2.getContactName() );
        if (contactArrayList.size() > 0) {
            Collections.sort(contactArrayList,compareByName);
        }

    }
    /*@Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.tvContactName.setText(contactArrayList.get(position).getZaloName());
        Picasso.get().load(contactArrayList.get(position).getImage()).into(holder.civContactImage);
    }*/


    @Override
    public int getItemCount() {
        return contactArrayList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvContactName;
        public CircleImageView civContactImage;
        public CircleImageView civStatus;
        public TextView tvCall;
        public TextView tvVideoCall;
        public ItemClickListener itemClickListener;




        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_contact_name);
            civContactImage = itemView.findViewById(R.id.civ_contact_image);
            civStatus = itemView.findViewById(R.id.civ_status);
            tvCall=itemView.findViewById(R.id.tv_call);
            tvVideoCall=itemView.findViewById(R.id.tv_video_call);


            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAbsoluteAdapterPosition(), false);
        }
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvHeaderAlphabet;
        public TextView tvContactName;
        public CircleImageView civContactImage;
        public CircleImageView civStatus;
        public TextView tvCall;
        public TextView tvVideoCall;
        public ItemClickListener itemClickListener;

        public LinearLayout contactItemLayout;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            contactItemLayout = itemView.findViewById(R.id.contact_item);
            tvHeaderAlphabet=itemView.findViewById(R.id.tv_alphabet);
            tvContactName = itemView.findViewById(R.id.tv_contact_name_1);
            civContactImage = itemView.findViewById(R.id.civ_contact_image_1);
            civStatus = itemView.findViewById(R.id.civ_status_1);
            tvCall=itemView.findViewById(R.id.tv_call_1);
            tvVideoCall=itemView.findViewById(R.id.tv_video_call_1);
            tvCall.setOnClickListener(this);
            tvVideoCall.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAbsoluteAdapterPosition(), false);

        }
    }

    private void makeCall(boolean isStringeeCall, boolean isVideoCall, Contact contact, User contactProfile ) {
        //*to = etTo.getText().toString();
        //if (to.trim().length() > 0) {
        if (Common.client.isConnected()) {
            Intent intent;
            if (isStringeeCall) {
                intent = new Intent(contactsFragment.getActivity(), OutgoingCallActivity.class);
            } else {
                intent = new Intent(contactsFragment.getActivity(), OutgoingCall2Activity.class);
            }
            intent.putExtra("from", Common.client.getUserId());
            intent.putExtra("to", contact.getNumberPhone());
            intent.putExtra("is_video_call", isVideoCall);
            intent.putExtra(Constants.KEY_CONTACT,contact);
            intent.putExtra(Constants.KEY_CONTACT_PROFILE,contactProfile);

//            ContactsFragment contactsFragment= new ContactsFragment();
            contactsFragment.launcher.launch(intent);
//            ContactsFragment.launch(intent);

        } else {
            Utils.reportMessage(fragmentActivity, "Stringee session not connected");
        }
   }

}

