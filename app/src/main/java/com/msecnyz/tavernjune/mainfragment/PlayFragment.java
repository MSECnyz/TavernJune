package com.msecnyz.tavernjune.mainfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.msecnyz.tavernjune.R;
import com.msecnyz.tavernjune.onuwerewolf.FirstwwActivity;

public class PlayFragment extends Fragment {

    CardView uWolfCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If false, root is only used to create the correct subclass of LayoutParams for the root view in the XML.
        View v = inflater.inflate(R.layout.fragment_play, container, false);

        uWolfCard = (CardView)v.findViewById(R.id.card_uw);
        uWolfCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PlayFragment.this.getActivity(), FirstwwActivity.class);
                PlayFragment.this.getActivity().startActivity(intent);
            }
        });

        return v;
    }
}
