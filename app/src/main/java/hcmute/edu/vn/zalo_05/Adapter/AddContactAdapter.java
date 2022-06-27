package hcmute.edu.vn.zalo_05.Adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_05.Event.ItemClickListener;
import hcmute.edu.vn.zalo_05.Models.Contact;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.CommonUtils;
import hcmute.edu.vn.zalo_05.Utilities.Constants;

public class AddContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = AddContactAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    List<Contact> addContactList;
    List<User> contactsAlreadyExist;
    Map<Integer, Boolean> checkOverlap;
    private final Context context;

    @SuppressLint("NotifyDataSetChanged")
    public AddContactAdapter(List<Contact> addContactList, Context context) {
        this.addContactList = addContactList;
        this.context = context;
        notifyDataSetChanged();
        //sortContactList();
    }

    @SuppressLint("NotifyDataSetChanged")
    public AddContactAdapter(List<Contact> addContactList, List<User> contactsAlreadyExist, Context context) {
        this.addContactList = addContactList;
        this.contactsAlreadyExist = contactsAlreadyExist;
        this.context = context;
        this.checkOverlap = new HashMap<>();
        notifyDataSetChanged();
        //sortContactList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_contact_with_alphabet_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_contact_recyclerview, parent, false);
            return new AddContactViewHolder(view);
        }
        throw new RuntimeException("No match for " + viewType + ".");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!checkOverlap.containsKey(position)) {
            checkOverlap.put(position, true);
//            Get contact from contact list
            Contact contact = addContactList.get(position);
            Log.d(String.valueOf(context), String.format("Get contact at position %s: %s", String.valueOf(position), contact.toString()));
            User contactProfile = contactsAlreadyExist.get(position);
            Log.d(String.valueOf(context), String.format("Get contactProfile at position %s: %s", String.valueOf(position), contactProfile.toString()));
//            Get current user data
            User currentUser = UserService.getInstance(context).getCurrentUser();

//            get the current contact's phone number, look in the current user's contact collections to see
//            if that phone number already exists
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
                    .document(currentUser.getNumberPhone())
                    .collection(Constants.KEY_COLLECTION_CONTACTS)
                    .whereEqualTo(Constants.KEY_NUMBER_PHONE, contact.getNumberPhone())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if(!querySnapshot.isEmpty() && querySnapshot != null) {
                                    for (DocumentChange snapshot: querySnapshot.getDocumentChanges()) {
                                        if(snapshot.getType() == DocumentChange.Type.ADDED) {

//                                            if data exists, map to contact object
                                            Contact existsContact = snapshot.getDocument().toObject(Contact.class);
                                            if(existsContact.getNumberPhone().equals(contact.getNumberPhone()) ) {
                                                Log.d(String.valueOf(context), "existsContact: " + existsContact.toString());
//                                                add data to view holder
                                                if(holder instanceof HeaderViewHolder) {
                                                    ((HeaderViewHolder) holder).setData(existsContact, contactProfile);
                                                } else if(holder instanceof AddContactViewHolder) {
                                                    ((AddContactViewHolder) holder).setData(existsContact, contactProfile);
                                                }
                                                break;
                                            }
                                        }


                                    }
                                }
                                else {
//                                    if contact data is not in the current user's contact list, add that contact to the list and update to firestore
                                    contact.setFriend(false);
                                    contact.setCreatedAt(new Date());
                                    Log.d(String.valueOf(context), "Not found exist contact, auto add to list contact in firestore: " + contact.toString());
                                    UserService.getInstance(context).asyncFromContact(contact);
//                                    after update, load data into viewholder

                                    if(holder instanceof HeaderViewHolder) {
                                        ((HeaderViewHolder) holder).setData(contact, contactProfile);
                                    } else if(holder instanceof AddContactViewHolder) {
                                        ((AddContactViewHolder) holder).setData(contact, contactProfile);
                                    }
                                }
                            }
                        }
                    });
        }

    }
    @Override
    public int getItemViewType(int position) {
        if(position==0)
            return TYPE_HEADER;
        else
        {
            String a = String.valueOf(addContactList.get(position).getContactName().charAt(0));
            String b = String.valueOf(addContactList.get(position-1).getContactName().charAt(0));
            if (a.compareToIgnoreCase(b) == 0) {
                return TYPE_ITEM;
            }
            else return TYPE_HEADER;
        }

    }

    @Override
    public int getItemCount() {
        return addContactList.size();
    }

    public class AddContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvContactName;
        public CircleImageView civContactImage;
        public TextView tvZaloName;
        public TextView tvisFriend;
        public TextView tvAddFriend;
        public ItemClickListener itemClickListener;


        public AddContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tv_add_contact_name);
            civContactImage = itemView.findViewById(R.id.civ_add_contact_image);
            tvZaloName = itemView.findViewById(R.id.tv_zalo_name);
            tvisFriend = itemView.findViewById(R.id.tv_is_friend);
            tvAddFriend = itemView.findViewById(R.id.tv_add_friend);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setData(Contact contact, User contactProfile) {
            tvContactName.setText(contact.getContactName());
            if(contactProfile.getAvatarImageUrl() != null || contactProfile.getAvatarImageUrl() != "") {
                Picasso.get().load(contactProfile.getAvatarImageUrl()).into(civContactImage);
            }
            tvZaloName.setText("Tên Zalo: " + contactProfile.getZaloName());

            if(contact.isFriend()) {
                tvisFriend.setVisibility(View.VISIBLE);
                tvAddFriend.setVisibility(View.GONE);

            }
            else {
                tvAddFriend.setVisibility(View.VISIBLE);
                tvisFriend.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAbsoluteAdapterPosition(), false);
        }
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        TextView tvHeaderAlphabet;
        public TextView tvContactName;
        public CircleImageView civContactImage;
        public TextView tvZaloName;
        public TextView tvisFriend;
        public TextView tvAddFriend;
        public ItemClickListener itemClickListener;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderAlphabet=itemView.findViewById(R.id.tv_add_contact_header_alphabet);
            tvContactName = itemView.findViewById(R.id.tv_add_contact_name_with_header);
            civContactImage = itemView.findViewById(R.id.civ_contact_image_with_header);
            tvZaloName = itemView.findViewById(R.id.tv_zalo_name_with_header);
            tvisFriend = itemView.findViewById(R.id.tv_is_friend_with_header);
            tvAddFriend = itemView.findViewById(R.id.tv_add_friend_with_header);
        }

        public void setData(Contact contact, User contactProfile) {
            String alphabet = String.valueOf(CommonUtils.removeAccent(String.valueOf(contact.getContactName())).charAt(0)).toUpperCase(Locale.ROOT);
            tvHeaderAlphabet.setText(alphabet);
            tvContactName.setText(contact.getContactName());
            if(contactProfile.getAvatarImageUrl() != null || contactProfile.getAvatarImageUrl() != "") {
                Picasso.get().load(contactProfile.getAvatarImageUrl()).into(civContactImage);
            }
            tvZaloName.setText("Tên Zalo: " + contactProfile.getZaloName());
            if(contact.isFriend()) {
                tvisFriend.setVisibility(View.VISIBLE);
                tvAddFriend.setVisibility(View.GONE);

            }
            else {
                tvAddFriend.setVisibility(View.VISIBLE);
                tvisFriend.setVisibility(View.GONE);
            }

        }
    }

}
