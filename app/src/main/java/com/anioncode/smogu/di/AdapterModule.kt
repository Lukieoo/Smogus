package com.anioncode.smogu.di

import android.app.Application
import com.anioncode.smogu.adapter.SensorAdapter
import com.anioncode.smogu.adapter.SensorChartAdapter
import com.anioncode.smogu.events.NavEvent
import com.anioncode.smogu.model.ModelSensorId.SensorbyID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.rxjava3.processors.PublishProcessor
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
object AdapterModule {

    @Singleton
    @Provides
    fun provideSensorAdapter(application: Application, sensorEvent : PublishProcessor<SensorbyID> ): SensorAdapter {
        return SensorAdapter(application.applicationContext, object :
            SensorAdapter.OnItemClickListener {
            override fun onItemClick(model: SensorbyID) {
                sensorEvent.onNext(model)
            }

        })
    }
    @Singleton
    @Provides
    fun provideSensorChartAdapter(application: Application): SensorChartAdapter {
        return SensorChartAdapter(application.applicationContext)
    }
    @Provides
    @Singleton
    fun providesSensorEvent(): PublishProcessor<SensorbyID> = PublishProcessor.create()
}