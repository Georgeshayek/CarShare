package com.example.project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class SettingAdapter(context: Context, layoutResource: Int, items: Array<String>) :
    ArrayAdapter<String>(context, layoutResource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.setting_listitem, parent, false)
        }

        val itemText = view?.findViewById<TextView>(R.id.item_text)
        val itemIcon = view?.findViewById<ImageView>(R.id.item_icon)

        itemText?.text = getItem(position)


        when (position) {
            0 -> itemIcon?.setImageResource(R.drawable.baseline_person_24)
            1 -> itemIcon?.setImageResource(R.drawable.baseline_history_24)
            2 -> itemIcon?.setImageResource(R.drawable.baseline_info_24)
            3 -> itemIcon?.setImageResource(R.drawable.baseline_logout_24)
        }

        return view!!
    }
}