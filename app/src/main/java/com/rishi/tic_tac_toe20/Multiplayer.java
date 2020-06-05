package com.rishi.tic_tac_toe20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Random;

import javax.annotation.Nullable;

public class Multiplayer extends AppCompatActivity {

    private String TAG="Multiplayer";
    private String room_key;
    private Dialog create_room_dialog;
    private boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Button create_room = findViewById(R.id.create_room);
        Button join_room = findViewById(R.id.join_room);
        create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_room();
            }
        });

        join_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join_room();
            }
        });
    }

    private void join_room() {
        final Dialog dialog=new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.joinroom_dialog);
        Button join=dialog.findViewById(R.id.join_bt);
        final EditText token=dialog.findViewById(R.id.room_et);
        dialog.show();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                room_key=token.getText().toString();
                if(room_key.length()>0){
                    validate_token();
                    dialog.dismiss();
                }else{
                    token.setError("Enter key");
                }
            }
        });
    }

    private void validate_token() {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        final DocumentReference document =firestore.collection("roomkey").document(room_key);
             document.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc=task.getResult();
                        if(doc!=null) {
                            if (doc.exists()) {
                                Toast.makeText(Multiplayer.this, "Room present", Toast.LENGTH_SHORT).show();
                                HashMap<String,Object> map=new HashMap<>();
                                map.put("user2",User.getName());
                                document.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Dialog dialog=new Dialog(Multiplayer.this);
                                        dialog.setContentView(R.layout.joined_dialog);
                                        dialog.setCancelable(false);
                                        TextView roomno=dialog.findViewById(R.id.roomid_j);
                                        roomno.setText(room_key);
                                        dialog.show();
                                        check_join();
                                    }
                                });
                            }else{
                                Toast.makeText(Multiplayer.this, "invalid ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void create_room(){
       HashMap<String,Object> map=new HashMap<>();
       map.put("user1", User.getName());
       map.put("status","wait");
       map.put("game","not start");
        Random random=new Random();
       room_key = String.valueOf(random.nextInt(999999));
       Log.d(TAG, "create_room: "+room_key);
       final FirebaseFirestore firestore=FirebaseFirestore.getInstance();
       firestore.collection("roomkey").document(room_key).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   //room created
                   Toast.makeText(Multiplayer.this, "Room created", Toast.LENGTH_SHORT).show();
                   check_join();
                   create_room_dialog=new Dialog(Multiplayer.this);
                   create_room_dialog.setCancelable(false);
                   create_room_dialog.setContentView(R.layout.createroom_dialog);
                   create_room_dialog.show();
                   TextView roomno_t=create_room_dialog.findViewById(R.id.roomid);
                   roomno_t.setText(room_key);
                   Button start=create_room_dialog.findViewById(R.id.startgame);
                   start.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           if(flag){
                               //start game
                               //when second user has joined
                               HashMap<String,Object> map=new HashMap<>();
                               map.put("game","start");
                               firestore.collection("roomkey").document(room_key).update(map);
                               create_room_dialog.dismiss();
                           }else{
                               Toast.makeText(Multiplayer.this, "Second Player not joined yet", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                   ImageView imageView=create_room_dialog.findViewById(R.id.cancel);
                   imageView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           firestore.collection("roomkey").document(room_key).delete();
                           create_room_dialog.dismiss();
                       }
                   });
                   TextView user1=create_room_dialog.findViewById(R.id.user1);
                   user1.setText(User.getName());
               }
           }
       });
   }

    private void check_join(){
       Log.d(TAG, "check_join: "+room_key);
       FirebaseFirestore firestore=FirebaseFirestore.getInstance();
       firestore.collection("roomkey").document(room_key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

               if (documentSnapshot != null) {
                   Toast.makeText(Multiplayer.this, "Data changed", Toast.LENGTH_SHORT).show();
                   Log.d(TAG, "onEvent: "+"Snapshot executed");
                   String user2=documentSnapshot.getString("user2");
                   if(user2!=null&&!user2.equals(User.getName())){
                       //if the user is present them this wil be non-null
                       Log.d(TAG, "onEvent: "+"player joined"+documentSnapshot.getString("user2"));
                       TextView newuser=create_room_dialog.findViewById(R.id.user2);
                       newuser.setText(user2);
                       flag=true;
                   }
                   String status=documentSnapshot.getString("game");
                   if (status != null && status.equals("start")) {
                       String user_2=documentSnapshot.getString("user2");
                       Intent intent=new Intent(Multiplayer.this,M_game.class);
                       intent.putExtra("user_2",user_2);
                       intent.putExtra("roomkey",room_key);
                       startActivity(intent);
                       finish();
                   }else{
                       Log.d(TAG, "onEvent: "+"cannot navigate to new Activity");
                   }
               }
               //this method will be triggered when there will be some changed in this document
               //this changed take place when second user joins the game
               // and hence this callback get triggered
           }
       });
   }
}
