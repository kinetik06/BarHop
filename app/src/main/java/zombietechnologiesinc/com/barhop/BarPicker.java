package zombietechnologiesinc.com.barhop;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by marcus on 8/3/16.
 */

public class BarPicker {

    private DatabaseReference databaseReference;
    private DatabaseReference mBarlist;
    ArrayList<Bar> mBars = new ArrayList<>();
    Boolean saved;

    public BarPicker(){

    }



    String [] bars = {
            "Cha Chas Cantina",
            "Tap House",
            "Society",
            "District 5",
            "Sticky Rice",
            "Baja Bean",
            "Sine",
            "Havana",
            "Plush"
    };





       /* private ArrayList<Bar> getBarList() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("bars");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bar bar = dataSnapshot.getValue(Bar.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);




        ArrayList <String> barList = new ArrayList<String>(Arrays.asList(bars));

        return barList;
    }*/
}
