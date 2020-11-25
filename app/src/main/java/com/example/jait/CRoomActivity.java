package com.example.jait;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.jait.adapters.PostAdapter;
import com.example.jait.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CRoomActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private RecyclerView mPostRecyclerView;
    private DocumentReference mDocRef = mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid());
    private PostAdapter mAdapter;
    private List<Post> mDatas;
    private List<Post> mArrayList;
    private String[] temp = new String[1000000];
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_room);

        mPostRecyclerView = findViewById(R.id.croom_recyclerview);
        mArrayList = new ArrayList<>();

        mPostRecyclerView.addOnItemTouchListener(new CRoomActivity.RecyclerTouchListener(getApplicationContext(), mPostRecyclerView, new CRoomActivity.ClickListener() {
            public void onClick(View view, int position) {
                Post post= mDatas.get(position);
                Intent intent = new Intent(getBaseContext(), LookActivity.class);

                intent.putExtra("title", post.getTitle());
                intent.putExtra( "contents", post.getContents());
                intent.putExtra("postId", post.getPostId());

                startActivity(intent);
            }


            public void onLongClick(View view, int position) {
            }
        }));

    }
    @Override
    protected void onStart() {
        super.onStart();
        mDatas = new ArrayList<>();
        mStore.collection(FirebaseID.post)
                .orderBy(FirebaseID.timestamp, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {

                            mDatas.clear();
                            for (DocumentSnapshot snap : value.getDocuments()) {
                                flag = 0;
                                final Map<String, Object> shot = snap.getData();
                                String documentId = String.valueOf(shot.get(FirebaseID.documentID));
                                String nickname = String.valueOf(shot.get(FirebaseID.nickname));
                                String title = String.valueOf(shot.get(FirebaseID.title));
                                String contents = String.valueOf(shot.get(FirebaseID.contents));
                                final String postid = String.valueOf(shot.get(FirebaseID.postId));
                                final Post data = new Post(documentId,nickname, title, contents, postid);
                                mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                for(String o: (ArrayList<String>) document.get(FirebaseID.myChatting)) {
                                                    if(o.equals(postid)){
                                                        mDatas.add(data);
                                                        mAdapter = new PostAdapter(mDatas);
                                                        mPostRecyclerView.setAdapter(mAdapter);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                });
                            }
                        }


                    }
                });
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CRoomActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CRoomActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}