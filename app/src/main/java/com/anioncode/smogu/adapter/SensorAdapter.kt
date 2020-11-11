package com.anioncode.smogu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.item_pomiar.view.*

class SensorAdapter(
    val items: ArrayList<SensorbyID>,
    val context: Context,
    var clickListner: OnItemClickListner
) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListner{
        fun onItemClick(model:SensorbyID)
    }
    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pomiar, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model=items.get(position)
        holder?.nameSensor?.text = model.key
        var iter=0;

        holder.itemView.setOnClickListener {
            clickListner.onItemClick(model)

        }

      if (model.values!=null)  {
          for (data in model.values){
              println(data.value);
              println(data.date);

              if(data.value!=null&&model.values.get(iter).value!=0.0){

                  holder?.nameSensorPomiar?.text = model.values.get(iter).value.toInt().toString()
                  break
              }
              iter++;
          }
      }

    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val nameSensor = view.namesensor
    val nameSensorPomiar = view.namesensorPomiar
    
}