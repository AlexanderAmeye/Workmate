package me.example.paul.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.example.paul.CardAdapter;
import me.example.paul.Model.Reward;
import me.example.paul.R;


public class Card extends Fragment {
    private CardView cardView;
    private TextView reward_title;
    private TextView reward_description;
    private ImageView reward_icon;
    private TextView reward_price;


    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);
        reward_title = view.findViewById(R.id.reward_title);
        reward_description = view.findViewById(R.id.reward_description);
        reward_icon = view.findViewById(R.id.reward_icon);
        reward_price = view.findViewById(R.id.reward_price);
        cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setMaxCardElevation(cardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return cardView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Reward r_data = (Reward) getArguments().getSerializable("data");
        reward_title.setText(Html.fromHtml(r_data.getTitle()));
        reward_description.setText(Html.fromHtml(r_data.getDescription()));
        reward_icon.setImageResource(r_data.getIcon());
        reward_price.setText(Html.fromHtml(r_data.getPrice()));
    }
}