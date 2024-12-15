package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginPicked = true;

    private short lan = 0;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private ImageView arrowBackLogin;
    private TextView labelLogin;
    private TextView loginLabel;
    private TextView passLabel;
    private TextView rePassLabel;
    private EditText loginInput;
    private EditText passInput;
    private EditText rePassInput;
    private Button butTog;
    private Button butCon;
    private LinearLayout logLay;
    private LinearLayout pasLay;
    private LinearLayout rePassLay;
    private LinearLayout butLay;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            isLoginPicked = bundle.getBoolean("loginState");
            lan = bundle.getShort("loginLan");
        }
            Log.d(TAG, "Intent received");
        } else {
            Log.e(TAG, "Intent is null");
            finish(); // Optionally finish the activity if the intent is null
            return;
        }
        
        dbHelper = new DatabaseHelper(this);
        xmlInit();
        setup();
        changeT();

        
    }

    private void xmlInit() {
        arrowBackLogin = findViewById(R.id.arrowBackLogin);
        loginInput = findViewById(R.id.loginInput);
        passInput = findViewById(R.id.passInput);
        rePassInput = findViewById(R.id.rePassInput);
        butTog = findViewById(R.id.butTog);
        butCon = findViewById(R.id.butCon);
        logLay = findViewById(R.id.logLay);
        pasLay = findViewById(R.id.pasLay);
        rePassLay = findViewById(R.id.rePassLay);
        butLay = findViewById(R.id.butLay);
        labelLogin = findViewById(R.id.labelLogin);
        labelLogin = findViewById(R.id.labelLogin);
        passLabel = findViewById(R.id.passLabel);
        rePassLabel = findViewById(R.id.rePassLabel);
        loginLabel = findViewById(R.id.loginLabel);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
            isLoginPicked = bundle.getBoolean("loginState");
            lan = bundle.getShort("loginLan");
        }
        setup();
        super.onNewIntent(intent);
    }

    private void setup() {
        arrowBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        });
        setBasedOnLan();
    }

    private void setBasedOnLan() {
        switch(lan){
            case 0:
                setPolish();
                break;
            case 1:
                setEnglish();
                break;
            default:
                Log.e(MainActivity.TAG,"HUH wrong lan?");
        }
}
    private void setPolish(){
        loginLabel.setText(R.string.loginLoginPl);
        loginInput.setHint(R.string.loginHintLoginPl);
        passLabel.setText(R.string.passwordLoginPl);
        passInput.setHint(R.string.passHintLoginPl);
        if (isLoginPicked){
            labelLogin.setText(R.string.logInLabelPl);
            butTog.setText(R.string.toRegisterButPl);
            butCon.setText(R.string.logInButPl);
            rePassLay.setVisibility(View.GONE);

        }
        else {
            labelLogin.setText(R.string.registerLabelPl);
            butTog.setText(R.string.toLogInButPl);
            butCon.setText(R.string.registerButPl);
            rePassLay.setVisibility(View.VISIBLE);
            rePassLabel.setText(R.string.rePasswordLoginPl);
            rePassInput.setHint(R.string.rePassHintLoginPl);
        }
    }
    private void setEnglish(){
        loginLabel.setText(R.string.loginLoginEn);
        loginInput.setHint(R.string.loginHintLoginEn);
        passLabel.setText(R.string.passwordLoginEn);
        passInput.setHint(R.string.passHintLoginEn);
        if (isLoginPicked){
            labelLogin.setText(R.string.logInLabelEn);
            butTog.setText(R.string.toRegisterButEn);
            butCon.setText(R.string.logInButEn);
            rePassLay.setVisibility(View.GONE);
        }
        else {
            labelLogin.setText(R.string.registerLabelEn);
            butTog.setText(R.string.toLogInButEn);
            butCon.setText(R.string.registerButEn);
            rePassLay.setVisibility(View.VISIBLE);
            rePassLabel.setText(R.string.rePasswordLoginEn);
            rePassInput.setHint(R.string.rePassHintLoginEn);
        }
    }
    private void toggleState(){
        isLoginPicked = !isLoginPicked;
    }
    private void changeT(){
        setBasedOnLan();
        butTog.setOnClickListener(v -> {
            toggleState();
            changeT();
        });
        if (isLoginPicked){
            butCon.setOnClickListener(v->logIn());
        }
        else{
            butCon.setOnClickListener(v->register());
        }
    }

    private void register() {
        String login = loginInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();
        String rePass = rePassInput.getText().toString().trim();

        if (!login.isEmpty() && !pass.isEmpty()) {
            if (pass.equals(rePass)) {
                boolean result = dbHelper.addUser(login, pass);
                if (result) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("myLogin",dbHelper.getUserIdByLogin(login));
                    editor.apply();
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Błąd")
                            .setMessage("Takie konto już istnieje")
                            .setPositiveButton(R.string.okPl, (dialog, which) -> dialog.dismiss());
                    builder.create().show();
                    Log.e("Register", "Registration failed");
                }
            }
        } else {
            showErrorDialog();
            Log.e("Register", "Invalid input or passwords do not match");
        }
    }

    private void logIn() {
        String login = loginInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();
        if (!login.isEmpty() && !pass.isEmpty()){
            int id = dbHelper.checkCred(login,pass);
            if (id == - 1 || id == -2){
                showErrorDialog();
            }
            else {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("myLogin",dbHelper.getUserIdByLogin(login));
                editor.apply();
                startActivity(intent);

            }
        }

    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        switch(lan) {
            case 0: // Polish
                builder.setTitle(R.string.errorTitlePl)
                       .setMessage(R.string.errorMessagePl)
                       .setPositiveButton(R.string.okPl, (dialog, which) -> dialog.dismiss());
                break;
            case 1: // English
                builder.setTitle(R.string.errorTitleEn)
                       .setMessage(R.string.errorMessageEn)
                       .setPositiveButton(R.string.okEn, (dialog, which) -> dialog.dismiss());
                break;
            default:
                Log.e(MainActivity.TAG, "Unknown language code");
        }
        
        builder.create().show();
    }
}