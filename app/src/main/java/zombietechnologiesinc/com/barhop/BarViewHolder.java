package zombietechnologiesinc.com.barhop;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shuhart.stepview.StepView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 10/13/2016.
 */

public class BarViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    public TextView barNameTV;
    public TextView barAddressTV;
    public ImageView barLogo;
    public TextView barCountTV;
    public TextView barEventTV;
    public ProgressBar barCap;
    public StepView stepView;
    public TextView lowCapTV;
    public TextView medCapTV;
    public TextView highCapTV;

    public BarViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        barCap = (ProgressBar)itemView.findViewById(R.id.capacity_progress);
        barNameTV = (TextView) itemView.findViewById(R.id.messageTextView);
        barAddressTV = (TextView) itemView.findViewById(R.id.messengerTextView);
        barLogo = (ImageView) itemView.findViewById(R.id.messengerImageView);
        barCountTV = (TextView) itemView.findViewById(R.id.bar_count);
        barEventTV = (TextView) itemView.findViewById(R.id.bar_event_tv);
        lowCapTV = (TextView) itemView.findViewById(R.id.cap_lowTV);
        medCapTV = (TextView) itemView.findViewById(R.id.cap_medTV);
        highCapTV = (TextView) itemView.findViewById(R.id.cap_highTV);
        //stepView = (StepView) itemView.findViewById(R.id.stepViewPB);



    }
}
