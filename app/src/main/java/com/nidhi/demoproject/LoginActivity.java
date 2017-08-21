package com.nidhi.demoproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nidhi.demoproject.common.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = LoginActivity.this;
    private Button bt_signIn,bt_Register;
    private EditText et_username,et_password;
    ProgressDialog progressDialog;

    /* ButterKnife annotations:

    @BindView(R.id.et_username) EditText et_username;
    @BindView(R.id.editText_password) EditText et_password;
    @BindView(R.id.button_signIn) Button bt_signIn;
    @BindView(R.id.button_Register) Button bt_Register;
    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        et_username = (EditText)findViewById(R.id.et_username);

        et_username.setFilters(new InputFilter[]{Utils.filter});

        et_password =  (EditText)findViewById(R.id.editText_password);
        et_password.setFilters(new InputFilter[]{Utils.filter});


        bt_Register = (Button)findViewById(R.id.button_Register);
        bt_signIn = (Button)findViewById(R.id.button_signIn);

        bt_Register.setOnClickListener(this);
        bt_signIn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        //ButterKnife.bind(this);

    }

    //@OnClick(R.id.button_signIn) public void OnClicksignIn() {}
    //@OnClick(R.id.button_Register) public void OnClickRegister(){}
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_Register:
                //Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                //startActivity(intent);
                //finish();
                //onNewIntent(intent);
                break;

            case R.id.button_signIn:
                //progressDialog.setMessage("Logging in...please wait");
                //progressDialog.setCancelable(false);
                //progressDialog.show();

                //validations

                if (et_username.length() > 0) {

                    if (et_username.length() >= 6) {

                        if(et_password.length()>0){

                            if(et_password.length()>=6){

                                Toast.makeText(context, "Successfully logged in", Toast.LENGTH_SHORT).show();

                                String username = et_username.getText().toString();
                                String password = et_password.getText().toString();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("keyForUsername",username);
                                intent.putExtra("keyForPassword",password);
                                startActivity(intent);





                            }else{
                                Toast.makeText(context,"Password must be minimum six characters in length",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(context,"Password cannot be blank ",Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, "Username must be minimum of six characters in length", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Username cannot be blank", Toast.LENGTH_SHORT).show();
                }
                break;



//                AppPreferences.getInstance(context).addStringToStore("name",uname);
//                AppPreferences.getInstance(context).getStringFromStore("name");







        }

    }
}
