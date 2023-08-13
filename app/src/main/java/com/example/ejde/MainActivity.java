package com.example.ejde;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private Button logout;
    private Button byDate,all;
    private EditText date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("HOME");
        logout = findViewById(R.id.logout);
        byDate = findViewById(R.id.button);
        all= findViewById(R.id.allbutton);
        date=findViewById(R.id.date);

        //Using date mails can be deleted
        byDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String d= date.getText().toString();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,startActivity.class));

            }
        });
    }
}