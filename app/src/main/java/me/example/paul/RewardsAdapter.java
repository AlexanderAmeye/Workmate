package me.example.paul;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.example.paul.Model.Reward;

public class RewardsAdapter extends BaseAdapter {

    private final Context mContext;
    private final Reward[] rewards;

    public RewardsAdapter(Context context, Reward[] books) {
        this.mContext = context;
        this.rewards = books;
    }

    @Override
    public int getCount() {
        return rewards.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final Reward reward = rewards[position];

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.linearlayout_reward, null);
        }

        // 3
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_book_name);
        final TextView authorTextView = (TextView)convertView.findViewById(R.id.textview_book_author);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        // 4
        imageView.setImageResource(reward.getImageResource());
        nameTextView.setText(reward.getName());
        authorTextView.setText(reward.getDescription());

        return convertView;
    }


}

