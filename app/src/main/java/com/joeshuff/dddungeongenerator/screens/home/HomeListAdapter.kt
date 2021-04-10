package com.joeshuff.dddungeongenerator.screens.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import com.joeshuff.dddungeongenerator.screens.create.GeneratingActivity
import com.joeshuff.dddungeongenerator.util.FirebaseTracker
import org.json.JSONException

class HomeListAdapter(var memories: List<MemoryGeneration>) : RecyclerView.Adapter<HomeListAdapter.PreviousSearchViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PreviousSearchViewHolder {
        return PreviousSearchViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.history_item, viewGroup, false))
    }

    override fun onBindViewHolder(previousSearchViewHolder: PreviousSearchViewHolder, i: Int) {
        previousSearchViewHolder.dungeonSeed.text = memories[i].seed
        previousSearchViewHolder.dungeonGenTime.text = memories[i].generatedAt
        previousSearchViewHolder.dungeonName.text = memories[i].workOutDungeonName()
        previousSearchViewHolder.deleteButton.setOnClickListener { deletePressed(previousSearchViewHolder.itemView.context, memories[i]) }
        previousSearchViewHolder.itemView.setOnClickListener { clicked(previousSearchViewHolder.itemView.context, memories[i]) }
    }

    fun deletePressed(c: Context, memoryGeneration: MemoryGeneration) {
        val dialogClickListener = DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    FirebaseTracker.EVENT(c, "MemoryDelete", "Deleted:" + memoryGeneration.generatedAt)
                    try {
                        MemoryController.removeFromMemory(c, memoryGeneration)
                        memories = MemoryController.getMemory(c)
                        notifyDataSetChanged()
                    } catch (ec: JSONException) {
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

    fun clicked(context: Context, memoryGeneration: MemoryGeneration?) {
        val generator = Intent(context, GeneratingActivity::class.java)
        generator.putExtra("instructions", Gson().toJson(memoryGeneration))
        context.startActivity(generator)
    }

    override fun getItemCount(): Int {
        return memories.size
    }

    class PreviousSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dungeonName: TextView
        var dungeonGenTime: TextView
        var dungeonSeed: TextView
        var deleteButton: ImageButton

        init {
            deleteButton = itemView.findViewById(R.id.deleteButton)
            dungeonName = itemView.findViewById(R.id.dungeonNameField)
            dungeonGenTime = itemView.findViewById(R.id.dungeonTimeGennedField)
            dungeonSeed = itemView.findViewById(R.id.dungeonSeedField)
        }
    }

}