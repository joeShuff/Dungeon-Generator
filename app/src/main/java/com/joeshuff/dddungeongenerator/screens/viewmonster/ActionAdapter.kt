package com.joeshuff.dddungeongenerator.screens.viewmonster

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.generator.monsters.Action
import com.joeshuff.dddungeongenerator.screens.viewmonster.ActionAdapter.ActionViewHolder

class ActionAdapter(val actions: List<Action>) : RecyclerView.Adapter<ActionViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ActionViewHolder {
        return ActionViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.monster_special_actions, viewGroup, false))
    }

    override fun onBindViewHolder(actionViewHolder: ActionViewHolder, i: Int) {
        val a = actions[i]
        actionViewHolder.text.text = Html.fromHtml("<b><i>" + a.name + "</i></b>    " + a.desc)
    }

    override fun getItemCount() = actions.size

    class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.specialAction)
    }
}