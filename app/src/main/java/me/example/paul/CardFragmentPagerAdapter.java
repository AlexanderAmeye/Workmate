package me.example.paul;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.example.paul.Fragments.Card;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private final ArrayList<Card> fragments;
    private LayoutInflater inflater;
    private float baseElevation = 1.5f;

    public CardFragmentPagerAdapter(FragmentManager fm, ArrayList<Card> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    public int getPosition(Fragment frag)
    {
        return this.fragments.indexOf(frag);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (Card) fragment);

        return fragment;
    }

    public void addCardFragment(Card fragment) {
        fragments.add(fragment);
    }

}