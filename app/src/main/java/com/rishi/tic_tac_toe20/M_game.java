package com.rishi.tic_tac_toe20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import javax.annotation.Nullable;

public class M_game extends AppCompatActivity {

    private ImageView o_o,o_1,o_2,f_o,f_1,f_2,s_o,s_1,s_2;
    private FirebaseFirestore firebasefirestore;
    private String player="1";
    private String roomkey;
    private static final String TAG = "M_game";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_game);
        Intent intent=getIntent();
        firebasefirestore=FirebaseFirestore.getInstance();
        String user_2=intent.getStringExtra("user_2");
        if(user_2.equals(User.getName())){
            player="2";
        }
        roomkey=intent.getStringExtra("roomkey");
        o_o=findViewById(R.id.o_o);
        o_1=findViewById(R.id.o_1);
        o_2=findViewById(R.id.o_2);
        f_o=findViewById(R.id.f_o);
        f_1=findViewById(R.id.f_1);
        f_2=findViewById(R.id.f_2);
        s_o=findViewById(R.id.s_o);
        s_1=findViewById(R.id.s_1);
        s_2=findViewById(R.id.s_2);
        click_listener();
    }

    void click_listener(){
        o_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("o_o");
            }
        });
        o_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("o_1");
            }
        });
        o_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("o_2");
            }
        });
        f_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("f_o");
            }
        });
        f_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("f_1");
            }
        });
        f_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("f_2");
            }
        });
        s_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("s_o");
            }
        });
        s_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("s_1");
            }
        });
        s_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_move("s_2");
            }
        });
        database_listener();
    }

    void register_move(String position){
        HashMap<String,Object> map=new HashMap<>();
        map.put("player",player);
        map.put("position",position);
        firebasefirestore.collection("roomkey").document(roomkey).collection("moves")
                .document(position).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(M_game.this, "move updated", Toast.LENGTH_SHORT).show();
                    database_listener();
                }else{
                    Toast.makeText(M_game.this, "move not registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void database_listener(){
        firebasefirestore.collection("roomkey").document(roomkey).collection("moves")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(queryDocumentSnapshots!=null){
                            for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                                if(doc.getType()== DocumentChange.Type.ADDED){
                                    Log.d(TAG, "onEvent: "+"new move registered");
                                    DocumentSnapshot document= doc.getDocument();
                                    String plya=document.getString("player");
                                    String pos=document.getString("position");
                                    update_UI(plya,pos);
                                }
                            }
                        }
                    }
                });
    }

    void update_UI(String player,String position){
        Log.d(TAG, "update_UI: "+"UI updated");
        if(player.equals("1")){
            switch (position) {
                case "o_o":
                    o_o.setImageResource(R.drawable.x);
                    break;
                case "o_1":
                    o_1.setImageResource(R.drawable.x);
                    break;
                case "o_2":
                    o_2.setImageResource(R.drawable.x);
                    break;
                case "f_o":
                    f_o.setImageResource(R.drawable.x);
                    break;
                case "f_1":
                    f_1.setImageResource(R.drawable.x);
                    break;
                case "f_2":
                    f_2.setImageResource(R.drawable.x);
                    break;
                case "s_o":
                    s_o.setImageResource(R.drawable.x);
                    break;
                case "s_1":
                    s_1.setImageResource(R.drawable.x);
                    break;
                case "s_2":
                    s_2.setImageResource(R.drawable.x);
                    break;
            }
        }else{
            switch (position) {
                case "o_o":
                    o_o.setImageResource(R.drawable.zero);
                    break;
                case "o_1":
                    o_1.setImageResource(R.drawable.zero);
                    break;
                case "o_2":
                    o_2.setImageResource(R.drawable.zero);
                    break;
                case "f_o":
                    f_o.setImageResource(R.drawable.zero);
                    break;
                case "f_1":
                    f_1.setImageResource(R.drawable.zero);
                    break;
                case "f_2":
                    f_2.setImageResource(R.drawable.zero);
                    break;
                case "s_o":
                    s_o.setImageResource(R.drawable.zero);
                    break;
                case "s_1":
                    s_1.setImageResource(R.drawable.zero);
                    break;
                case "s_2":
                    s_2.setImageResource(R.drawable.zero);
                    break;
            }
        }
    }
}
