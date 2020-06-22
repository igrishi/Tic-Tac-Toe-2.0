package com.rishi.tic_tac_toe20;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Nullable;

public class M_game extends AppCompatActivity {

    private ImageView o_o, o_1, o_2, f_o, f_1, f_2, s_o, s_1, s_2;
    private int[][] data;
    private FirebaseFirestore firebasefirestore;
    private String player = "1";
    private String roomkey;
    private static final String TAG = "M_game";
    private boolean win = true;
    private String user_2;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_game);
        dialog=new Dialog(this);
        Intent intent = getIntent();
        data = new int[3][3];
        ;
        firebasefirestore = FirebaseFirestore.getInstance();
        user_2 = intent.getStringExtra("user_2");
        assert user_2 != null;
        if (user_2.equals(User.getName())) {
            player = "2";
        }
        roomkey = intent.getStringExtra("roomkey");
        o_o = findViewById(R.id.o_o);
        o_1 = findViewById(R.id.o_1);
        o_2 = findViewById(R.id.o_2);
        f_o = findViewById(R.id.f_o);
        f_1 = findViewById(R.id.f_1);
        f_2 = findViewById(R.id.f_2);
        s_o = findViewById(R.id.s_o);
        s_1 = findViewById(R.id.s_1);
        s_2 = findViewById(R.id.s_2);
        click_listener();
    }

    void click_listener() {
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

    void register_move(String position) {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("player", player);
        map.put("position", position);
        final DocumentReference documentReference = firebasefirestore
                .collection("roomkey").document(roomkey)
                .collection("moves")
                .document(position);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (!documentSnapshot.exists()) {
                        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(M_game.this, "move updated", Toast.LENGTH_SHORT).show();
                                    database_listener();
                                } else {
                                    Toast.makeText(M_game.this, "move not registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(M_game.this, "cannot register this move", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
       /* firebasefirestore.collection("roomkey").document(roomkey).collection("moves")
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
        });*/
    }

    void database_listener() {
        firebasefirestore.collection("roomkey").document(roomkey).collection("moves")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Log.d(TAG, "onEvent: " + "new move registered");
                                    DocumentSnapshot document = doc.getDocument();
                                    String plya = document.getString("player");
                                    String pos = document.getString("position");
                                    update_UI(plya, pos);
                                }
                                if(doc.getType()== DocumentChange.Type.REMOVED){
                                    dialog.dismiss();
                                    for(int i=0;i<3;++i){
                                        for(int j=0;j<3;++j){
                                            data[i][j]=0;
                                        }
                                    }
                                    reset_UI();
                                }
                            }
                        }
                    }
                });
    }

    void update_UI(String player, String position) {
        Log.d(TAG, "update_UI: " + "UI updated");
        if (player.equals("1")) {
            switch (position) {
                case "o_o":
                    o_o.setImageResource(R.drawable.x);
                    data[0][0] = 1;
                    break;
                case "o_1":
                    o_1.setImageResource(R.drawable.x);
                    data[0][1] = 1;
                    break;
                case "o_2":
                    o_2.setImageResource(R.drawable.x);
                    data[0][2] = 1;
                    break;
                case "f_o":
                    f_o.setImageResource(R.drawable.x);
                    data[1][0] = 1;
                    break;
                case "f_1":
                    f_1.setImageResource(R.drawable.x);
                    data[1][1] = 1;
                    break;
                case "f_2":
                    f_2.setImageResource(R.drawable.x);
                    data[1][2] = 1;
                    break;
                case "s_o":
                    s_o.setImageResource(R.drawable.x);
                    data[2][0] = 1;
                    break;
                case "s_1":
                    s_1.setImageResource(R.drawable.x);
                    data[2][1] = 1;
                    break;
                case "s_2":
                    s_2.setImageResource(R.drawable.x);
                    data[2][2] = 1;
                    break;
            }
            player_win();
        } else {
            switch (position) {
                case "o_o":
                    o_o.setImageResource(R.drawable.zero);
                    data[0][0] = 2;
                    break;
                case "o_1":
                    o_1.setImageResource(R.drawable.zero);
                    data[0][1] = 2;
                    break;
                case "o_2":
                    o_2.setImageResource(R.drawable.zero);
                    data[0][2] = 2;
                    break;
                case "f_o":
                    f_o.setImageResource(R.drawable.zero);
                    data[1][0] = 2;
                    break;
                case "f_1":
                    f_1.setImageResource(R.drawable.zero);
                    data[1][1] = 2;
                    break;
                case "f_2":
                    f_2.setImageResource(R.drawable.zero);
                    data[1][2] = 2;
                    break;
                case "s_o":
                    s_o.setImageResource(R.drawable.zero);
                    data[2][0] = 2;
                    break;
                case "s_1":
                    s_1.setImageResource(R.drawable.zero);
                    data[2][1] = 2;
                    break;
                case "s_2":
                    s_2.setImageResource(R.drawable.zero);
                    data[2][2] = 2;
                    break;
            }
            player_win();
        }
    }

    void player_win() {
        if ((data[0][0] == 1 && data[0][1] == 1 && data[0][2] == 1)
                || (data[0][0] == 2 && data[0][1] == 2 && data[0][2] == 2)) {
            int p = data[0][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        }
        else if ((data[1][0] == 1 && data[1][1] == 1 && data[1][2] == 1)
                || (data[1][0] == 2 && data[1][1] == 2 && data[1][2] == 2)) {
            int p = data[1][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[2][0] == 1 && data[2][1] == 1 && data[2][2] == 1)
                || (data[2][0] == 2 && data[2][1] == 2 && data[2][2] == 2)) {
            int p = data[2][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[0][0] == 1 && data[1][0] == 1 && data[2][0] == 1)
                || (data[0][0] == 2 && data[1][0] == 2 && data[2][0] == 2)) {
            int p = data[0][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[0][1] == 1 && data[1][1] == 1 && data[2][1] == 1)
                || (data[0][1] == 2 && data[1][1] == 2 && data[2][1] == 2)) {
            int p = data[0][1];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[0][2] == 1 && data[1][2] == 1 && data[2][2] == 1)
                || (data[0][2] == 2 && data[1][2] == 2 && data[2][2] == 2)) {
            int p = data[0][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[0][0] == 1 && data[1][1] == 1 && data[2][2] == 1)
                || (data[0][0] == 2 && data[1][1] == 2 && data[2][2] == 2)) {
            int p = data[0][0];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        } else if ((data[0][2] == 1 && data[1][1] == 1 && data[2][0] == 1)
                || (data[0][2] == 2 && data[1][1] == 2 && data[2][0] == 2)) {
            int p = data[0][2];
            Log.d(TAG, "player_win: " + p);
            Win_Dialog(p);
        }
    }

    void Win_Dialog(int p) {
        if (win) {
            win = false;
            int winnner=Integer.parseInt(player);
            String result;
            if(p==winnner){
                result="You Won";
            }
            else{
                result="you lose";
            }
            dialog.setContentView(R.layout.win_dialog);
            TextView text5=dialog.findViewById(R.id.text5);
            ImageView image=dialog.findViewById(R.id.wind_image);
            final TextView restart=dialog.findViewById(R.id.restart_wd);
            TextView leave_r=dialog.findViewById(R.id.leave_wd);
            if(result.equals("You Won")){
                image.setImageResource(R.drawable.win);
            }
            else {
                image.setImageResource(R.drawable.lose);
            }
            text5.setText(result);
            dialog.setCancelable(false);
            leave_r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(M_game.this, "game restart", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    restart_game();
                }
            });
            dialog.show();
        }
    }

    void restart_game(){
        Log.d(TAG, "restart: ");
        /*firebasefirestore.collection("roomkey").document(roomkey).collection("moves").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()){
                        doc.getReference().delete();
                        Log.d(TAG, "onEvent: "+"deleting");
                    }
                }
                // arrays.fill sets all element of the array as value specified bascially it runs a loop over the array
            }
        });*/
        firebasefirestore.collection("roomkey").document(roomkey).collection("moves").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot=task.getResult();
                        for(DocumentSnapshot doc:querySnapshot.getDocuments()){
                            doc.getReference().delete();
                            Log.d(TAG, "onComplete: "+"deleting");
                        }
                    }
                });
    }

    void reset_UI(){
        Log.d(TAG, "reset_UI: ");
        o_o.setImageDrawable(null);
        o_1.setImageDrawable(null);
        o_2.setImageDrawable(null);
        f_o.setImageDrawable(null);
        f_1.setImageDrawable(null);
        f_2.setImageDrawable(null);
        s_o.setImageDrawable(null);
        s_1.setImageDrawable(null);
        s_2.setImageDrawable(null);
        win=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
