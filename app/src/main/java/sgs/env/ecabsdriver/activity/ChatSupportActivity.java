package sgs.env.ecabsdriver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import sgs.env.ecabsdriver.R;

public class ChatSupportActivity extends BaseActivity implements View.OnClickListener {

    RelativeLayout enviBroadcast,enviChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        inIt();
        listner();
    }


    private void inIt() {
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Envi Chat Support");

        enviBroadcast = findViewById(R.id.enviBroadcast);
        enviChat = findViewById(R.id.enviChat);
    }

    private void listner() {
        enviBroadcast.setOnClickListener(this);
        enviChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enviBroadcast:
                Intent intent = new Intent(ChatSupportActivity.this, BroadcastChatActivity.class);
                startActivity(intent);
                break;
            case R.id.enviChat:
                Intent intent1 = new Intent(ChatSupportActivity.this, ChatActivity.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void refresh() {

    }

    @Override
    protected int setLayoutIfneeded() {
        return 0;
    }

    @Override
    protected int getColor() {
        return 0;
    }

    @Override
    protected void retrivedLocation(Location location) {

    }

}