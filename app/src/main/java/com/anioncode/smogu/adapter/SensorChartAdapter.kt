package com.anioncode.smogu.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anioncode.smogu.model.ChartPart
import com.anioncode.smogu.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.item_chart.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SensorChartAdapter(val items: ArrayList<ChartPart>, val context: Context) :
    RecyclerView.Adapter<ViewChartHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewChartHolder {
        return ViewChartHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_chart,
                parent,
                false
            )
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewChartHolder, position: Int) {
        var model = items.get(position)

        var color1: Int
        var color2: Int
        when (model.codeName) {
            "CO" -> {
                color1 = ContextCompat.getColor(context, R.color.chart1)
                color2 = ContextCompat.getColor(context, R.color.chart1dq)
            }
            "NO2" -> {
                color1 = ContextCompat.getColor(context, R.color.chart2)
                color2 = ContextCompat.getColor(context, R.color.chart2dq)
            }
            "O3" -> {
                color1 = ContextCompat.getColor(context, R.color.chart3)
                color2 = ContextCompat.getColor(context, R.color.chart3dq)
            }
            "PM10" -> {
                color1 = ContextCompat.getColor(context, R.color.chart4)
                color2 = ContextCompat.getColor(context, R.color.chart4dq)
            }
            "PM2.5" -> {
                color1 = ContextCompat.getColor(context, R.color.chart5)
                color2 = ContextCompat.getColor(context, R.color.chart5dq)
            }
            "SO2" -> {
                color1 = ContextCompat.getColor(context, R.color.chart6)
                color2 = ContextCompat.getColor(context, R.color.chart6dq)
            }
            else -> {
                color1 = ContextCompat.getColor(context, R.color.chart1)
                color2 = ContextCompat.getColor(context, R.color.chart1dq)
            }


        }


        holder.codeSensor.text = "Ocena jakości ${model.codeName}"
        holder.chartSensor.apply {
            setNoDataTextTypeface(Typeface.SANS_SERIF)
            setNoDataText("");
            setTouchEnabled(true)
            setPinchZoom(false)
            setNoDataTextColor(color1)
            getLegend().setEnabled(false);
            getAxisRight().setDrawLabels(false);
            getAxisLeft().setDrawGridLines(false);
            getXAxis().setDrawGridLines(false);
            description.text = ""
            setExtraBottomOffset(35f);
        }


        val lineDataSet1 = LineDataSet(model.entries, "")
        lineDataSet1.apply {
            color = color2
            disableDashedLine()
            setDrawFilled(true)

            lineWidth = 2f
            setCircleColor(R.color.colorGreen)
            fillColor = color1
            setDrawValues(false)
            setAxisDependency(YAxis.AxisDependency.RIGHT)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }


        holder.chartSensor.data = LineData(lineDataSet1)

        holder.chartSensor.animateX(
            500,
            Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseInExpo)
        )
        holder.chartSensor.invalidate()
        val xAxis: XAxis = holder.chartSensor.getXAxis()
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val xAxisFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                val format = SimpleDateFormat("HH:mm")
                return format.format(Date(value.toLong() * 1000L))
            }

        }

        with(xAxis) {
            valueFormatter = xAxisFormatter
            setLabelCount(model.entries.size)
        }

        xAxis.textColor = color2
    }
}

class ViewChartHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val codeSensor = view.partJakosc
    val chartSensor = view.lineChartView

}