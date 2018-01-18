package zombietechnologiesinc.com.barhop;

import android.app.FragmentManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity implements BarHopFragment.OnFragmentInteractionListener, UserMenuFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        BarHopFragment barHopFragment = new BarHopFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_barhop, barHopFragment).commit();

        UserMenuFragment userMenuFragment = new UserMenuFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_user_menu, userMenuFragment).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
