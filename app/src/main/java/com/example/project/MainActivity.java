package com.example.project;

import static android.Manifest.permission.SEND_SMS;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
//import android.content.pm.ShortcutInfo;
//import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
//import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ProjectTag";
    private short lan = 0;
    Integer[] orderState ={1,null,null,null};

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private Menu menu;
    private MenuItem orderList;
    private MenuItem saveCart;
    private MenuItem about;
    private MenuItem shareCart;
    private MenuItem sendSms;
    private MenuItem changeLan;
    private MenuItem saveState;
    private MenuItem loadState;
    private MenuItem createAcc;
    private MenuItem clearSavedState;
    private MenuItem sendMail;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private View arrowBackMenu;
    ImageView pcImageView;
    Spinner pcSpinner;
    CheckBox mouseCheck;
    ImageView mouseImageView;
    Spinner mouseSpinner;
    CheckBox keyboardCheck;
    ImageView keyboardImageView;
    Spinner keyboardSpinner;
    CheckBox cameraCheck;
    ImageView cameraImageView;
    Spinner cameraSpinner;
    TextView finalPrice;
    Button orderButton;
    TextView extraLabel;
    TextView priceLabel;
    TextView loginTV;
    Button loginBut;
    TextView navHeader;
    TextView navSubtext;


    ArrayList<String[]> pcOptions;
    ArrayList<String[]> mouseOptions;
    ArrayList<String[]> keyboardOptions;
    ArrayList<String[]> cameraOptions;

    // DONE!!!! - PENTLA W NAZWACH AKCESORII I DODANIE CENY//
    // DONE!!!! - LOGOWANIE //
    // DONE!!!! - WYSYŁANIE MAIL, SMS, itp //
    // DONE!!!! - IKONA //
    // DONE!!!! - CAŁE TO ZAPISYWANIE ZAMÓWIEŃ (Po zapisie czyszczenie + zerowanie) //
    // TODO - SKRÓTY DYNAMICZNE //
    // TODO - (Zapisanie zdj ig)//
    // DONE (Chyba??? nadal nie wiem o co chodzi) - UDOSTĘPANIE Z MENU (o co chodzi z tą aplikacją???) //
    // TODO - MAIL W TLE //
    // DONE!!!! - ZAMÓWIENIA Z LISTVIEW!! //
    // DONE!!!! - TO menu głupie moze //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the status bar
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        xmlInnit();
        startupDatabaseInnit();
        setAllSpinnerEquip();
        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        if (sharedPreferences.getInt("myLogin",-5) == -5){ // -5 meaning not existing - no logins before //
            loginOff();
        }

        loadStates();




        setByLanguage();

        getState();
        settingOnChange();



    }



    // --- XML Innitialization --- //
    private void xmlInnit(){
        pcImageView = findViewById(R.id.pcImage);
        pcSpinner = findViewById(R.id.pcSpinner);
        mouseCheck = findViewById(R.id.mouseCheck);
        mouseImageView = findViewById(R.id.mouseImage);
        mouseSpinner = findViewById(R.id.mouseSpinner);
        keyboardCheck = findViewById(R.id.keyboardCheck);
        keyboardImageView = findViewById(R.id.keyboardImage);
        keyboardSpinner = findViewById(R.id.keyboardSpinner);
        cameraCheck = findViewById(R.id.cameraCheck);
        cameraImageView = findViewById(R.id.cameraImage);
        cameraSpinner = findViewById(R.id.cameraSpinner);
        finalPrice = findViewById(R.id.finPrice);
        orderButton = findViewById(R.id.orderBut);
        extraLabel = findViewById(R.id.extraLabel);
        priceLabel = findViewById(R.id.cenaTextView);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(false);
        }


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        menu = navigationView.getMenu();

        if (menu!=null){
            orderList = menu.findItem(R.id.orderList);
            saveCart = menu.findItem(R.id.saveCart);
            about = menu.findItem(R.id.about);
            shareCart = menu.findItem(R.id.shareCart);
            sendSms = menu.findItem(R.id.sendSms);
            changeLan = menu.findItem(R.id.changeLan);
            saveState = menu.findItem(R.id.saveState);
            loadState = menu.findItem(R.id.loadState);
            createAcc = menu.findItem(R.id.registerOpt);
            clearSavedState = menu.findItem(R.id.clearSavedState);
            sendMail = menu.findItem(R.id.sendMail);
        }

        // Initialize the ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigationDrawerOpenPl, R.string.navigationDrawerClosePl);

        // Add the drawer listener
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Sync the toggle state with the drawer

        // Set the toolbar navigation click listener
        toggle.setToolbarNavigationClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        View headerView = navigationView.getHeaderView(0);
        arrowBackMenu = headerView.findViewById(R.id.arrowBackMenu);
        arrowBackMenu.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        loginTV = headerView.findViewById(R.id.loginTextView);
        loginBut = headerView.findViewById(R.id.loginButton);
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                if (sharedPreferences.getInt("myLogin", -1) == -1) {
                    Log.d(TAG, "User is not logged in, starting LoginActivity");
                    intent.putExtra("loginState", true);
                    intent.putExtra("loginLan", lan);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "User is logged in, logging off");
                    loginOff();
                }

            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getInt("myLogin", -1) == -1) {
                    // User is not logged in, show alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    switch (lan) {
                        case 0: // Polish
                            builder.setTitle("Błąd")
                                    .setMessage("Musisz być zalogowany, aby złożyć zamówienie")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                            break;
                        case 1: // English
                            builder.setTitle("Error")
                                    .setMessage("You need to be logged in to place an order")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                            break;
                    }
                    builder.show();
                } else {
                    // User is logged in, process the order
                    int userId = sharedPreferences.getInt("myLogin", -1);
                    float totalPrice = Float.parseFloat(finalPrice.getText().toString());
                    String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
                    
                    // Filter out null values from orderState
                    ArrayList<Integer> validEquipment = new ArrayList<>();
                    for (Integer equipId : orderState) {
                        if (equipId != null) {
                            validEquipment.add(equipId);
                        }
                    }
                    
                    if (!validEquipment.isEmpty()) {
                        // Add the order
                        boolean orderAdded = dbHelper.addOrder(userId, totalPrice, currentDate);
                        
                        if (orderAdded) {
                            int orderId = dbHelper.getLatestOrderId();
                            
                            if (orderId != -1) {
                                boolean allConnectionsAdded = true;
                                Integer firstConnectionId = null;
                                
                                // Add connections for each piece of equipment
                                for (Integer equipmentId : validEquipment) {
                                    boolean connectionAdded = dbHelper.insertOrderEquipmentConnection(orderId, equipmentId);
                                    if (!connectionAdded) {
                                        allConnectionsAdded = false;
                                        break;
                                    }
                                    // Store the first connection ID
                                    if (firstConnectionId == null) {
                                        firstConnectionId = dbHelper.getLatestOrderEquipmentId();
                                    }
                                }
                                
                                if (allConnectionsAdded && firstConnectionId != null) {
                                    // Update the order with the first connection ID
                                    boolean orderUpdated = dbHelper.updateOrderConnection(orderId, firstConnectionId);
                                    
                                    if (orderUpdated) {
                                        // Show success message and clear the order
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        switch (lan) {
                                            case 0: // Polish
                                                builder.setTitle("Sukces")
                                                        .setMessage("Zamówienie zostało złożone")
                                                        .setPositiveButton("OK", (dialog, which) -> {
                                                            dialog.dismiss();
                                                            // Clear the order state
                                                            Arrays.fill(orderState, null);
                                                            orderState[0] = 1; // Reset to default PC
                                                            mouseCheck.setChecked(false);
                                                            keyboardCheck.setChecked(false);
                                                            cameraCheck.setChecked(false);
                                                            updatePrice();
                                                        });
                                                break;
                                            case 1: // English
                                                builder.setTitle("Success")
                                                        .setMessage("Order has been placed")
                                                        .setPositiveButton("OK", (dialog, which) -> {
                                                            dialog.dismiss();
                                                            // Clear the order state
                                                            Arrays.fill(orderState, null);
                                                            orderState[0] = 1; // Reset to default PC
                                                            mouseCheck.setChecked(false);
                                                            keyboardCheck.setChecked(false);
                                                            cameraCheck.setChecked(false);
                                                            updatePrice();
                                                        });
                                                break;
                                        }
                                        builder.show();
                                    } else {
                                        showErrorMessage();
                                    }
                                } else {
                                    showErrorMessage();
                                }
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                }
            }
            
            private void showErrorMessage() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                switch (lan) {
                    case 0: // Polish
                        builder.setTitle("Błąd")
                                .setMessage("Nie udało się złożyć zamówienia")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        break;
                    case 1: // English
                        builder.setTitle("Error")
                                .setMessage("Failed to place the order")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        break;
                }
                builder.show();
            }
        });

        navHeader = headerView.findViewById(R.id.header_title);
        navSubtext = headerView.findViewById(R.id.header_subtitle);

        // Set Language
        switch (lan) {
            case 0:
                toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigationDrawerOpenPl, R.string.navigationDrawerClosePl);
                break;
            case 1:
                toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigationDrawerOpenEn, R.string.navigationDrawerCloseEn);
                break;
            default:
                toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigationDrawerOpenPl, R.string.navigationDrawerClosePl);
        }

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.orderList:
                        Intent listIntent = new Intent(MainActivity.this, ListViewActivity.class);
                        startActivity(listIntent);
                        Log.v(TAG, "Order List clicked");
                        break;
                    case R.id.saveCart:
                        Log.v(TAG, "Save Cart clicked");
                        break;
                    case R.id.about:
                        aboutMet();

                        Log.v(TAG, "About me clicked");
                        break;
                    case R.id.shareCart:
                        shareOrder();
                        Log.v(TAG, "Share Cart clicked");
                        break;
                    case R.id.sendSms:
                        sendOrderViaSms();
                        Log.v(TAG, "Send SMS clicked");
                        break;
                    case R.id.changeLan:
                        lan = (lan == 0) ? (short) 1 : (short) 0;
                        setByLanguage();
                        invalidateOptionsMenu();
                        break;
                    case R.id.saveState:
                        saveStates();
                        break;
                    case R.id.loadState:
                        loadStates();
                        loadStates();
                        loadStates();
                        break;
                    case R.id.registerOpt:
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        intent.putExtra("loginState",false);
                        intent.putExtra("loginLan",lan);
                        startActivity(intent);
                        break;
                    case R.id.clearSavedState:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("pcState", -1);
                        editor.putInt("mouseState", -1);
                        editor.putInt("keyboardState", -1);
                        editor.putInt("cameraState", -1);
                        editor.apply();
                        loadStates();
                        loadStates();
                        loadStates();
                        //czemu? nie wiem... Ale działa :D
                        break;
                    case R.id.sendMail:
                        sendEmailNotInBackground();
                        //juz se dodaje te miliard glupot po to zeby nie dzialalo
                        break;
                    default:
                        Log.v(TAG, "Bad Menu Item");
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


    // METHODS FOR INSERTING DATA TO DATABASE //
    // adding user //
    private void addUserToDatabase(String login, String password) {
        boolean isInserted = dbHelper.insertUser(login, password);
        if (isInserted) {
            Log.v(TAG,"Added user: \n Login: " + login + "\n Password: "+password);
        } else {
            Log.v(TAG,"Failed in adding user");
        }
    }

    // adding equipment //
    private short addEquipmentToDatabase(String name, String image, int type, float price) {
        // Check if the equipment already exists
        Cursor cursor = dbHelper.getEquipmentByName(name); // Assuming this method exists
        if (cursor != null && cursor.moveToFirst()) {
            Log.v(TAG, "Equipment already exists: " + name);
            cursor.close(); // Close the cursor when done
            return 0; //0 meaning duplicate
        }
        cursor.close(); // Close the cursor if it was not null

        boolean isInserted = dbHelper.insertEquipment(name, image, type, price);
        if (isInserted) {
            Log.v(TAG,"Added equipment: \n Name: " + name + "\n Image: " + image + "\n Type: " + type + "\n Price: " + price);
            return 1; //1 meaning success
        } else {
            Log.v(TAG,"Failed in adding equipment");
            return -1; //-1 meaning error
        }

    }

    // adding equipment description //
    private void addEquipmentDescriptionToDatabase(int equipmentId, String languageCode, String description) {
        boolean isInserted = dbHelper.insertEquipmentDescription(equipmentId, languageCode, description);
        if (isInserted) {
            Log.v(TAG,"Added equipment description: \n Equipment ID: " + equipmentId + "\n Language Code: " + languageCode + "\n Description: " + description);
        } else {
            Log.v(TAG,"Failed in adding equipment description " );
        }
    }

    // adding order //
    private void addOrderToDatabase(float price, String date, String name, int connection) {
        boolean isInserted = dbHelper.insertOrder(price, date, name, connection);
        if (isInserted) {
            Log.v(TAG,"Added order: \n Price: " + price + "\n Date: "+date + "\n Name: " + name + "\n Connection: " + connection);
        } else {
            Log.v(TAG,"Failed in adding order");
        }
    }

    // adding order equipment connection //
    private void addOrderEquipmentConnectionToDatabase(int orderId, int equipmentId) {
        boolean isInserted = dbHelper.insertOrderEquipmentConnection(orderId, equipmentId);
        if (isInserted) {
            Log.v(TAG,"Added order equipment connection: \n Order ID: " + orderId + "\n Equipment ID: "+equipmentId);
        } else {
            Log.v(TAG,"Failed in adding order equipment connection");
        }
    }
    private void startupDatabaseInnit(){
        Resources res = getResources();
        String[] pcName = res.getStringArray(R.array.pcs);
        String[] pcPrice = res.getStringArray(R.array.pcPrice);
        String[] mouseName = res.getStringArray(R.array.mouseName);
        String[] mousePrice = res.getStringArray(R.array.mousePrice);
        String[] keyboardName = res.getStringArray(R.array.keyboardName);
        String[] keyboardPrice = res.getStringArray(R.array.keyboardPrice);
        String[] cameraName = res.getStringArray(R.array.webcamName);
        String[] cameraPrice = res.getStringArray(R.array.webcamPrice);
//        pcPrice = new String[]{"12", "32", "41", "51", "22"};

        Log.v(TAG, "Resource ID for pcPrice: " + R.array.pcPrice);

        String[] pcDescPl = res.getStringArray(R.array.pcDescPl);
        String[] pcDescEn = res.getStringArray(R.array.pcDescEn);
        String[] mouseDescPl = res.getStringArray(R.array.mouseDescPl);
        String[] mouseDescEn = res.getStringArray(R.array.mouseDescEn);
        String[] keyboardDescPl = res.getStringArray(R.array.keyboardDescPl);
        String[] keyboardDescEn = res.getStringArray(R.array.keyboardDescEn);
        String[] cameraDescPl = res.getStringArray(R.array.webcamDescPl);
        String[] cameraDescEn = res.getStringArray(R.array.webcamDescEn);
        Log.v(TAG, "PC Names: " + Arrays.toString(pcName));
        Log.v(TAG, "PC Prices: " + Arrays.toString(pcPrice));

        Log.v(TAG,"PL Desc: " + Arrays.toString(pcDescPl));
        Log.v(TAG,"En Desc: " + Arrays.toString(pcDescEn));
        for (int i = 0; i<pcName.length;i++){
            float price = Float.parseFloat(pcPrice[i].replace("f",""));
            addEquipmentToDatabase(pcName[i],("pc"+(i+1)),0,price);
            addEquipmentDescriptionToDatabase(i+1,"pl",pcDescPl[i]);
            addEquipmentDescriptionToDatabase(i+1,"en",pcDescEn[i]);
        }
        for (int i = 0; i<mouseName.length;i++){
            float price = Float.parseFloat(mousePrice[i].replace("f",""));
            addEquipmentToDatabase(mouseName[i],("mouse"+(i+1)),1,price);
            addEquipmentDescriptionToDatabase(i+1,"pl",mouseDescPl[i]);
            addEquipmentDescriptionToDatabase(i+1,"en",mouseDescEn[i]);
        }
        for (int i = 0; i<keyboardName.length;i++){
            float price = Float.parseFloat(keyboardPrice[i].replace("f",""));
            addEquipmentToDatabase(keyboardName[i],("keyboard"+(i+1)),2,price);
            addEquipmentDescriptionToDatabase(i+1,"pl",keyboardDescPl[i]);
            addEquipmentDescriptionToDatabase(i+1,"en",keyboardDescEn[i]);
        }
        for (int i = 0; i<cameraName.length;i++){
            float price = Float.parseFloat(cameraPrice[i].replace("f",""));
            addEquipmentToDatabase(cameraName[i],("camera"+(i+1)),3,price);
            addEquipmentDescriptionToDatabase(i+1,"pl",cameraDescPl[i]);
            addEquipmentDescriptionToDatabase(i+1,"en",cameraDescEn[i]);
        }
        //logEquipmentData(dbHelper.getAllEquipment());
        logEquipmentAndDesc();
    }
    public void setByLanguage() {
        switch (lan) {
            case 0:
                setPolish();
                updateMenuTitles(R.string.orderListPl, R.string.saveCartPl, R.string.aboutPl, R.string.shareCartPl, R.string.sendSmsPl,R.string.changeLanPl, R.string.saveStatePl, R.string.loadStatePl, R.string.createAccPl, R.string.clearSavedStatePl, R.string.sendMailPl);
                break;
            case 1:
                setEnglish();
                updateMenuTitles(R.string.orderListEn, R.string.saveCartEn, R.string.aboutEn, R.string.shareCartEn, R.string.sendSmsEn,R.string.changeLanEn, R.string.saveStateEn, R.string.loadStateEn, R.string.createAccEn, R.string.clearSavedStateEn, R.string.sendMailEn);
                break;
        }
    }

    private void updateMenuTitles(int orderListTitle, int saveCartTitle, int aboutTitle, int shareCartTitle, int sendSmsTitle,int changeLanTitle, int saveStateTitle, int loadStateTitle, int createAccTitle, int clearSavedStateTitle, int sendMailTitle) {
        if (menu != null) {
            if (orderList != null) {
                orderList.setTitle(orderListTitle);
            }
            if (saveCart != null) {
                saveCart.setTitle(saveCartTitle);
            }
            if (about != null) {
                about.setTitle(aboutTitle);
            }
            if (shareCart != null) {
                shareCart.setTitle(shareCartTitle);
            }
            if (sendSms != null) {
                sendSms.setTitle(sendSmsTitle);
            }
            if (changeLan != null) {
                changeLan.setTitle(changeLanTitle);
            }
            if (saveState != null) {
                saveState.setTitle(saveStateTitle);
            }
            if (loadState != null) {
                loadState.setTitle(loadStateTitle);
            }
            if (createAcc != null) {
                createAcc.setTitle(createAccTitle);
            }
            if (clearSavedState != null) {
                clearSavedState.setTitle(clearSavedStateTitle);
            }
            if (sendMail != null) {
                sendMail.setTitle(sendMailTitle);
            }
        }
    }

    private void setPolish() {
        extraLabel.setText(R.string.extraPl);
        mouseCheck.setTag(R.string.mouseCheckPl);
        keyboardCheck.setText(R.string.keyboardCheckPl);
        cameraCheck.setText(R.string.cameraCheckPl);
        priceLabel.setText(R.string.priceLabelPl);
        orderButton.setText(R.string.orderButPl);


        if (menu!=null){
        // 20 metod !!
            menuInnit(/*0*/);
        }
        loginOn();
        navHeader.setText(R.string.menuHeaderPl);
        navSubtext.setText(R.string.menuSubtextPl);


    }



    private void setEnglish() {
        extraLabel.setText(R.string.extraEn);
        mouseCheck.setText(R.string.mouseCheckEn);
        keyboardCheck.setText(R.string.keyboardCheckEn);
        cameraCheck.setText(R.string.cameraCheckEn);
        priceLabel.setText(R.string.priceLabelEn);
        orderButton.setText(R.string.orderButEn);
        if (menu!=null){
            menuInnit(/*1*/);
        }
        loginOn();
        navHeader.setText(R.string.menuHeaderEn);
        navSubtext.setText(R.string.menuSubtextEn);



    }
    private void menuInnit(/* short lan */) {
        switch (lan){
            case 0:
                orderList.setTitle(R.string.orderListPl);
                saveCart.setTitle(R.string.saveCartPl);
                about.setTitle(R.string.aboutPl);
                shareCart.setTitle(R.string.shareCartPl);
                sendSms.setTitle(R.string.sendSmsPl);
                break;
            case 1:
                orderList.setTitle(R.string.orderListEn);
                saveCart.setTitle(R.string.saveCartEn);
                about.setTitle(R.string.aboutEn);
                shareCart.setTitle(R.string.shareCartEn);
                sendSms.setTitle(R.string.sendSmsEn);
                break;
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu,menu);

        orderList = menu.findItem(R.id.orderList);
        saveCart = menu.findItem(R.id.saveCart);
        about = menu.findItem(R.id.about);
        shareCart = menu.findItem(R.id.shareCart);
        sendSms = menu.findItem(R.id.sendSms);
        menuInnit(/*lan*///);

    //     return super.onCreateOptionsMenu(menu);
    // }
    
    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu called");
        if (menu != null) {
            this.menu = menu;
            orderList = menu.findItem(R.id.orderList);
            saveCart = menu.findItem(R.id.saveCart);
            about = menu.findItem(R.id.about);
            shareCart = menu.findItem(R.id.shareCart);
            sendSms = menu.findItem(R.id.sendSms);

            // Check if items are found and log their current titles
            if (orderList != null) {
                Log.d(TAG, "Current title for orderList: " + orderList.getTitle());
                orderList.setTitle(R.string.orderListEn); // Update with your new string resource
            }
            if (saveCart != null) {
                Log.d(TAG, "Current title for saveCart: " + saveCart.getTitle());
                saveCart.setTitle(R.string.saveCartEn);   // Update with your new string resource
            }
            if (about != null) {
                Log.d(TAG, "Current title for about: " + about.getTitle());
                about.setTitle(R.string.aboutEn);          // Update with your new string resource
            }
            if (shareCart != null) {
                Log.d(TAG, "Current title for shareCart: " + shareCart.getTitle());
                shareCart.setTitle(R.string.saveCartEn);  // Update with your new string resource
            }
            if (sendSms != null) {
                Log.d(TAG, "Current title for sendSms: " + sendSms.getTitle());
                sendSms.setTitle(R.string.sendSmsEn);      // Update with your new string resource
            }
        }
        //menuInnit();
        return super.onPrepareOptionsMenu(menu);
    }
    */ //moge tak zrobic?
    

    private void logEquipmentData(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex("equipment_name");
                int typeIndex = cursor.getColumnIndex("equipment_type");
                int priceIndex = cursor.getColumnIndex("equipment_price");
                int idIndex = cursor.getColumnIndex("equipment_id");
                if (nameIndex != -1 && typeIndex != -1 && priceIndex != -1 && idIndex != -1) {
                    String eq_name = cursor.getString(nameIndex);
                    int eq_type = cursor.getInt(typeIndex);
                    float eq_price = cursor.getFloat(priceIndex);
                    float eq_id = cursor.getFloat(idIndex);

                    Log.d("EQ_LOG","\n id: "+ eq_id+ "\n name: " + eq_name + ", type: " + eq_type + ", price: " + eq_price);
                } else {
                    Log.v(TAG, "One or more columns do not exist in the cursor.");
                }
            } while (cursor.moveToNext());
        }
    }
    private void logEquipmentAndDesc(){
        Cursor cursor = dbHelper.getEquipmentWithDescription();
        if (cursor != null && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            Log.d(TAG, "Column names: " + Arrays.toString(columnNames));

            int idIndex = cursor.getColumnIndex("equipment_id");
            int nameIndex = cursor.getColumnIndex("equipment_name");
            int typeIndex = cursor.getColumnIndex("equipment_type");
            int priceIndex = cursor.getColumnIndex("equipment_price");
            int eqDescIndex = cursor.getColumnIndex(DatabaseHelper.DESC_DESCRIPTION);
            int langCodeIndex = cursor.getColumnIndex(DatabaseHelper.DESC_LANGUAGE_CODE);
            if (idIndex == -1 || nameIndex == -1 || typeIndex == -1 || priceIndex == -1 || eqDescIndex == -1 || langCodeIndex == -1) {
                Log.e(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA - Kolumny wybuchają");
                cursor.close();
                return;
            }

            do {
                String eq_name = cursor.getString(nameIndex);
                int eq_type = cursor.getInt(typeIndex);
                float eq_price = cursor.getFloat(priceIndex);
                float eq_id = cursor.getFloat(idIndex);
                String eq_lang_code = cursor.getString(langCodeIndex);
                String eq_desc = cursor.getString(eqDescIndex);
                Log.d("EquipmentDescLog", " --- \n ID: " + eq_id + ", \n Name: " + eq_name + ", \nType: " +eq_type +
                      ", \nPrice: " + eq_price + ", \nLanguage: " + eq_lang_code +
                      ", \nDescription: " + eq_desc + "\n ---");
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
    private void setAllSpinnerEquip() {
        pcOptions = new ArrayList<>();
        mouseOptions = new ArrayList<>();
        keyboardOptions = new ArrayList<>();
        cameraOptions = new ArrayList<>();
        Cursor cursor = dbHelper.getAllEquipment();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex("equipment_name");
                int typeIndex = cursor.getColumnIndex("equipment_type");
                int priceIndex = cursor.getColumnIndex("equipment_price");
                int idIndex = cursor.getColumnIndex("equipment_id");
                int imgIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_IMAGE);
                if (nameIndex != -1 && typeIndex != -1 && priceIndex != -1 && idIndex != -1) {
                    String eq_name = cursor.getString(nameIndex);
                    int eq_type = cursor.getInt(typeIndex);
                    float eq_price = cursor.getFloat(priceIndex);
                    float eq_id = cursor.getFloat(idIndex);
                    String eq_image = cursor.getString(imgIndex);

                    getSpinnerOption(eq_type, eq_name, eq_image);
                } else {
                    Log.v(TAG, "One or more columns do not exist in the cursor.");
                }
            } while (cursor.moveToNext());
        }
        innitSpinners();

    }

    private void innitSpinners() {
        ArrayAdapter<String> pcAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, pcOptions.stream().map(option -> option[0]).toArray(String[]::new));
        pcAdapter.setDropDownViewResource(R.layout.spinner_item);
        pcSpinner.setAdapter(pcAdapter);
        pcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedImage = pcOptions.get(position)[1];
                Log.v(TAG, selectedImage);
                pcImageView.setImageResource(getResources().getIdentifier(selectedImage, "drawable", getPackageName()));
                getState();
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> mouseAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mouseOptions.stream().map(option -> option[0]).toArray(String[]::new));
        mouseAdapter.setDropDownViewResource(R.layout.spinner_item);
        mouseSpinner.setAdapter(mouseAdapter);
        mouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedImage = mouseOptions.get(position)[1];
                Log.v(TAG, selectedImage);
                mouseImageView.setImageResource(getResources().getIdentifier(selectedImage, "drawable", getPackageName()));
                getState();
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> keyboardAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, keyboardOptions.stream().map(option -> option[0]).toArray(String[]::new));
        keyboardAdapter.setDropDownViewResource(R.layout.spinner_item);
        keyboardSpinner.setAdapter(keyboardAdapter);
        keyboardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedImage = keyboardOptions.get(position)[1];
                Log.v(TAG, selectedImage);
                keyboardImageView.setImageResource(getResources().getIdentifier(selectedImage, "drawable", getPackageName()));
                getState();
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize Camera Spinner
        ArrayAdapter<String> cameraAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, cameraOptions.stream().map(option -> option[0]).toArray(String[]::new));
        cameraAdapter.setDropDownViewResource(R.layout.spinner_item);
        cameraSpinner.setAdapter(cameraAdapter);
        cameraSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedImage = cameraOptions.get(position)[1];
                Log.v(TAG, selectedImage);
                cameraImageView.setImageResource(getResources().getIdentifier(selectedImage, "drawable", getPackageName()));
                getState();
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
            }
        });
    }
    // Not needed, may be useful later //
