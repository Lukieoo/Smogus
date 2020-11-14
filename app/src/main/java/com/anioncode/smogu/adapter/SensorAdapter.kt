package com.anioncode.smogu.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.item_pomiar.view.*

open class SensorAdapter(
    val context: Context,
    private var onClick: OnItemClickListener
) : RecyclerView.Adapter<ViewHolder>() {

    private lateinit var data: ArrayList<SensorbyID>

    fun setData(data: ArrayList<SensorbyID>) {
        this.data = data
        notifyDataSetChanged()
    }

    // Gets the number of sensor in the list
    override fun getItemCount(): Int {

        return if (::data.isInitialized) {
            data.size
        } else {
            0
        }
    }

    interface OnItemClickListener {
        fun onItemClick(model: SensorbyID)
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_pomiar, parent, false)
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = data.get(position)
        holder?.nameSensor?.text = model.key
        var iter = 0
        holder.itemView.setOnClickListener {
            onClick.onItemClick(model)
            for (singleItem in data) {
                singleItem.isClicked = false
            }
            model.isClicked = true
            notifyDataSetChanged()
        }

        if (model.isClicked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.bacgroundCardView.setCardBackgroundColor(context.getColor(R.color.colorBlue))
                holder.nameSensor.setTextColor(context.getColor(R.color.colorWhite))
                holder.nameSensorPomiar.setTextColor(context.getColor(R.color.colorWhite))
                holder.kind.setTextColor(context.getColor(R.color.colorWhite))
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.bacgroundCardView.setCardBackgroundColor(context.getColor(R.color.colorBlueNotSelected))
                holder.nameSensor.setTextColor(context.getColor(android.R.color.tab_indicator_text))
                holder.nameSensorPomiar.setTextColor(context.getColor(android.R.color.tab_indicator_text))
                holder.kind.setTextColor(context.getColor(android.R.color.tab_indicator_text))
            }
        }

        if (model.values != null) {
            for (data in model.values) {
                println(data.value);
                println(data.date);

                if (data.value != null && model.values.get(iter).value != 0.0) {

                    holder?.nameSensorPomiar?.text = model.values[iter].value.toInt().toString()
                    break
                }
                iter++;
            }
        }

    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val nameSensor = view.namesensor
    val nameSensorPomiar = view.namesensorPomiar
    val bacgroundCardView = view.background_card
    val kind = view.kind

}