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
import android.widget.ListView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class BarHopActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        checkPermission();
        requestPermission();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        barhopTV1 = (TextView)findViewById(R.id.barhopTV1);
        barhopTV2 = (TextView)findViewById(R.id.barhopTV2);
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
                            mLinearLayoutManager = new LinearLayoutManager(context);
                            mLinearLayoutManager.setStackFromEnd(false);
                            mRecyclerView.setLayoutManager(mLinearLayoutManager);
                            mRecyclerView.setAdapter(nearestBars);

                            ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                                    new ItemClickSupport.OnItemClickListener() {
                                        @Override
                                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                            Bar bar = arrayOfBars.get(position);
                                            Toast.makeText(BarHopActivity.this, bar.getBarName(), Toast.LENGTH_SHORT).show();
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


}
