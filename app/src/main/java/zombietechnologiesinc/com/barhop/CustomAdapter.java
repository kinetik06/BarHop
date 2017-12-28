package zombietechnologiesinc.com.barhop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by User on 9/27/2016.
 */

public class CustomAdapter extends BaseAdapter{
    Context mContext;
    ArrayList<Bar> mBars;

    public CustomAdapter(Context context, ArrayList<Bar> bars) {
        mContext = context;
        mBars = bars;
    }

    @Override
    public int getCount() {
        return mBars.size();
    }

    @Override
    public Object getItem(int position) {
        return mBars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}
