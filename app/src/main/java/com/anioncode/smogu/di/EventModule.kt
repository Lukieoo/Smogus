package com.anioncode.smogu.di

import com.anioncode.smogu.events.NavEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.rxjava3.processors.PublishProcessor
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class EventModule {

    @Provides
    @Singleton
    fun providesNavEvents(): PublishProcessor<NavEvent> = PublishProcessor.create()


}