//    private void setSpinnerEquip(short type) {
//        Cursor cursor = dbHelper.getEquipmentByType(type);
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int nameIndex = cursor.getColumnIndex("equipment_name");
//                int typeIndex = cursor.getColumnIndex("equipment_type");
//                int priceIndex = cursor.getColumnIndex("equipment_price");
//                int idIndex = cursor.getColumnIndex("equipment_id");
//                int imgIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_IMAGE);
//                if (nameIndex != -1 && typeIndex != -1 && priceIndex != -1 && idIndex != -1) {
//                    String eq_name = cursor.getString(nameIndex);
//                    int eq_type = cursor.getInt(typeIndex);
//                    float eq_price = cursor.getFloat(priceIndex);
//                    float eq_id = cursor.getFloat(idIndex);
//                    String eq_image = cursor.getString(imgIndex);
//
//                    addSpinnerOption(eq_type,eq_name,eq_image);
//                } else {
//                    Log.v(TAG, "One or more columns do not exist in the cursor.");
//                }
//            } while (cursor.moveToNext());
//        }
//    }

    private void getSpinnerOption(int type, String name, String image) {
        Log.d(TAG, "getSpinnerOption - Input values: type=" + type + ", name=" + name + ", image=" + image);
        
        if (name == null) {
            Log.e(TAG, "Name is null, skipping");
            return;
        }
        
        // Get the price from the database
        float price = 0;
        Cursor cursor = dbHelper.getEquipmentByName(name);
        
        try {
            if (cursor != null) {
                Log.d(TAG, "Cursor obtained successfully");
                if (cursor.moveToFirst()) {
                    int priceIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_PRICE);
                    Log.d(TAG, "Price index: " + priceIndex);
                    if (priceIndex != -1) {
                        price = cursor.getFloat(priceIndex);
                        Log.d(TAG, "Price retrieved: " + price);
                    }
                } else {
                    Log.e(TAG, "Cursor is empty");
                }
            } else {
                Log.e(TAG, "Cursor is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing cursor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        
        // Combine name and price, with null check
        String displayText = (name != null ? name : "") + ", " + price + "zł";

        switch (type){
            case 0:
                pcOptions.add(new String[]{displayText, image});
                break;
            case 1:
                mouseOptions.add(new String[]{displayText, image});
                break;
            case 2:
                keyboardOptions.add(new String[]{displayText, image});
                break;
            case 3:
                cameraOptions.add(new String[]{displayText, image});
                break;
        }
    }
    public void getState() {
        // First check if pcSpinner has a selected item
        if (pcSpinner.getSelectedItem() == null) {
            Log.e(TAG, "No item selected in pcSpinner");
            return;
        }

        // Extract just the name from the selected item (remove price part)
        String selectedPcName = pcSpinner.getSelectedItem().toString().split(",")[0];
        
        Cursor cursor = dbHelper.getEquipmentByName(selectedPcName);
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_ID);
            if (idIndex != -1) {
                Log.d(TAG, String.valueOf(idIndex));
                if (cursor.moveToFirst()) {
                    orderState[0] = cursor.getInt(idIndex);
                } else {
                    Log.e(TAG, "Cursor empty for PC");
                    orderState[0] = null;
                }
                
                // Check mouse
                if (mouseCheck.isChecked() && mouseSpinner.getSelectedItem() != null) {
                    String selectedMouseName = mouseSpinner.getSelectedItem().toString().split(",")[0];
                    cursor = dbHelper.getEquipmentByName(selectedMouseName);
                    if (cursor.moveToFirst()) {
                        orderState[1] = cursor.getInt(idIndex);
                    } else {
                        orderState[1] = null;
                    }
                } else {
                    orderState[1] = null;
                }
                
                // Check keyboard
                if (keyboardCheck.isChecked() && keyboardSpinner.getSelectedItem() != null) {
                    String selectedKeyboardName = keyboardSpinner.getSelectedItem().toString().split(",")[0];
                    cursor = dbHelper.getEquipmentByName(selectedKeyboardName);
                    if (cursor.moveToFirst()) {
                        orderState[2] = cursor.getInt(idIndex);
                    } else {
                        orderState[2] = null;
                    }
                } else {
                    orderState[2] = null;
                }
                
                // Check camera
                if (cameraCheck.isChecked() && cameraSpinner.getSelectedItem() != null) {
                    String selectedCameraName = cameraSpinner.getSelectedItem().toString().split(",")[0];
                    cursor = dbHelper.getEquipmentByName(selectedCameraName);
                    if (cursor.moveToFirst()) {
                        orderState[3] = cursor.getInt(idIndex);
                    } else {
                        orderState[3] = null;
                    }
                } else {
                    orderState[3] = null;
                }
            }
            cursor.close();
        }
        Log.d(TAG, Arrays.toString(orderState));
    }
    public void settingOnChange(){
        mouseCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {getState(); updatePrice();});
        keyboardCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {getState(); updatePrice();});
        cameraCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {getState(); updatePrice();});
    }
    public void updatePrice() {
        Log.d(TAG, "Starting updatePrice()");
        float finalPrice = 0;

        if (orderState == null) {
            Log.e(TAG, "orderState is null");
            return;
        }

        Log.d(TAG, "Current orderState: " + Arrays.toString(orderState));

        for(Integer x : orderState) {
            if (x != null) {
                Log.d(TAG, "Processing equipment ID: " + x);
                Cursor cursor = dbHelper.getEquipmentById(x);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        int priceIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_PRICE);
                        if (priceIndex != -1) {
                            float itemPrice = cursor.getFloat(priceIndex);
                            finalPrice += itemPrice;
                            Log.d(TAG, "Added price: " + itemPrice + ", running total: " + finalPrice);
                        } else {
                            Log.e(TAG, "Price column not found in cursor");
                        }
                    } else {
                        Log.e(TAG, "Cursor is null or empty for equipment ID: " + x);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing equipment ID " + x + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
        
        String formattedPrice = String.format("%.2f", finalPrice);
        Log.d(TAG, "Final formatted price: " + formattedPrice);

        if (this.finalPrice == null) {
            Log.e(TAG, "finalPrice TextView is null");
            return;
        }

        try {
            this.finalPrice.setText(formattedPrice);
            Log.d(TAG, "Price successfully set to TextView");
        } catch (Exception e) {
            Log.e(TAG, "Error setting price to TextView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // --- SharedPreferences --- //
    private void loadStates(){
        orderState[0] = (sharedPreferences.getInt("pcState", -1) != -1) ? sharedPreferences.getInt("pcState", -1): null; 
        orderState[1] = (sharedPreferences.getInt("mouseState", -1) != -1) ? sharedPreferences.getInt("mouseState", -1): null;
        orderState[2] = (sharedPreferences.getInt("keyboardState", -1) != -1) ? sharedPreferences.getInt("keyboardState", -1): null;
        orderState[3] = (sharedPreferences.getInt("cameraState", -1) != -1) ? sharedPreferences.getInt("cameraState", -1): null;
        setBasedOnState();


        updatePrice();
    }
    private void saveStates(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pcState", orderState[0] != null ? orderState[0] : -1); 
        editor.putInt("mouseState", orderState[1] != null ? orderState[1] : -1); 
        editor.putInt("keyboardState", orderState[2] != null ? orderState[2] : -1); 
        editor.putInt("cameraState", orderState[3] != null ? orderState[3] : -1); 
        editor.apply();
    }

    public void setBasedOnState() {
        Spinner[] objects = {pcSpinner, mouseSpinner, keyboardSpinner, cameraSpinner};
        
        // Set checkboxes based on orderState
        mouseCheck.setChecked(orderState[1] != null);
        keyboardCheck.setChecked(orderState[2] != null);
        cameraCheck.setChecked(orderState[3] != null);
        
        // Set spinner selections based on orderState
        for (int i = 0; i < orderState.length; i++) {
            if (orderState[i] != null) {
                Cursor cursor = dbHelper.getEquipmentById(orderState[i]);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_NAME);
                        if (nameIndex != -1) {
                            String equipmentName = cursor.getString(nameIndex);
                            if (equipmentName != null) {
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) objects[i].getAdapter();
                                if (adapter != null) {
                                    for (int position = 0; position < adapter.getCount(); position++) {
                                        String item = adapter.getItem(position);
                                        if (item != null && item.startsWith(equipmentName)) {
                                            objects[i].setSelection(position);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }




    // --- Menu Methods --- //
    private void aboutMet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        switch (lan){
            case 0:
                builder.setTitle("O Autorze");
                builder.setMessage("Aplikacja została stworzona przez Piotra Schulz.\n\n" +
                        "Aplikacja obsługuje sklep internetowy.");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                break;
            case 1:
                builder.setTitle("About");
                builder.setMessage("This app was created by Piotr Schulz.\n\n" +
                        "The application supports an online store.");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                break;

            default:

        }
        builder.show();
    }

    // --- Intent Methods --- //

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        loginOn();
        super.onNewIntent(intent);
    }

    // --- Login methods --- //
    public void loginOff(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("myLogin",-1); //-1 meaning no account
        editor.apply();
        switch (lan){
            case 0:
                loginTV.setText(R.string.loggedOutPl);
                loginBut.setText(R.string.loginButtonPl);
                break;
            case 1:
                loginTV.setText(R.string.loggedOutEn);
                loginBut.setText(R.string.loginButtonEn);
                break;
            default:
                Log.e(TAG,"Bad lan ?");
        }

    }
    @SuppressLint("SetTextI18n")
    public void loginOn(){
        int id = sharedPreferences.getInt("myLogin",-1);
        if (id==-1){
            loginOff();
        }
        else {
            String login = dbHelper.getUserLoginById(id);
            switch (lan){
                case 0:
                    loginTV.setText(getString(R.string.loggedAsPl) + login);
                    loginBut.setText(R.string.logoutButtonPl);
                    break;
                case 1:
                    loginTV.setText(getString(R.string.loggedAsEn) + login);
                    loginBut.setText(R.string.logoutButtonEn);
                    break;
                default:
                    Log.e(TAG,"Bad lan ?");
            }
        }

    }

    private void shareOrder() {
        // Create content
        StringBuilder content = new StringBuilder();
        float totalPrice = 0;
        for (Integer equipId : orderState) {
            if (equipId != null) {
                Cursor cursor = dbHelper.getEquipmentById(equipId);
                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_NAME));
                    @SuppressLint("Range") float price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_PRICE));
                    totalPrice += price;
                    content.append(name)
                           .append(" - ")
                           .append(String.format("%.2f zł", price))
                           .append("\n");
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        
        // Add total price
        content.append("\nSuma: ").append(String.format("%.2f zł", totalPrice));
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share order via"));
        } else {
            showErrorDialog();
        }
    }

    private void sendOrderViaSms() {
        if (ContextCompat.checkSelfPermission(this, SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{SEND_SMS},
                1);
            return;
        }
        StringBuilder content = new StringBuilder();
        float totalPrice = 0;
        for (Integer equipId : orderState) {
            if (equipId != null) {
                Cursor cursor = dbHelper.getEquipmentById(equipId);
                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_NAME));
                    @SuppressLint("Range") float price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_PRICE));
                    totalPrice += price;
                    content.append(name)
                           .append(" - ")
                           .append(String.format("%.2f zł", price))
                           .append("\n");
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        
        // Add total price
        content.append("\nSuma: ").append(String.format("%.2f zł", totalPrice));

        // Show dialog to input phone number
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        switch (lan) {
            case 0: // Polish
                builder.setTitle("Wprowadź numer telefonu")
                      .setPositiveButton("Wyślij", (dialog, which) -> {
                          String phoneNumber = input.getText().toString();
                          sendSms(phoneNumber, content.toString());
                      })
                      .setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());
                break;
            case 1: // English
                builder.setTitle("Enter phone number")
                      .setPositiveButton("Send", (dialog, which) -> {
                          String phoneNumber = input.getText().toString();
                          sendSms(phoneNumber, content.toString());
                      })
                      .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                break;
        }
        builder.show();
    }

    private void sendSms(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            
            // Show success message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            switch (lan) {
                case 0: // Polish
                    builder.setTitle("Sukces")
                          .setMessage("SMS został wysłany")
                          .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    break;
                case 1: // English
                    builder.setTitle("Success")
                          .setMessage("SMS has been sent")
                          .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    break;
            }
            builder.show();
        } catch (Exception e) {
            // Show error message
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (lan) {
            case 0: // Polish
                builder.setTitle("Błąd")
                      .setMessage("Wystąpił błąd podczas wysyłania")
                      .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                break;
            case 1: // English
                builder.setTitle("Error")
                      .setMessage("An error occurred while sending")
                      .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                break;
        }
        builder.show();
    }

    private void sendEmailNotInBackground() {
        StringBuilder content = new StringBuilder();
        float totalPrice = 0;
        
        for (Integer equipId : orderState) {
            if (equipId != null) {
                Cursor cursor = dbHelper.getEquipmentById(equipId);
                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_NAME));
                    @SuppressLint("Range") float price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.EQUIPMENT_PRICE));
                    totalPrice += price;
                    content.append(name)
                           .append(" - ")
                           .append(String.format("%.2f zł", price))
                           .append("\n");
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        content.append("\nSuma: ").append(String.format("%.2f zł", totalPrice));
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"siezabije0@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order Details");
        emailIntent.putExtra(Intent.EXTRA_TEXT, content.toString());

        try {
            startActivity(emailIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            switch (lan) {
                case 0: // Polish
                    builder.setTitle("Błąd")
                          .setMessage("Nie znaleziono aplikacji email")
                          .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    break;
                case 1: // English
                    builder.setTitle("Error")
                          .setMessage("No email app found")
                          .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    break;
            }
            builder.show();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
//    private void createShortcuts() {
//        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
//        if (shortcutManager != null) {
//            List<ShortcutInfo> existingShortcuts = shortcutManager.getDynamicShortcuts();
//            if (existingShortcuts.size() < 3) {
//
//            if (existingShortcuts.size() < 3) { // Adjust based on your needs
//                ShortcutInfo logInShortcut = new ShortcutInfo.Builder(this, "log_in")
//                        .setShortLabel("Log In")
//                        .setLongLabel("Open Login Activity")
//                        .setIntent(new Intent(this, LoginActivity.class))
//                        .build();
//
//                ShortcutInfo checkOrdersShortcut = new ShortcutInfo.Builder(this, "check_orders")
//                        .setShortLabel("Check Orders")
//                        .setLongLabel("Open ListView Activity")
//                        .setIntent(new Intent(this, ListViewActivity.class))
//                        .build();
//
//                ShortcutInfo registerShortcut = new ShortcutInfo.Builder(this, "register")
//                        .setShortLabel("Register")
//                        .setLongLabel("Open Registration")
//                        .setIntent(new Intent(this, LoginActivity.class).putExtra("loginState", false))
//                        .build();
//
//                shortcutManager.setDynamicShortcuts(Arrays.asList(logInShortcut, checkOrdersShortcut, registerShortcut));
//                }
//            }
//        }
//    }
}

