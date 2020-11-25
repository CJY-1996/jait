package com.example.jait.adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jait.FirebaseID;
import com.example.jait.R;
import com.example.jait.models.Message;
import com.example.jait.utils.SCUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private ArrayList<Message> data;
    private Context mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference mDocRef = mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid());

    public ChatAdapter(Context context, ArrayList<Message> data) {
        this.data = data;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_main, null);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        final Message message = data.get(i);
        String formatted_date = SCUtils.formatted_date(message.getTimestamp());
        if (message.isNotification()) {
            myViewHolder.textView_message.setText(Html.fromHtml("<small><i><font color=\"#FFBB33\">" + " " + message.getMessage() + "</font></i></small>"));
        } else {
            myViewHolder.textView_name.setText(
                    (message.getUsername()));
            myViewHolder.textView_message.setText(
                    (" " + message.getMessage()));
            myViewHolder.textView_timestamp.setText(formatted_date);

            mStore.collection(FirebaseID.user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (message.getUserID().equals(document.get(FirebaseID.nickname))) {
                               // myViewHolder.imageView_chat.setImageURI(Uri.parse(document.get(FirebaseID.profileUri).toString()));

                                String imagename = document.get(FirebaseID.nickname) + ".png";
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://jait-2c9d0.appspot.com/images/").child(imagename);
                                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(mContext.getApplicationContext()).load(uri).into(myViewHolder.imageView_chat);
                                        myViewHolder.imageView_chat.setBackground(new ShapeDrawable(new OvalShape()));
                                        myViewHolder.imageView_chat.setClipToOutline(true);
                                    }
                                });
                            }
                        }
                    }
                }
            });
            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (message.getUserID().equals(document.get(FirebaseID.nickname))) {
                                myViewHolder.linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                myViewHolder.linearLayout.setGravity(5);
                            } else {
                                myViewHolder.linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                                myViewHolder.linearLayout.setGravity(3);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView_chat;
        protected TextView textView_name;
        protected TextView textView_message;
        protected TextView textView_timestamp;
        protected LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            this.imageView_chat = (ImageView) view.findViewById(R.id.image_chat);
            this.textView_name = (TextView) view.findViewById(R.id.textView_name);
            this.textView_message = (TextView) view.findViewById(R.id.textView_message);
            this.textView_timestamp = (TextView) view.findViewById(R.id.text_timestamp);
            this.linearLayout = (LinearLayout) view.findViewById(R.id.chat_linear);
        }
    }
}

