package zombietechnologiesinc.com.barhop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 10/13/2016.
 */

public class BarAdapter extends RecyclerView.Adapter<BarViewHolder> {

    private LayoutInflater inflater;
    ArrayList<Bar> arrayOfBars;
    Bar bar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://bar-hop-b83f2.appspot.com");
    private String profilePic = "mUpdatePic.jpg";
    private String barName;



    public Bar getBar(int position) {
        return arrayOfBars.get(position);
    }

    public BarAdapter(Context context,ArrayList<Bar> arrayOfBars) {

        inflater=LayoutInflater.from(context);
        this.arrayOfBars=arrayOfBars;
        bar= new Bar();
    }
    @Override
    public BarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_message,parent,false);
        BarViewHolder holder=new BarViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BarViewHolder holder, int position) {
        bar=arrayOfBars.get(position);
        barName=bar.getBarName();

        double percentage = (double) bar.getBarCount() / bar.getBarCap();
        holder.barNameTV.setText(bar.getBarName());
        holder.barAddressTV.setText(bar.getBarAddress());
        holder.barEventTV.setText(bar.getBarEvent());
        double progress = percentage * 100;
        holder.barCountTV.setText(String.valueOf(percentage * 100).substring(0, 3) + "%");
        if (holder.barCountTV.getText().charAt(2) == '.'){
            holder.barCountTV.setText(String.valueOf(percentage * 100).substring(0, 2) + "%");

        }
        holder.barCap.setProgress((int) progress);
        Log.d("PROGRESS BAR:", String.valueOf(progress));
        Log.d ("other stuff", String.valueOf(holder.barCountTV.getText().charAt(2)));
        Log.d("STUFFFFFFFF", holder.barCountTV.getText().toString());
        final StorageReference profilePicRef = storageRef.child(bar.getUserId()).child(profilePic);
        //Get Bar Image
        final long ONE_MEGABYTE = 1024 * 1024;
        profilePicRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "profilepic.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.barLogo.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                holder.barLogo.setImageResource(R.drawable.ic_account_circle_black_36dp);
            }
        });




    }

    @Override
    public int getItemCount() {
        return arrayOfBars.size();
    }






}