package com.joeshuff.dddungeongenerator.screens.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.db.dungeonDao
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.screens.create.GeneratingActivity
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsActivity
import com.joeshuff.dddungeongenerator.util.FirebaseTracker
import com.joeshuff.dddungeongenerator.util.Logs
import com.joeshuff.dddungeongenerator.util.makeGone
import com.joeshuff.dddungeongenerator.util.makeVisible
import io.realm.Realm
import kotlinx.android.synthetic.main.history_item.view.*

class DungeonHistoryAdapter(var history: ArrayList<DungeonHistoryItem>,
                                var sortable: HomeSortable) : RecyclerView.Adapter<DungeonHistoryAdapter.DungeonHistoryViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) =
        DungeonHistoryViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.history_item, viewGroup, false))

    fun update(newHistory: ArrayList<DungeonHistoryItem>) {
        val old = arrayListOf<DungeonHistoryItem>()
        old.addAll(this.history)

        this.history.clear()
        this.history.addAll(sortable.sort.invoke(newHistory))

        if (this.history.size > 10 || this.history.isEmpty() || old.isEmpty()) {
            notifyDataSetChanged()
        } else {
            val diffResult = DiffUtil.calculateDiff(DungeonHistoryDiffUtil(old, this.history))
            diffResult.dispatchUpdatesTo(this)
        }
    }

    fun updateSort(sortable: HomeSortable) {
        this.sortable = sortable
        val items = arrayListOf<DungeonHistoryItem>()
        items.addAll(this.history)
        update(items)
    }

    override fun onBindViewHolder(previousSearchViewHolder: DungeonHistoryViewHolder, i: Int) {
        previousSearchViewHolder.bind(history[i])
    }

    override fun getItemCount() = history.size

    class DungeonHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dungeonName: TextView = itemView.dungeonNameField
        var dungeonGenTime: TextView = itemView.dungeonTimeGennedField
        var dungeonSeed: TextView = itemView.dungeonSeedField
        var deleteButton: ImageButton = itemView.deleteButton

        fun bind(dungeon: DungeonHistoryItem) {
            dungeonName.text = dungeon.dungeonName
            dungeonGenTime.text = dungeon.getCreatedDate()
            dungeonSeed.text = dungeon.seed
            deleteButton.setOnClickListener { deletePressed(itemView.context, dungeon) }
            itemView.setOnClickListener { clicked(itemView.context, dungeon) }
        }

        fun clicked(context: Context, dungeon: DungeonHistoryItem) {
            var generator = Intent(context, ResultsActivity::class.java)
            dungeon.dungeonId?.let { generator.putExtra(ResultsActivity.DUNGEON_ID_EXTRA, it) }
            dungeon.memoryId?.let {
                generator = Intent(context, GeneratingActivity::class.java)
                generator.putExtra(GeneratingActivity.GENERATION_INSTRUCTIONS, Gson().toJson(it))
            }

            context.startActivity(generator)
        }

        fun deletePressed(c: Context, dungeon: DungeonHistoryItem) {
            val dialogClickListener = DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        dungeon.dungeonId?.let {
                            FirebaseTracker.EVENT(c, "MemoryDelete", "Deleted:" + it)
                            Realm.getDefaultInstance().dungeonDao().deleteDungeonById(it).subscribe({}, {
                                Logs.e("DeleteDungeon", "Something went wrong deleting the dungeon", it)
                                Toast.makeText(c, "Something went wrong deleting this dungeon.", Toast.LENGTH_LONG).show()
                            })
                        }?: dungeon.memoryId?.let {
                            MemoryController.removeFromMemory(c, it)
                        }

                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

            val builder = AlertDialog.Builder(c)
            builder.setMessage("Are you sure you want to delete this dungeon?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show()
        }
    }

    inner class DungeonHistoryDiffUtil(val oldItems: ArrayList<DungeonHistoryItem>, val newItems: ArrayList<DungeonHistoryItem>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = false
    }
}