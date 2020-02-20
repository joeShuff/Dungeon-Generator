package com.joeshuff.dddungeongenerator.screens;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.generator.monsters.Action;

import java.util.ArrayList;
import java.util.List;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    List<Action> actions = new ArrayList<>();

    public ActionAdapter(List<Action> actions) {
        if (actions == null) return;
        this.actions = actions;
    }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ActionViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.monster_special_actions, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewHolder actionViewHolder, int i) {
        Action a = actions.get(i);
        actionViewHolder.text.setText(Html.fromHtml("<b><i>" + a.getName() + "</i></b>    " + a.getDesc()));
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public static class ActionViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public ActionViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.specialAction);
        }
    }

}
