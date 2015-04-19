package org.pushla.tes.tespushla;

/**
 * Created by Laila L on 29/03/2015.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_menu_list_item, null);
        }
        TextView txt = (TextView) convertView.findViewById(R.id.txt);
        txt.setText(navDrawerItems.get(position).getTitle());

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        imgIcon.setBackgroundResource(navDrawerItems.get(position).getIcon());


        //TO-DO: Buat Profil
        if (position == 0) {
            convertView.setMinimumHeight(200);
            txt.setTypeface(null, Typeface.BOLD);
//            imgIcon.getLayoutParams().height = 250;
        }

        return convertView;
    }

    public boolean isEnabled(int position) {
        if(position == 0) {
            return false;
        }
        return true;
    }

}

