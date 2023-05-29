package com.bangkit.bisamerchant.core.di

import android.content.Context
import com.bangkit.bisamerchant.core.helper.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return SharedPreferences.getInstance(context)
    }
}