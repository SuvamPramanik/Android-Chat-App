package com.application.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.ListViewCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            displayChatMessages();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                displayChatMessages();
            }

            else {
                Toast.makeText(this, "Sorry, couldn't sign you in. Please try again later",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "You have been signed out!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }
        return true;
    }

    public void postMessage(View v){
        EditText msg = (EditText) findViewById(R.id.input);
        String text = msg.getText().toString();

        if(text != null && !text.equals("")) {
            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            FirebaseDatabase.getInstance()
                    .getReference()
                    .push()
                    .setValue(new ChatMessage(msg.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
            msg.setText("");
        } else {
            Toast.makeText(this, "Please enter something !!", Toast.LENGTH_LONG).show();
        }

    }

    private void displayChatMessages(){
        ListView messageList = (ListView) findViewById(R.id.messagesList);
        messageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageList.setStackFromBottom(true);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.message_layout,
                FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //get the views
                RelativeLayout messageLayout = (RelativeLayout) v.findViewById(R.id.message_bubble_layout);
                RelativeLayout messageLayoutParent = (RelativeLayout) v.findViewById(R.id.message_bubble_layout_parent);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) messageLayout.getLayoutParams();

                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);

                if (model.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    messageLayout.setBackgroundResource(R.drawable.bubble2);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    messageLayout.setLayoutParams(params);
                }
                // If not my message then align to left
                else {
                    messageLayout.setBackgroundResource(R.drawable.bubble1);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    messageLayout.setLayoutParams(params);
                }

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("HH:mm A", model.getMessageTime()));

            }
        };
        messageList.setAdapter(adapter);
    }
}
