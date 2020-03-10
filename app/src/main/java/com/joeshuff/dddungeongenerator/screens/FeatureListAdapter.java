package com.joeshuff.dddungeongenerator.screens;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.util.FirebaseTracker;
import com.joeshuff.dddungeongenerator.MonsterActivity;
import com.joeshuff.dddungeongenerator.R;
import com.joeshuff.dddungeongenerator.generator.features.*;
import com.skydoves.expandablelayout.ExpandableLayout;
import com.skydoves.expandablelayout.OnExpandListener;

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
                return new StairsFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item, viewGroup, false));
            case 1:
                return new MonsterFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item, viewGroup, false));
            case 2:
                return new TrapFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item, viewGroup, false));
            case 3:
                return new TreasureFeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item, viewGroup, false));
            default:
                return new FeatureViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feature_item, viewGroup, false), R.layout.feature_standard_content);
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

        ConstraintLayout tabTitle;
        ExpandableLayout expandableLayout;
        ImageView arrowView;

        int position = -1;

        public void setPosition(int position) {
            this.position = position;
        }

        public FeatureViewHolder(@NonNull View itemView, @LayoutRes Integer resource) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.featureItemExpandableLayout);
            tabTitle = expandableLayout.parentLayout.findViewById(R.id.featureItemTitleBar);
            arrowView = itemView.findViewById(R.id.triangleIcon);
            arrowView.setRotation(0f);

            expandableLayout.setSecondLayoutResource(resource);

            tabTitle.setOnClickListener(e -> {
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                }
            });

            expandableLayout.setOnExpandListener(new OnExpandListener() {
                @Override
                public void onExpand(boolean b) {
                    if (b) {
                        onPreOpen();
                    } else {
                        onPreClose();
                    }
                }

                public void onPreOpen() {
                    createRotateAnimator(arrowView, 0f, 180f).start();
                    if (position != -1) expandState.put(position, true);
                }

                public void onPreClose() {
                    createRotateAnimator(arrowView, 180f, 0f).start();
                    if (position != -1) expandState.put(position, false);
                }
            });
        }

        public void setTitle(String title) {
            TextView titleView = tabTitle.findViewById(R.id.barText);
            titleView.setText(title);
        }

        public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(new LinearInterpolator());
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
            super(itemView, R.layout.feature_stairs_content);

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
            super(itemView, R.layout.feature_monster_content);

            monsterType = itemView.findViewById(R.id.monsterType);
            monsterCount = itemView.findViewById(R.id.monsterCount);
            viewMonsterButton = itemView.findViewById(R.id.viewMonsterButton);
            bossTextView = itemView.findViewById(R.id.monsterBossText);
        }
    }

    public static class TreasureFeatureViewHolder extends FeatureViewHolder {

        TextView treasureText;

        public TreasureFeatureViewHolder(@NonNull View itemView) {
            super(itemView, R.layout.feature_treasure_content);

            treasureText = itemView.findViewById(R.id.treasureDescription);
        }
    }

    public static class TrapFeatureViewHolder extends FeatureViewHolder {

        TextView trapDesc;

        public TrapFeatureViewHolder(@NonNull View itemView) {
            super(itemView, R.layout.feature_trap_content);

            trapDesc = itemView.findViewById(R.id.trapDescription);
        }
    }
}
