package zombietechnologiesinc.com.barhop;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class BarHopActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PopupMenu.OnMenuItemClickListener {


    private static final String TAG = BarHopActivity.class.getSimpleName();
    TextView titleTV;
    Context context;
    private String userName;
    private DatabaseReference mBarlist;
    private ListView barListLV;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Bar, BarViewHolder> mFirebaseRecyclerAdapter;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double userLat;
    private double userLong;
    private String barKey;
    private RecyclerView.Adapter nearestBars;
    private ArrayList<Bar> arrayOfBars;
    private DatabaseReference tempDataRef;
    private ArrayList<String> arrayOfKeys;
    private ArrayList<String> barUserNameArray;
    private int counter;
    private int size;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    String StoredVersionname = "";
    String VersionName;
    AlertDialog LoginDialog;
    AlertDialog UpdateDialog;
    AlertDialog FirstRunDialog;
    SharedPreferences prefs;
    Typeface typeface;
    TextView barhopTV1;
    TextView barhopTV2;
    TextView danceClubTV;
    private PopupMenu popupMenu;
    ArrayList<Bar> danceBarsList;
    ArrayList<DailySpecial> dailySpecialArrayList;
    LinearLayout ballLayout;
    @BindView(R.id.micLayout)LinearLayout micLayout;
    @BindView(R.id.guitarLayout)LinearLayout guitarLayout;
    @BindView(R.id.mugLayout)LinearLayout mugLayout;
    @BindView(R.id.wineLayout)LinearLayout wineLayout;
    ArrayList<Bar> trimList;
    int dayOfWeekInt = 0;
    String dayOfTheWeek = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        dayOfTheWeek = sdf.format(d);
        getDayOfWeekInt(dayOfTheWeek);
        trimList = new ArrayList<>();



        checkPermission();
        requestPermission();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        dailySpecialArrayList = new ArrayList<>();
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLat = location.getLatitude();
                userLong = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates("gps", 10000, 0, mLocationListener);

        //get firebase auth instance
        Log.d("lifecycle","OnCreateCalled");
        auth = FirebaseAuth.getInstance();
        barUserNameArray = new ArrayList<String>();
        danceBarsList = new ArrayList<>();


        //set up google api for location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }


        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //user auth state changed, launch login activity
                    startActivity(new Intent(BarHopActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        setContentView(R.layout.activity_bar_hop);
        ButterKnife.bind(this);
        ballLayout = findViewById(R.id.ballLayout);
        barhopTV1 = (TextView)findViewById(R.id.barhopTV1);
        barhopTV2 = (TextView)findViewById(R.id.barhopTV2);
        danceClubTV = (TextView)findViewById(R.id.dance_clubTV);
        trimList = new ArrayList<>();
        //Pop Up Menu

        popupMenu = new PopupMenu(this, findViewById(R.id.menu_iconIV));
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Log Out");
        popupMenu.setOnMenuItemClickListener(this);
        findViewById(R.id.menu_iconIV).setOnClickListener(this);




        typeface = ResourcesCompat.getFont(this, R.font.geosanslight);
        Typeface geosansBold = Typeface.create(typeface, Typeface.BOLD);
        barhopTV1.setTypeface(geosansBold);
        barhopTV2.setTypeface(geosansBold);

        mBarlist = FirebaseDatabase.getInstance().getReference().child("bars");
        //Explain to the user how barhop works. Puts together the dialog to show the user
        AlertDialog.Builder firstUseBuilder = new AlertDialog.Builder(new ContextThemeWrapper(BarHopActivity.this, R.style.AlertDialogCustom));
        firstUseBuilder.setMessage(getString(R.string.second_message))
                .setTitle("Welcome to BarHop")
                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        FirstRunDialog = firstUseBuilder.create();
        FirstRunDialog.show();
        CheckFirstRun();
        Log.d("MESSAGE: ", getString(R.string.second_message));
        userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //set up array adapter

        arrayOfBars = new ArrayList<>();
        arrayOfKeys= new ArrayList<>();
        counter=0;
        size=0;

        //attach adapter to listview
 /*       ListView listView = (ListView) findViewById(R.id.barLV);
        listView.setAdapter(nearestBars);*/

        //Toast.makeText(BarHopActivity.this, "Welcome Back " + userName, Toast.LENGTH_SHORT).show();
        // setUpFirebase();

        //set fonts
        /*titleTV = (TextView) findViewById(R.id.titleTV);
        final Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Metropolis-Black.otf");
        titleTV.setTypeface(font);*/


//        //set listview
//        barListLV = (ListView) findViewById(R.id.barsLV);

        //Initialize recycler view
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.barRecyclerView);


        //instantiate Bar list
        //new child entries
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        checkPermission();
        requestPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar_hop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("lifecycle", "onConnected called");
        counter=0;
        size=0;
        checkPermission();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null && userLong == 0 && userLat == 0 ) {
            userLat = mLastLocation.getLatitude();
            userLong = mLastLocation.getLongitude();
        }
        Log.d(TAG, "LOCATION:" + userLat + "," + userLong);
        GeoFire geoFire = new GeoFire(mDatabaseReference.child("bars_location"));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(userLat, userLong), 40);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("LOCATION:", String.valueOf(userLat) + "," + String.valueOf(userLong));
                Log.d("Lifecycle","Onkeyentered");
                Log.d("LIST OF BARS",key);
                size++;
                tempDataRef = FirebaseDatabase.getInstance().getReference("/bars/"+key);
