package com.example.jait;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jait.adapters.ChatAdapter;
import com.example.jait.models.Message;
import com.example.jait.utils.ProfanityFilter;
import com.example.jait.utils.SCUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final int ANTI_FLOOD_SECONDS = 3; //simple anti-flood
    private boolean IS_ADMIN = false; //set this to true for the admin app.
    private String username = ""; //default username
    private boolean PROFANITY_FILTER_ACTIVE = true;
    private FirebaseDatabase database;
    private RecyclerView main_recycler_view;
    private String userID;
    private ChatActivity mContext;
    private ChatAdapter adapter;
    private DatabaseReference databaseRef;
    private ImageButton imageButton_send;
    private EditText editText_message;
    ArrayList<Message> messageArrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private long last_message_timestamp = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String postnum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = ChatActivity.this;
        main_recycler_view = (RecyclerView) findViewById(R.id.main_recycler);
        imageButton_send = (ImageButton) findViewById(R.id.imageButton_send);
        editText_message = (EditText) findViewById(R.id.editText_message);
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        username = mAuth.getCurrentUser().getDisplayName();

        main_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(mContext, messageArrayList);
        main_recycler_view.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        postnum = extras.getString("postId");

        databaseRef.child("chatting").child(postnum).child("the_messages").limitToLast(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message new_message = dataSnapshot.getValue(Message.class);
                messageArrayList.add(new_message);
                adapter.notifyDataSetChanged();
                main_recycler_view.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("REMOVED", dataSnapshot.getValue(Message.class).toString());
                messageArrayList.remove(dataSnapshot.getValue(Message.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        imageButton_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                process_new_message(editText_message.getText().toString().trim(), false);
            }
        });

        editText_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND)) {
                    imageButton_send.performClick();
                }
                return false;
            }
        });

        logic_for_username();
    }

    private void process_new_message(String new_message, boolean isNotification) {
        if (new_message.isEmpty()) {
            return;
        }

        //simple anti-flood protection
        if ((System.currentTimeMillis() / 1000L - last_message_timestamp) < ANTI_FLOOD_SECONDS) {
            return;
        }

        //yes, admins can swear ;)
        if ((PROFANITY_FILTER_ACTIVE) && (!IS_ADMIN)) {
            new_message = new_message.replaceAll(ProfanityFilter.censorWords(ProfanityFilter.ENGLISH), ":)");
        }

        editText_message.setText("");

        Message xmessage = new Message(userID, username, new_message, System.currentTimeMillis() / 1000L, IS_ADMIN, isNotification);
        String key = databaseRef.child("chatting").child(postnum).child("the_messages").push().getKey();
        databaseRef.child("chatting").child(postnum).child("the_messages").child(key).setValue(xmessage);

        last_message_timestamp = System.currentTimeMillis() / 1000L;
    }

    private void logic_for_username() {
        userID = mAuth.getCurrentUser().getDisplayName().toString();
    }

    private void show_alert_username() {
        AlertDialog.Builder alertDialogUsername = new AlertDialog.Builder(mContext);
        alertDialogUsername.setMessage("Your username");
        final EditText input = new EditText(mContext);
        input.setText(username);
        alertDialogUsername.setView(input);

        alertDialogUsername.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                String new_username = input.getText().toString().trim();
                if ((!new_username.equals(username)) && (!username.equals("anonymous"))) {
                    process_new_message(username + " changed it's nickname to " + new_username, true);
                }
                username = new_username;
                databaseRef.child("users").child(userID).setValue(username);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialogUsername.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            show_alert_username();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private long time = 0;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatActivity.this, CRoomActivity.class));
        finish();
    }
}