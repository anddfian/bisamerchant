package com.bangkit.bisamerchant.presentation.services

import android.app.PendingIntent
import android.content.Intent
import com.bangkit.bisamerchant.domain.home.model.MessageNotif
import com.bangkit.bisamerchant.presentation.history.activity.HistoryActivity
import com.bangkit.bisamerchant.presentation.utils.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    @Inject lateinit var notificationService: NotificationService

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            notificationService.sendTokenRegistration(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            val action = remoteMessage.data["action"]

            val contentIntent = createPendingIntent(action)

            Utils.pushNotification(
                applicationContext,
                contentIntent,
                resources,
                MessageNotif(title, body, null)
            )
        }
    }

    private fun createPendingIntent(action: String?): PendingIntent {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra("action", action)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}
