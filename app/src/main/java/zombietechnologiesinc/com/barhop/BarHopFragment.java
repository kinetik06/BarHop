package zombietechnologiesinc.com.barhop;

import android.*;
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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BarHopFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BarHopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarHopFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BarHopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarHopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarHopFragment newInstance(String param1, String param2) {
        BarHopFragment fragment = new BarHopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        context=getContext();
        checkPermission();
        requestPermission();
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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


        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);



        //set up google api for location
     /*   if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(context)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }*/


        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //user auth state changed, launch login activity
                    startActivity(new Intent(context, LoginActivity.class));

                }
            }
        };



        barhopTV1 = (TextView)getView().findViewById(R.id.barhopTV1);
        barhopTV2 = (TextView)getView().findViewById(R.id.barhopTV2);
        danceClubTV = (TextView)getView().findViewById(R.id.dance_clubTV);
        typeface = ResourcesCompat.getFont(context, R.font.geosanslight);
        Typeface geosansBold = Typeface.create(typeface, Typeface.BOLD);
        barhopTV1.setTypeface(geosansBold);
        barhopTV2.setTypeface(geosansBold);

        mBarlist = FirebaseDatabase.getInstance().getReference().child("bars");
        //Explain to the user how barhop works. Puts together the dialog to show the user
        AlertDialog.Builder firstUseBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
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
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.barRecyclerView);


        //instantiate Bar list
        //new child entries
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();



        return inflater.inflate(R.layout.fragment_bar_hop, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                                                                          //Toast.makeText(BarHopActivity.this, bar.getBarName(), Toast.LENGTH_SHORT).show();
                                            /*new StyleableToast.Builder(BarHopActivity.this).text(bar.getBarName()).textColor(getResources()
                                                    .getColor(R.color.white))
                                                    .backgroundColor(getResources().getColor(R.color.main_top_grey)).show();*/

                                                                          //StyleableToast.makeText(BarHopActivity.this, bar.getBarName(), R.style.mytoast).show();
                                                                          String barPickId = bar.getUserId();

                                                                          Intent intent = new Intent(context, BarDetailsActivity.class);
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
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
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
        VersionName = BarHopActivity.getVersionName(context, BarHopActivity.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        StoredVersionname = (prefs.getString("versionname", null));
        if (StoredVersionname == null || StoredVersionname.length() == 0){
            FirstRunDialog.show();
        }
        prefs.edit().putString("versionname", VersionName).commit();
    }



}
