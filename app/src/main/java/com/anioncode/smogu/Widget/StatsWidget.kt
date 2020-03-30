package com.anioncode.smogu.Widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.anioncode.retrofit2.ApiService
import com.anioncode.retrofit2.RetrofitClientInstance
import com.anioncode.smogu.CONST.MyPreference
import com.anioncode.smogu.Model.ModelIndex.ModelIndex
import com.anioncode.smogu.R
import kotlinx.android.synthetic.main.fragment_dash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Implementation of App Widget functionality.
 */
class StatsWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
lateinit var modelIndex: ModelIndex
lateinit var myPreference: MyPreference
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.stats_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
    myPreference = MyPreference(context)
    views.setTextViewText(R.id.nameStation, myPreference.getSTATION_NAME())
//    views.setTextViewText(R.id.nameLevel, "XD")
    // Instruct the widget manager to update the widget
    getDataStation(context,views,appWidgetManager,appWidgetId)
    appWidgetManager.updateAppWidget(appWidgetId, views)

}

private fun getDataStation(context: Context,views:RemoteViews, appWidgetManager: AppWidgetManager,appWidgetId: Int) {
    val api = RetrofitClientInstance.getRetrofitInstance()!!.create(ApiService::class.java)

    api.getIndex(if (!myPreference.getID().toString().equals("")) myPreference.getID().toString() else "14")
        .enqueue(object : Callback<ModelIndex> {

            override fun onResponse(
                call: Call<ModelIndex>,
                response: Response<ModelIndex>
            ) {
                var color: Int =
                    R.color.colorbdb
                modelIndex = response.body()!!
                var nameLevel: String = "uspik"

                views.setTextViewText(R.id.nameLevel, "esx")
             //   if (modelIndex.stIndexLevel != null) {
                    color = pairColor(modelIndex.stIndexLevel.id)
                    nameLevel = modelIndex.stIndexLevel.indexLevelName


              //  }


                    views.setTextViewText(R.id.nameLevel, nameLevel)
                  views.setTextColor(R.id.nameLevel,context.resources.getColor(color))
                appWidgetManager.updateAppWidget(appWidgetId, views)


            }

            override fun onFailure(call: Call<ModelIndex>, t: Throwable) {
                Log.d("MainActivity1313x: ", "Call  ${t.message}")
                views.setTextViewText(R.id.nameLevel, "XDs")
            }


            fun pairColor(model: Int): Int {
                var color1: Int
                when (model) {
                    0 -> {
                        color1 =
                            R.color.colorbdb
                    }
                    1 -> {
                        color1 =
                            R.color.colordb
                    }
                    2 -> {
                        color1 =
                            R.color.colordst
                    }
                    3 -> {
                        color1 =
                            R.color.colordop
                    }
                    4 -> {
                        color1 =
                            R.color.colorndst
                    }
                    else -> {
                        color1 =
                            R.color.colorndst
                    }
                }
                return color1
            }
        })


}