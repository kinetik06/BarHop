package zombietechnologiesinc.com.barhop;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

public class BarDetailsActivity extends FragmentActivity
        implements OnMapReadyCallback {
    private TextView barNameTV;
    private String mBarUserId;
    private DatabaseReference mDatabaseReference;
    private TextView barAddressTV;
    private MapFragment mMapFragment;
    private LatLng barLatLng;
    private static final String TAG = BarDetailsActivity.class.getSimpleName();
    private UiSettings mUiSettings;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private RideRequestButton mRequestButton;
    private SessionConfiguration mSessionConfiguration;
    private ServerTokenSession mServerTokenSession;
    private double userLat;
    private double userLong;
    private RideParameters mRideParameters;
    private String mBarKey;
    private Location mLocation;
    private int LOCATION_PERMISSION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_details);
        // set up uber

        mSessionConfiguration = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("9cQ1Ivem4wS92EGh2K2t34-hukR_ztL-")
                // required for enhanced button features
                .setServerToken("0pfd1-hwkmqkqUNSHuyOqwvF_UGy2bjy3iamKc7E")
                // required for implicit grant authentication
                .setRedirectUri("")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(mSessionConfiguration);
        mServerTokenSession = new ServerTokenSession(mSessionConfiguration);



        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        barNameTV = (TextView) findViewById(R.id.bar_name);
        barAddressTV = (TextView) findViewById(R.id.bar_address);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        mBarUserId = intent.getStringExtra("barpick");
        userLat = intent.getDoubleExtra("userLat", 1);
        userLong = intent.getDoubleExtra("userLong", 1);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("bars").child(mBarUserId);


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "BARKEY:" + mBarKey);
                Bar bar = dataSnapshot.getValue(Bar.class);
                barNameTV.setText(bar.getBarName());
                barAddressTV.setText(bar.getBarAddress());
                barLatLng = new LatLng(bar.getLatitude(), bar.getLongitude());
                mUiSettings = googleMap.getUiSettings();
                mUiSettings.setAllGesturesEnabled(false);
                mUiSettings.setCompassEnabled(false);
                googleMap.addMarker(new MarkerOptions().position(barLatLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barLatLng, 15));

                //set up uber stuff
                mRequestButton = new RideRequestButton(BarDetailsActivity.this);

                mRequestButton.setSession(mServerTokenSession);

                mLinearLayout.addView(mRequestButton);
                if (userLat == 1 || userLong == 1) {
                    mRideParameters = new RideParameters.Builder()
                            .setPickupToMyLocation()
                            .setDropoffLocation(bar.getLatitude(),bar.getLongitude(),bar.getBarName(),bar.getBarAddress())
                            .build();
                }else {

                    mRideParameters = new RideParameters.Builder()
                            .setPickupLocation(userLat,userLong,"Here","Here")
                            .setDropoffLocation(bar.getLatitude(), bar.getLongitude(), bar.getBarName(), bar.getBarAddress())
                            .build();
                }
                mRequestButton.setRideParameters(mRideParameters);
                mRequestButton.loadRideInformation();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addValueEventListener(valueEventListener);

        mProgressBar.setVisibility(View.INVISIBLE);


    }


}
