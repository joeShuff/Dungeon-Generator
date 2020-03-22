package com.joeshuff.dddungeongenerator.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.util.FirebaseTracker;
import com.joeshuff.dddungeongenerator.GeneratingActivity;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.memory.MemoryController;
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration;
import org.json.JSONException;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.PreviousSearchViewHolder> {

    List<MemoryGeneration> memories;

    public HomeListAdapter(List<MemoryGeneration> memoryGenerationList) {
        memories = memoryGenerationList;
    }

    @NonNull
    @Override
    public PreviousSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PreviousSearchViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousSearchViewHolder previousSearchViewHolder, int i) {
        previousSearchViewHolder.dungeonSeed.setText(memories.get(i).getSeed());
        previousSearchViewHolder.dungeonGenTime.setText(memories.get(i).getGeneratedAt());
        previousSearchViewHolder.dungeonName.setText(memories.get(i).workOutDungeonName());

        previousSearchViewHolder.deleteButton.setOnClickListener(e -> {
            deletePressed(previousSearchViewHolder.itemView.getContext(), memories.get(i));
        });

        previousSearchViewHolder.itemView.setOnClickListener(e -> {
            clicked(previousSearchViewHolder.itemView.getContext(), memories.get(i));
        });
    }

    public void deletePressed(Context c, MemoryGeneration memoryGeneration) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    FirebaseTracker.EVENT(c, "MemoryDelete", "Deleted:" + memoryGeneration.getGeneratedAt());

                    try {
                        MemoryController.removeFromMemory(c, memoryGeneration);
                        memories = MemoryController.getMemory(c);
                        notifyDataSetChanged();
                    } catch (JSONException ec) {

                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage("Are you sure you want to delete this dungeon?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void clicked(Context context, MemoryGeneration memoryGeneration) {
        Intent generator = new Intent(context, GeneratingActivity.class);

        generator.putExtra("instructions", new Gson().toJson(memoryGeneration));

        context.startActivity(generator);
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    public static class PreviousSearchViewHolder extends RecyclerView.ViewHolder {

        TextView dungeonName;
        TextView dungeonGenTime;
        TextView dungeonSeed;

        ImageButton deleteButton;

        public PreviousSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            deleteButton = itemView.findViewById(R.id.deleteButton);
            dungeonName = itemView.findViewById(R.id.dungeonNameField);
            dungeonGenTime = itemView.findViewById(R.id.dungeonTimeGennedField);
            dungeonSeed = itemView.findViewById(R.id.dungeonSeedField);
        }
    }

}
