package com.bangkit.bisamerchant.data.setting.repository

import com.bangkit.bisamerchant.data.setting.datasource.SettingDataSource
import com.bangkit.bisamerchant.domain.setting.repository.ISettingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val settingDataSource: SettingDataSource
) : ISettingRepository {
    override suspend fun logout() =
        settingDataSource.logout()
}
