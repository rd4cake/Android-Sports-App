package com.example.b07_project;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class unApprovedEventPageDenny extends AppCompatActivity {
    public static String activity;
    public static String[] activityInfo = new String[5];
    public static String userId;
    public static Boolean isThisMyEvent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_denny);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userId = getIntent().getStringExtra("userID");
        if (userId == null) userId = "";

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference("Events");
        DatabaseReference userRef = database.getReference("Users");

        eventRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Event event;
                //((ViewGroup) findViewById(R.id.profilePage)).removeView(findViewById(R.id.sampleEventCard));
                for(DataSnapshot i : task.getResult().getChildren())
                {
                    event = i.getValue(Event.class);
                    Event finalEvent = event;
                    userRef.child(event.getOwnerId() + "").child("firstName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String hostName = task.getResult().getValue(String.class);
                            addCard(findViewById(R.id.profilePage), finalEvent, hostName);
                        }
                    });



                }

            }
        });

    }

    public void addCard(View view, Event event, String hostName) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.cardList);
        View v = LayoutInflater.from(this).inflate(R.layout.activity_card2, null);
        TextView textView = v.findViewById(R.id.hostName);
        if (hostName == null) hostName = "default";
        textView.setText(hostName);
        textView.setHint(event.getOwnerId());
        //Log.i("ownerId", event.getOwnerId());
        textView = v.findViewById(R.id.cardEventDate);
        textView.setText(event.getStartDateString());
        textView = v.findViewById(R.id.startTime);
        textView.setText(event.getStartTimeString());
        textView = v.findViewById(R.id.endTime);
        textView.setText(event.getEndTimeString());
        textView = v.findViewById(R.id.profileEventName);
        textView.setText(event.getName());
        textView.setHint(event.getId() + "");
        layout.addView(v);
    }

    public void transitionToDesc(View view){
        ViewGroup newView = (ViewGroup) view;

        TextView date = (TextView) newView.getChildAt(0);
        TextView start = (TextView) newView.getChildAt(1);
        TextView end = (TextView) newView.getChildAt(2);

        ViewGroup eventContainer = (ViewGroup) newView.getChildAt(4);
        TextView event = (TextView) eventContainer.getChildAt(0);
        TextView host = (TextView) eventContainer.getChildAt(1);

        Intent addProfile = new Intent(this, ActivityDesc.class);
        activityInfo[0] = date.getText().toString();
        activityInfo[1] = start.getText().toString();
        activityInfo[2] = end.getText().toString();
        activityInfo[3] = event.getText().toString();
        activityInfo[4] = host.getText().toString();
        int eventId = Integer.parseInt(event.getHint().toString());
        isThisMyEvent = host.getHint().toString().equals(userId);
        //Log.i("host id", host.getHint());
        Log.i("do i own this event?", isThisMyEvent.toString());

        addProfile.putExtra("isThisMyEvent", isThisMyEvent);
        addProfile.putExtra(activity, activityInfo);
        addProfile.putExtra("eventId", eventId);
        addProfile.putExtra("userId", userId);
        startActivity(addProfile);
    }

    private void transitionToViewUpcomingEvents(View view){
        Intent intent = new Intent(this, UpcomingEventsDriver.class);

        startActivity(intent);
    }

}