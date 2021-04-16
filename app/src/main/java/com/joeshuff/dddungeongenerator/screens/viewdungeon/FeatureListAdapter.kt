package com.joeshuff.dddungeongenerator.screens.viewdungeon

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.generator.features.*
import com.joeshuff.dddungeongenerator.screens.viewmonster.MonsterActivity
import com.joeshuff.dddungeongenerator.util.FirebaseTracker
import kotlinx.android.synthetic.main.feature_item.view.*
import kotlinx.android.synthetic.main.feature_item_title.view.*
import kotlinx.android.synthetic.main.feature_monster_content.view.*
import kotlinx.android.synthetic.main.feature_stairs_content.view.*
import kotlinx.android.synthetic.main.feature_trap_content.view.*

class FeatureListAdapter(var context: Context, var roomFeatures: FeatureContainer) : RecyclerView.Adapter<FeatureListAdapter.FeatureViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (roomFeatures.features[position]) {
            is StairsFeature -> 0
            is MonsterFeature -> 1
            is TrapFeature -> 2
            is TreasureFeature -> 3
            else -> 4
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FeatureViewHolder {
        return when (i) {
            0 -> StairsFeatureViewHolder(viewGroup)
            1 -> MonsterFeatureViewHolder(viewGroup)
            2 -> TrapFeatureViewHolder(viewGroup)
            3 -> TreasureFeatureViewHolder(viewGroup)
            else -> FeatureViewHolder(viewGroup)
        }
    }

    override fun onBindViewHolder(viewHolder: FeatureViewHolder, i: Int) {
        viewHolder.bind(roomFeatures.features[i])
    }

    override fun getItemCount(): Int {
        return roomFeatures.features.size
    }

    open class FeatureViewHolder(parent: ViewGroup, @LayoutRes resource: Int = R.layout.feature_standard_content, title: String = "No Feature") : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.feature_item, parent, false)) {
        open fun bind(feature: RoomFeature) {}

        init {
            val content = LayoutInflater.from(parent.context).inflate(resource, parent, false)
            val contentContainer = itemView.feature_content
            contentContainer.addView(content)
            itemView.title_bar.barText.text = title
        }
    }

    class StairsFeatureViewHolder(parent: ViewGroup) : FeatureViewHolder(parent, R.layout.feature_stairs_content, "Stairs") {
        override fun bind(feature: RoomFeature) {
            super.bind(feature)

            if (feature is StairsFeature) {
                with (feature) {
                    itemView.stairsDescription.text = getFeatureDescription()
                    itemView.stairsDirection.text = "Direction: $direction"
                    itemView.targetRoomId.text = "Destination Room: $connectedRoomId"
                    itemView.targetFloorId.text = "Destination Floor: $connectedFloorId"
                    itemView.viewTargetRoom.setOnClickListener {
                        val changeFloor = Intent("goto-room")
                        changeFloor.putExtra("room", connectedRoomId)
                        changeFloor.putExtra("floor", connectedFloorId)
                        FirebaseTracker.EVENT(itemView.context, "FeatureInteract", "GOTO ROOM")
                        LocalBroadcastManager.getInstance(itemView.context).sendBroadcast(changeFloor)
                    }
                }
            }
        }
    }

    class MonsterFeatureViewHolder(parent: ViewGroup) : FeatureViewHolder(parent, R.layout.feature_monster_content, "Monster") {
        override fun bind(feature: RoomFeature) {
            super.bind(feature)

            if (feature is MonsterFeature) {
                with (feature) {
                    itemView.monsterType.text = "Monster Type: ${selectedMonster?.name?: "None"}"
                    itemView.monsterCount.text = "Monster Count: $size"
                    itemView.viewMonsterButton.setOnClickListener {
                        FirebaseTracker.EVENT(itemView.context, "FeatureInteract", "VIEW MONSTER")
                        val showMonster = Intent(itemView.context, MonsterActivity::class.java)
                        showMonster.putExtra("monster", Gson().toJson(selectedMonster?: "{}"))
                        itemView.context.startActivity(showMonster)
                    }

                    itemView.monsterBossText.visibility = if (isBoss) View.VISIBLE else View.GONE
                }
            }
        }
    }

    class TreasureFeatureViewHolder(parent: ViewGroup) : FeatureViewHolder(parent, R.layout.feature_trap_content, "Treasure") {
        override fun bind(feature: RoomFeature) {
            super.bind(feature)
            if (feature is TreasureFeature) {
                itemView.trapDescription.text = feature.getFeatureDescription().trim()
            }
        }
    }

    class TrapFeatureViewHolder(parent: ViewGroup) : FeatureViewHolder(parent, R.layout.feature_trap_content, "Trap") {
        override fun bind(feature: RoomFeature) {
            super.bind(feature)
            if (feature is TrapFeature) {
                itemView.trapDescription.text = feature.getFeatureDescription().trim()
            }
        }
    }
}