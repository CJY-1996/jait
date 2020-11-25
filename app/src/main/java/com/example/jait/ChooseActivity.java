package com.example.jait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ChooseActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String nickname;

    private DocumentReference mDocRef = mStore.collection(FirebaseID.user).document(mAuth.getCurrentUser().getUid());
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);


        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final ImageView drawer_profile_image = findViewById(R.id.drawer_profile_image);
                        String imagename = document.get(FirebaseID.nickname) + ".png";
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://jait-2c9d0.appspot.com/images/").child(imagename);
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext()).load(uri).into(drawer_profile_image);
                                drawer_profile_image.setBackground(new ShapeDrawable(new OvalShape()));
                                drawer_profile_image.setClipToOutline(true);
                            }
                        });
                    }
                }
            }
        });


// Navigation View
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);

        ImageButton btn_open = (ImageButton) findViewById(R.id.btn_nav_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        // 닉네임
        TextView drawer_nickname = (TextView) findViewById(R.id.drawer_nav_nickname);
        drawer_nickname.setText(mAuth.getCurrentUser().getDisplayName().toString());

        // 프로필 설정 Button
        findViewById(R.id.drawer_nav_reprofile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseActivity.this, ProfileActivity.class));
            }
        });

        //nav 나가기
        ImageButton btn_close = (ImageButton) findViewById(R.id.drawer_close);
        btn_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        // 로그아웃
        findViewById(R.id.drawer_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("로그 아웃 하시겠습니까?");
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 삭제 작업
                                startActivity(new Intent(ChooseActivity.this, MainActivity.class));
                                FirebaseAuth.getInstance().signOut();
                            }
                        });
                builder.show();
            }
        });

        // 새로운 모임 찾기
        findViewById(R.id.choose_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseActivity.this, WritingActivity.class));
            }
        });
        findViewById(R.id.choose_chatting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseActivity.this, CRoomActivity.class));
            }
        });
    }

    private long time = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
    }
}