package zombietechnologiesinc.com.barhop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
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
    Context context;




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
        context = holder.barNameTV.getContext();
        Typeface typeface = ResourcesCompat.getFont(context, R.font.geosanslight);
        holder.barNameTV.setTypeface(typeface);
        holder.barAddressTV.setTypeface(typeface);
        holder.barEventTV.setTypeface(typeface);
        holder.barCountTV.setTypeface(typeface);

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
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(null, bitmap);
                drawable.setCornerRadius(20);
                Bitmap roundedProfilePic = getRoundedCornerBitmap(bitmap);

                holder.barLogo.setImageDrawable(drawable);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                holder.barLogo.setImageResource(R.drawable.rectangle_placeholder);
            }
        });




    }

    @Override
    public int getItemCount() {
        return arrayOfBars.size();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }





}