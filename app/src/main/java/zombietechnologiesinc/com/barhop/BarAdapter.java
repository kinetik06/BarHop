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
import android.support.v4.content.ContextCompat;
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
import com.shuhart.stepview.StepView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    ArrayList<String> eventArrayList;
    ArrayList<DailySpecial> dailySpecialArrayList;
    DailySpecial dailySpecial;




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

        dailySpecialArrayList = new ArrayList<>();

        eventArrayList = new ArrayList<String>();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);



        bar=arrayOfBars.get(position);
        barName=bar.getBarName();
        //Log.d("bar names", bar.getBarName());
        context = holder.barNameTV.getContext();
        Typeface typeface = ResourcesCompat.getFont(context, R.font.geosanslight);
        holder.barNameTV.setTypeface(typeface);
        holder.barAddressTV.setTypeface(typeface);
        holder.barEventTV.setTypeface(typeface);
        holder.barCountTV.setTypeface(typeface);

        //Trying out Step View

        /*holder.stepView.getState()
                .selectedTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                .animationType(StepView.ANIMATION_CIRCLE)
                .selectedCircleColor(ContextCompat.getColor(context, R.color.colorAccent))
                .selectedCircleRadius(14)
                .selectedStepNumberColor(ContextCompat.getColor(context, R.color.colorPrimary))
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add("First step");
                    add("Second step");
                    add("Third step");
                }})
                // You should specify only steps number or steps array of strings.
                // In case you specify both steps array is chosen.
                .animationDuration(context.getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(1)
                .nextTextColor(context.getResources().getColor(R.color.main_top_grey))
                .textSize(14)
                .stepNumberTextSize(16)
                // other state methods are equal to the corresponding xml attributes
                .commit();

        holder.stepView.go(2, true);*/


        double percentage = (double) bar.getBarCount() / bar.getBarCap();
        holder.barNameTV.setText(bar.getBarName());
        holder.barAddressTV.setText(bar.getBarAddress());

        //Get Bar Event Array
        if (bar.getSpecialsArray() != null) {
            Log.d("Bar Specials : ", bar.getSpecialsArray().toString());
            eventArrayList = bar.getSpecialsArray();

            if (!eventArrayList.isEmpty() && eventArrayList != null) {

                Log.d("Day Of Week: ", dayOfTheWeek);

                switch (dayOfTheWeek) {

                    case "Monday":

                        holder.barEventTV.setText(eventArrayList.get(0));
                        break;

                    case "Tuesday":
                        holder.barEventTV.setText(eventArrayList.get(1));
                        break;

                    case "Wednesday":
                        holder.barEventTV.setText(eventArrayList.get(2));
                        break;

                    case "Thursday":
                        holder.barEventTV.setText(eventArrayList.get(3));
                        break;

                    case "Friday":
                        holder.barEventTV.setText(eventArrayList.get(4));
                        break;

                    case "Saturday":
                        holder.barEventTV.setText(eventArrayList.get(5));
                        break;

                    case "Sunday":
                        holder.barEventTV.setText(eventArrayList.get(6));
                        break;

                    default:
                        holder.barEventTV.setText(bar.getBarEvent());
                        break;


                }

            }
        }


        //Start Daily Special Code

        //TODO Bars are open past midnight -- current code pulls sunday event into saturday

        if (bar.getDailySpecialArrayList() != null) {

            dailySpecialArrayList = bar.getDailySpecialArrayList();
            if (dailySpecialArrayList.size() > 7){
                ArrayList<DailySpecial> updateList = new ArrayList<DailySpecial>(dailySpecialArrayList.subList(0,7));
                dailySpecialArrayList = updateList;
            }

            for (int i = 0; i < dailySpecialArrayList.size(); i++) {

                dailySpecial = dailySpecialArrayList.get(i);
                /*Log.d("DAILY SPECIALS INT", String.valueOf(dailySpecial.getDayInt()));
                Log.d("Daily Specials: ", dailySpecial.getMessage());
                Log.d("Array SIze: ", String.valueOf(dailySpecialArrayList.size()));
                Log.d("Day Of the Week: ", dayOfTheWeek);*/

                if (Objects.equals(dailySpecial.getDateAsString(), dayOfTheWeek)) {
                    /*Log.d("DAY OF WEEK: ", dayOfTheWeek);
                    Log.d("DAILY SPECIAL DAY: ", dailySpecial.getDateAsString());
                    Log.d("ACTUAL DAY SPECIAL: ", dailySpecial.getMessage());*/
                    holder.barEventTV.setText(dailySpecial.getMessage());
                    if (Objects.equals(dailySpecial.getMessage(), "Tap to edit special")){
                        holder.barEventTV.setText("");
                    }

                }
            }
        }


        //TODO make the master break code holder.barEventTV.setText(bar.getBarEvent());
        double progress = percentage * 100;
        holder.barCountTV.setText(String.valueOf(percentage * 100).substring(0, 3) + "%");
        if (holder.barCountTV.getText().charAt(2) == '.'){
            holder.barCountTV.setText(String.valueOf(percentage * 100).substring(0, 2) + "%");

        }

        holder.barCap.setProgress((int) progress);
        //Log.d("PROGRESS BAR:", String.valueOf(progress));
        //Log.d ("other stuff", String.valueOf(holder.barCountTV.getText().charAt(2)));
        //Log.d("STUFFFFFFFF", holder.barCountTV.getText().toString());
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