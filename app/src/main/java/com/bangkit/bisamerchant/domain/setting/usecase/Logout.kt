package com.bangkit.bisamerchant.domain.setting.usecase

import com.bangkit.bisamerchant.domain.setting.repository.ISettingRepository
import javax.inject.Inject

class Logout @Inject constructor(private val settingRepository: ISettingRepository) {

    suspend fun execute() =
        settingRepository.logout()
}
