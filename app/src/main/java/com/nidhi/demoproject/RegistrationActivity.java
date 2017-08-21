package com.nidhi.demoproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context=RegistrationActivity.this;
    private Button bt_registerUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        bt_registerUser = (Button)findViewById(R.id.Register);
        bt_registerUser.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Register:
                Toast.makeText(context,"successfully registered",Toast.LENGTH_LONG).show();
        }
    }
}