/*
                ballLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Query danceClubQuery = tempDataRef.child("dailySpecialArrayList").child(String.valueOf(dayOfWeekInt)).child("genreInt").equalTo("1");
                        danceClubQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){

                                    for (DataSnapshot danceSnap : dataSnapshot.getChildren()){
                                        Log.d(TAG, String.valueOf(danceSnap.getChildrenCount()));

                                    }


                                }else {
                                    Log.d(TAG, "Nothing Here");
                                    Log.d(TAG, danceClubQuery.toString());
                                    Log.d(TAG, tempDataRef.toString() + " " + String.valueOf(dayOfWeekInt));


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        for (int i = 0; i < arrayOfBars.size(); i++){

                            Log.d(TAG, "Touched");

                            Bar bar = arrayOfBars.get(i);
                            if (bar.getDailySpecialArrayList() != null) {
                                dailySpecialArrayList = bar.getDailySpecialArrayList();
                                for (int j = 0; j < dailySpecialArrayList.size(); j++) {
                                    DailySpecial dailySpecial = dailySpecialArrayList.get(j);
                                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                                    Date d = new Date();
                                    String dayOfTheWeek = sdf.format(d);
                                    if (Objects.equals(dailySpecial.getDateAsString(), dayOfTheWeek)) {

                                        if (dailySpecial.getGenreInt() == 1) {

                                            danceBarsList.add(bar);

                                            Log.d(TAG, String.valueOf(danceBarsList.size()));
                                        }

                                    }
                                }
                            }
                        }

                        arrayOfBars.clear();
                        arrayOfBars.addAll(danceBarsList);
                        nearestBars.notifyDataSetChanged();

                    }



                });*/


                Log.d(TAG, "THIS IS THE REFERENCE AFTER ON KEY ENTERED:" + tempDataRef);
                ValueEventListener barListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("Lifecycle","onDataChange inside onKeyEntered called");
                        if(arrayOfBars.isEmpty()||arrayOfBars.size()<size)
                        {
                            arrayOfBars.add(counter,dataSnapshot.getValue(Bar.class));
                            arrayOfKeys.add(counter,dataSnapshot.getKey());
                            counter++;

                            ballLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Iterator<Bar> iterator = arrayOfBars.iterator();
                                    trimList.addAll(arrayOfBars);

                                    while (iterator.hasNext()){
                                        Bar bar = iterator.next();

                                        if (bar.getDailySpecialArrayList() == null || bar.getDailySpecialArrayList().get(dayOfWeekInt).getGenreInt() != 1 ){
                                            iterator.remove();
                                            counter = arrayOfBars.size();
                                            nearestBars.notifyDataSetChanged();

                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            for(int i=0;i<size;i++)
                            {
                                if(arrayOfKeys.get(i)==dataSnapshot.getKey())
                                {
                                    arrayOfBars.set(i,dataSnapshot.getValue(Bar.class));
                                    Log.d("whatsupset",arrayOfBars.get(i).getBarName()+i);
                                    nearestBars.notifyDataSetChanged();
                                }
                            }

                        }
                        mProgressBar.setVisibility(View.INVISIBLE);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                tempDataRef.addValueEventListener(barListener);

            }

                @Override
                public void onKeyExited (String key){

                    size--;

                }

                @Override
                public void onKeyMoved (String key, GeoLocation location){

                }

                @Override
                public void onGeoQueryReady () {
                    Log.d(TAG, "THIS IS THE VERICATION OF PERMISSION" + PERMISSION_REQUEST_CODE);
                    Log.d(TAG, "THIS IS THE REFERENCE:" + tempDataRef);
                    if (tempDataRef == null) {
                        tempDataRef = FirebaseDatabase.getInstance().getReference("/bars/MPNUp9NEb4TklT8kM5MxNke8v1K3");
                    }
                    tempDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("lifecycle","OnGeoQueryReadyCalled inside ondatachanged called");
                            Log.d("array",String.valueOf(arrayOfBars.size()));
                            nearestBars = new BarAdapter(context, arrayOfBars);

                            // Sort by Bar Genre








                            mLinearLayoutManager = new LinearLayoutManager(context);
                            mLinearLayoutManager.setStackFromEnd(false);
                            mRecyclerView.setLayoutManager(mLinearLayoutManager);
                            mRecyclerView.setAdapter(nearestBars);

                            ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                                    new ItemClickSupport.OnItemClickListener() {
                                        @Override
                                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                            Bar bar = arrayOfBars.get(position);
                                            //Toast.makeText(BarHopActivity.this, bar.getBarName(), Toast.LENGTH_SHORT).show();
                                            /*new StyleableToast.Builder(BarHopActivity.this).text(bar.getBarName()).textColor(getResources()
                                                    .getColor(R.color.white))
                                                    .backgroundColor(getResources().getColor(R.color.main_top_grey)).show();*/

                                            //StyleableToast.makeText(BarHopActivity.this, bar.getBarName(), R.style.mytoast).show();
                                            String barPickId = bar.getUserId();

                                            Intent intent = new Intent(BarHopActivity.this, BarDetailsActivity.class);
                                            intent.putExtra("barpick", barPickId);
                                            intent.putExtra("userLat", userLat);
                                            intent.putExtra("userLong", userLong);
                                            intent.putExtra("barKey", barKey);
                                            startActivity(intent);
                                        }
                                    }
                            );


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onGeoQueryError (DatabaseError error){
                    Log.d("lifecycle","OnGeoQueryCalled");
                }
            }

            );
        }

        @Override
        public void onConnectionSuspended ( int i){

        }

        @Override
        public void onConnectionFailed (@NonNull ConnectionResult connectionResult){

        }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(BarHopActivity.this,"Permission Granted, Now you can access location data.",Toast.LENGTH_SHORT).show();

                } else {

                    //Toast.makeText(BarHopActivity.this,"Permission Denied, You cannot access location data.",Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(BarHopActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(BarHopActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    public static String getVersionName(Context context, Class cls) {
        try {
            ComponentName comp = new ComponentName(context, cls);
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(
                    comp.getPackageName(), 0);
            return "Version: " + pinfo.versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public void CheckFirstRun() {
        VersionName = BarHopActivity.getVersionName(this, BarHopActivity.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StoredVersionname = (prefs.getString("versionname", null));
        if (StoredVersionname == null || StoredVersionname.length() == 0){
            FirstRunDialog.show();
        }
        prefs.edit().putString("versionname", VersionName).commit();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case 1:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(BarHopActivity.this, LoginActivity.class);
                startActivity(intent);

        }


        return false;
    }

    @Override
    public void onClick(View view) {

        popupMenu.show();



    }

    public int getDayOfWeekInt(String dayOfTheWeek){

        switch (dayOfTheWeek){


            case "Monday":

                dayOfWeekInt = 0;
                break;

            case "Tuesday":
                dayOfWeekInt = 1;
                break;

            case "Wednesday":
                dayOfWeekInt = 2;
                break;

            case "Thursday":
                dayOfWeekInt = 3;
                break;

            case "Friday":
                dayOfWeekInt = 4;
                break;

            case "Saturday":
                dayOfWeekInt = 5;
                break;

            case "Sunday":
                dayOfWeekInt = 6;
                break;

            default:
                break;


        }
        return dayOfWeekInt;

    }

    public boolean isPressed (LinearLayout layout){

        return false;

    }

}
