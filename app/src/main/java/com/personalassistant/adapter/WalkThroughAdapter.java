package com.personalassistant.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.personalassistant.R;
import com.personalassistant.enities.WalkThrough;

import java.util.ArrayList;

public class WalkThroughAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<WalkThrough> walkthrough = new ArrayList<>();

    public WalkThroughAdapter(Context context, ArrayList<WalkThrough> walkthrough) {
        this.context = context;
        this.walkthrough = walkthrough;
    }

    @Override
    public int getCount() {
        return walkthrough.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_walk_through_layout, container, false);
        TextView heading = itemView.findViewById(R.id.walk_through_header_tv);
        TextView bottom = itemView.findViewById(R.id.walk_through_footer_tv);
        ImageView imageView = itemView.findViewById(R.id.walk_through_iv);
        imageView.setImageResource(walkthrough.get(position).getImageResource());

        if (position == 0) {
            heading.setTextColor(context.getResources().getColor(R.color.md_purple_A400));
        }

        if (position == 2) {
            heading.setTextColor(context.getResources().getColor(R.color.orange));
        }

        heading.setText(walkthrough.get(position).getTittle());
        bottom.setText(walkthrough.get(position).getDescription());
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
