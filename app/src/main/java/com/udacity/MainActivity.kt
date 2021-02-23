package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannel()
        }





        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            val buttonId = radios.checkedRadioButtonId
            if (buttonId ==-1)
            {
                Toast.makeText(this,"please select the file to download",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            download()

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.isDownloadFinished()
            showNotification()
        }
    }

    private fun download() {

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.



    }

    fun onRadioClick(view: View)
    {

        when(view.id)
      {
          glide.id-> url = URL2
          App.id-> url = URL
          retrofit.id-> url = URL3
          else->
           {
               return
          }

      }


    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL2 = "https://github.com/bumptech/glide"
            private const val URL3 ="https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel()
    {
        val notificationChannel = NotificationChannel(CHANNEL_ID,"DownloadChannel",NotificationManager.IMPORTANCE_HIGH)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun showNotification()
    {
        val intent = Intent(this,DetailActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification =NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_notification)
                setContentTitle("Download")
                setContentText("Completed")


            }.build()
val notifiCationManager = NotificationManagerCompat.from(this)
        notifiCationManager.notify(1,notification)

    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//    }




}
