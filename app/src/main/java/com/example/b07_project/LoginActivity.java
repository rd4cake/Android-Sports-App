package com.example.b07_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    public int auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //if(currentUser != null){
        //    System.out.println("CREATE NEW INTENT OF MAIN ACTIVE");
        //}
    }


    public void login(View view){
        EditText email = (EditText) findViewById(R.id.Email_EditText);
        EditText password = (EditText) findViewById(R.id.Password_EditText);
        System.out.println(email.getText());
        System.out.println(password.getText());

        // validate that there are emails and passwords to check
        if(!(validateData(view, email, password))){return;}

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent mainact = new Intent(LoginActivity.this, MainActivity.class);
                            FirebaseUser user = mAuth.getCurrentUser();
                            //user.getEmail();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(user.getUid());

                            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){

                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        User logUser = ((User)task.getResult().getValue(User.class));
                                        mainact.putExtra("auth", logUser.auth);
                                        mainact.putExtra("id", logUser.id);

                                        if(logUser.auth == 1)
                                        {
                                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }
                                        startActivity(mainact);
                                        finish();
                                    }
                                }
                            );


                        } else {
                            System.out.println("LOGIN FAILED NOOO");
                            TextView loginFail = (TextView) findViewById(R.id.Login_Failed_TextView);
                            loginFail.setText("LOGIN FAILED. CHECK IF YOU ENTERED EMAIL CORRECTLY");
                        }
                    }
                });
    }

    private boolean validateData(View view, EditText email, EditText password){
        if(email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()){
            makePopUp(view, "Provide credentials to login.");
            return false;
        }
        return true;
    }

    private void makePopUp(View view, String message){
        // inflate layout of popup window
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        // popup window creation
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);
        //showing popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            popupWindow.setElevation(20);
        }
        //Create textview for popup
        TextView textView = (TextView) popupView.findViewById(R.id.popup_text);
        textView.setText(message);
        // dismiss message when clicked
        popupView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });
    public void trasitionToSignUp(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}