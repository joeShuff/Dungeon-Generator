package com.joeshuff.dddungeongenerator.screens.viewdungeon;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.screens.viewmonster.MonsterActivity;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.generator.features.*;
import com.joeshuff.dddungeongenerator.util.FirebaseTracker;

import java.util.List;

public class FeatureListAdapter extends RecyclerView.Adapter<FeatureListAdapter.FeatureViewHolder> {

    List<RoomFeature> roomFeatures;
    private static SparseBooleanArray expandState = new SparseBooleanArray();

    Context context;

    public FeatureListAdapter(Context context, List<RoomFeature> roomFeatures) {
        this.roomFeatures = roomFeatures;
        this.context = context;

        expandState.clear();
        for (int i = 0; i < roomFeatures.size(); i++) {
            expandState.append(i, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        RoomFeature thisFeature = roomFeatures.get(position);

        if (thisFeature instanceof StairsFeature) return 0;
        if (thisFeature instanceof MonsterFeature) return 1;
        if (thisFeature instanceof TrapFeature) return 2;
        if (thisFeature instanceof TreasureFeature) return 3;

        return 4;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                return new StairsFeatureViewHolder(viewGroup);
            case 1:
                return new MonsterFeatureViewHolder(viewGroup);
            case 2:
                return new TrapFeatureViewHolder(viewGroup);
            case 3:
                return new TreasureFeatureViewHolder(viewGroup);
            default:
                return new FeatureViewHolder(viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder viewHolder, int i) {
        viewHolder.bind(roomFeatures.get(i));
    }

    @Override
    public int getItemCount() {
        return roomFeatures.size();
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout tabTitle;

        int position = -1;

        public void setPosition(int position) {
            this.position = position;
        }

        public FeatureViewHolder(@NonNull ViewGroup parent) {
            this(parent, R.layout.feature_standard_content);
        }

        public FeatureViewHolder(@NonNull ViewGroup parent, @LayoutRes Integer resource) {
            this(parent, resource, "No Feature");
        }

        public FeatureViewHolder(@NonNull ViewGroup parent, @LayoutRes Integer resource, String title) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_item, parent, false));

            tabTitle = itemView.findViewById(R.id.title_bar);
            View content = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

            RelativeLayout contentContainer = itemView.findViewById(R.id.feature_content);
            contentContainer.addView(content);

            TextView titleText = tabTitle.findViewById(R.id.barText);
            titleText.setText(title);
        }

        public void bind(RoomFeature feature) {

        }
    }

    public static class StairsFeatureViewHolder extends FeatureViewHolder {
        TextView stairsDesc;
        TextView stairsDirection;

        TextView destRoom;
        TextView destFloor;

        MaterialButton gotoButton;

        public StairsFeatureViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.feature_stairs_content, "Stairs");

            stairsDesc = itemView.findViewById(R.id.stairsDescription);
            stairsDirection = itemView.findViewById(R.id.stairsDirection);

            destRoom = itemView.findViewById(R.id.targetRoomId);
            destFloor = itemView.findViewById(R.id.targetFloorId);

            gotoButton = itemView.findViewById(R.id.viewTargetRoom);
        }

        @Override
        public void bind(RoomFeature feature) {
            super.bind(feature);

            if (feature instanceof StairsFeature) {
                StairsFeature stairsFeature = (StairsFeature) feature;

                stairsDesc.setText(stairsFeature.getFeatureDescription());
                stairsDirection.setText("Direction: " + stairsFeature.getDirection());
                destRoom.setText("Destination Room: " + stairsFeature.getConnectedRoomId());
                destFloor.setText("Destination Floor: " + stairsFeature.getConnectedFloorId());

                gotoButton.setOnClickListener(e -> {
                    Intent changeFloor = new Intent("goto-room");
                    changeFloor.putExtra("room", stairsFeature.getConnectedRoomId());
                    changeFloor.putExtra("floor", stairsFeature.getConnectedFloorId());

                    FirebaseTracker.EVENT(itemView.getContext(), "FeatureInteract", "GOTO ROOM");

                    LocalBroadcastManager.getInstance(itemView.getContext()).sendBroadcast(changeFloor);
                });
            }
        }
    }

    public static class MonsterFeatureViewHolder extends FeatureViewHolder {

        TextView monsterType;
        TextView monsterCount;
        MaterialButton viewMonsterButton;
        TextView bossTextView;

        public MonsterFeatureViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.feature_monster_content, "Monster");

            monsterType = itemView.findViewById(R.id.monsterType);
            monsterCount = itemView.findViewById(R.id.monsterCount);
            viewMonsterButton = itemView.findViewById(R.id.viewMonsterButton);
            bossTextView = itemView.findViewById(R.id.monsterBossText);
        }

        @Override
        public void bind(RoomFeature feature) {
            super.bind(feature);

            if (feature instanceof MonsterFeature) {
                MonsterFeature monsterFeature = (MonsterFeature) feature;

                monsterType.setText("Monster Type: " + monsterFeature.getSelectedMonster().getName());
                monsterCount.setText("Monster Count: " + monsterFeature.getSize());

                viewMonsterButton.setOnClickListener(e -> {

                    FirebaseTracker.EVENT(itemView.getContext(), "FeatureInteract", "VIEW MONSTER");

                    Intent showMonster = new Intent(itemView.getContext(), MonsterActivity.class);
                    showMonster.putExtra("monster", new Gson().toJson(monsterFeature.getSelectedMonster()));
                    itemView.getContext().startActivity(showMonster);
                });

                bossTextView.setVisibility(monsterFeature.isBoss() ?  View.VISIBLE : View.GONE);
            }
        }
    }

    public static class TreasureFeatureViewHolder extends FeatureViewHolder {

        TextView treasureText;

        public TreasureFeatureViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.feature_trap_content, "Treasure");

            treasureText = itemView.findViewById(R.id.trapDescription);
        }

        @Override
        public void bind(RoomFeature feature) {
            super.bind(feature);

            if (feature instanceof TreasureFeature) {
                TreasureFeature treasureFeature = (TreasureFeature) feature;
                treasureText.setText(treasureFeature.getFeatureDescription().trim());
            }
        }
    }

    public static class TrapFeatureViewHolder extends FeatureViewHolder {

        TextView trapDesc;

        public TrapFeatureViewHolder(@NonNull ViewGroup parent) {
            super(parent, R.layout.feature_trap_content, "Trap");
            trapDesc = itemView.findViewById(R.id.trapDescription);
        }

        @Override
        public void bind(RoomFeature feature) {
            super.bind(feature);

            if (feature instanceof TrapFeature) {
                TrapFeature trapFeature = (TrapFeature) feature;
                trapDesc.setText(trapFeature.getFeatureDescription().trim());
            }
        }
    }
}
