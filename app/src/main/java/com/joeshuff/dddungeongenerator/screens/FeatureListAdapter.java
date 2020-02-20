package com.joeshuff.dddungeongenerator.screens;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.FirebaseTracker;
import com.joeshuff.dddungeongenerator.MonsterActivity;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.generator.features.*;

import java.util.List;

public class FeatureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                return new StairsFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_stairs_item, viewGroup, false));

            case 1:
                return new MonsterFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_monster_item, viewGroup, false));

            case 2:
                return new TrapFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_trap_item, viewGroup, false));

            case 3:
                return new TreasureFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_treasure_item, viewGroup, false));

            case 4:
                return new FeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_standard_item, viewGroup, false));

            default:
                return new FeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_standard_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = viewHolder.getItemViewType();

        ((FeatureViewHolder) viewHolder).setPosition(i);

        if (type == 0) { //Stairs
            StairsFeature stairs = (StairsFeature) roomFeatures.get(i);
            StairsFeatureViewHolder stairsFeatureViewHolder = (StairsFeatureViewHolder) viewHolder;

            loadStairsView(stairs, stairsFeatureViewHolder);

            return;
        }

        if (type == 1) { //Monster
            MonsterFeature monster = (MonsterFeature) roomFeatures.get(i);
            MonsterFeatureViewHolder viewHolderMonster = (MonsterFeatureViewHolder) viewHolder;

            loadMonsterView(monster, viewHolderMonster);

            return;
        }

        if (type == 2) { //Trap
            TrapFeature trapFeature = (TrapFeature) roomFeatures.get(i);
            TrapFeatureViewHolder trapFeatureViewHolder = (TrapFeatureViewHolder) viewHolder;

            loadTrapView(trapFeature, trapFeatureViewHolder);

            return;
        }

        if (type == 3) { //Treasure
            TreasureFeature treasureFeature = (TreasureFeature) roomFeatures.get(i);
            TreasureFeatureViewHolder treasureFeatureViewHolder = (TreasureFeatureViewHolder) viewHolder;

            loadTreasureView(treasureFeature, treasureFeatureViewHolder);

            return;
        }

        loadFeatureView(roomFeatures.get(i), (FeatureViewHolder) viewHolder);
    }

    private void loadFeatureView(RoomFeature roomFeature, FeatureViewHolder featureViewHolder) {

    }

    private void loadStairsView(StairsFeature stairsFeature, StairsFeatureViewHolder stairsFeatureViewHolder) {
        stairsFeatureViewHolder.stairsDesc.setText(stairsFeature.getFeatureDescription());
        stairsFeatureViewHolder.stairsDirection.setText("Direction: " + stairsFeature.getDirection());
        stairsFeatureViewHolder.destRoom.setText("Destination Room: " + stairsFeature.getConnectedRoomId());
        stairsFeatureViewHolder.destFloor.setText("Destination Floor: " + stairsFeature.getConnectedFloorId());

        stairsFeatureViewHolder.gotoButton.setOnClickListener(e -> {
            Intent changeFloor = new Intent("goto-room");
            changeFloor.putExtra("room", stairsFeature.getConnectedRoomId());
            changeFloor.putExtra("floor", stairsFeature.getConnectedFloorId());

            FirebaseTracker.EVENT(stairsFeatureViewHolder.itemView.getContext(), "FeatureInteract", "GOTO ROOM");

            LocalBroadcastManager.getInstance(stairsFeatureViewHolder.itemView.getContext()).sendBroadcast(changeFloor);
        });
    }

    private void loadMonsterView(MonsterFeature monsterFeature, MonsterFeatureViewHolder monsterFeatureViewHolder) {
        monsterFeatureViewHolder.monsterType.setText("Monster Type: " + monsterFeature.getSelectedMonster().getName());
        monsterFeatureViewHolder.monsterCount.setText("Monster Count: " + monsterFeature.getSize());

        monsterFeatureViewHolder.viewMonsterButton.setOnClickListener(e -> {

            FirebaseTracker.EVENT(context, "FeatureInteract", "VIEW MONSTER");

            Intent showMonster = new Intent(monsterFeatureViewHolder.itemView.getContext(), MonsterActivity.class);
            showMonster.putExtra("monster", new Gson().toJson(monsterFeature.getSelectedMonster()));
            monsterFeatureViewHolder.itemView.getContext().startActivity(showMonster);
        });

        monsterFeatureViewHolder.bossTextView.setVisibility(monsterFeature.isBoss() ?  View.VISIBLE : View.GONE);
    }

    private void loadTrapView(TrapFeature trapFeature, TrapFeatureViewHolder trapFeatureViewHolder) {
        trapFeatureViewHolder.trapDesc.setText(trapFeature.getFeatureDescription());
    }

    private void loadTreasureView(TreasureFeature treasureFeature, TreasureFeatureViewHolder treasureFeatureViewHolder) {
        treasureFeatureViewHolder.treasureText.setText(treasureFeature.getFeatureDescription());
    }

    @Override
    public int getItemCount() {
        return roomFeatures.size();
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tabTitle;
        ExpandableLinearLayout expandableLinearLayout;
        ImageView arrowView;

        int position = -1;

        public void setPosition(int position) {
            this.position = position;
        }

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            tabTitle = itemView.findViewById(R.id.tabTitle);
            expandableLinearLayout = itemView.findViewById(R.id.expandableLayout);
            arrowView = itemView.findViewById(R.id.triangleIcon);

            arrowView.setRotation(0f);

//            if (position != -1) expandableLinearLayout.setExpanded(expandState.get(position));

            expandableLinearLayout.setInRecyclerView(true);
            expandableLinearLayout.setDuration(300);
            expandableLinearLayout.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));

            tabTitle.setOnClickListener(e -> {
                expandableLinearLayout.toggle();
            });

            expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    createRotateAnimator(arrowView, 0f, 180f).start();
                    if (position != -1) expandState.put(position, true);
                }

                @Override
                public void onPreClose() {
                    createRotateAnimator(arrowView, 180f, 0f).start();
                    if (position != -1) expandState.put(position, false);
                }
            });


        }

        public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
            return animator;
        }
    }

    public static class StairsFeatureViewHolder extends FeatureViewHolder {

        TextView stairsDesc;
        TextView stairsDirection;

        TextView destRoom;
        TextView destFloor;

        MaterialButton gotoButton;

        public StairsFeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            stairsDesc = itemView.findViewById(R.id.stairsDescription);
            stairsDirection = itemView.findViewById(R.id.stairsDirection);

            destRoom = itemView.findViewById(R.id.targetRoomId);
            destFloor = itemView.findViewById(R.id.targetFloorId);

            gotoButton = itemView.findViewById(R.id.viewTargetRoom);
        }
    }

    public static class MonsterFeatureViewHolder extends FeatureViewHolder {

        TextView monsterType;
        TextView monsterCount;
        MaterialButton viewMonsterButton;
        TextView bossTextView;

        public MonsterFeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            monsterType = itemView.findViewById(R.id.monsterType);
            monsterCount = itemView.findViewById(R.id.monsterCount);
            viewMonsterButton = itemView.findViewById(R.id.viewMonsterButton);
            bossTextView = itemView.findViewById(R.id.monsterBossText);
        }
    }

    public static class TreasureFeatureViewHolder extends FeatureViewHolder {

        TextView treasureText;

        public TreasureFeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            treasureText = itemView.findViewById(R.id.treasureDescription);
        }
    }

    public static class TrapFeatureViewHolder extends FeatureViewHolder {

        TextView trapDesc;

        public TrapFeatureViewHolder(@NonNull View itemView) {
            super(itemView);

            trapDesc = itemView.findViewById(R.id.trapDescription);
        }
    }
}
