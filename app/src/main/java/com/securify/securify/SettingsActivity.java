package com.securify.securify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.securify.securify.model.MainModel;

public class SettingsActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText userNameInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button userNameButton=findViewById(R.id.user_name_confirm_btn);
        userNameButton.setOnClickListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        MainModel model=new MainModel(getApplicationContext());

        //Assign layout items
        userNameInput=findViewById(R.id.user_name_inputfield);
        userNameInput.setText(model.getActiveUser().getName());
        userNameInput.setSelection(userNameInput.getText().length()); //sets cursor to the end of the input field

    }

    @Override
    public void onClick(View view){
        MainModel model=new MainModel(getApplicationContext());
        model.changeUser(userNameInput.getText().toString());

        Toast.makeText(getApplicationContext(),
                "Sie sind jetzt " + model.getActiveUser().getName(),
                Toast.LENGTH_LONG).show();
    }


}

