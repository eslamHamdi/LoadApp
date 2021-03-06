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
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.transition.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager:DownloadManager
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
            if (buttonId < 0)
            {

                if (isNetworkConnected(this))
                {

                    Toast.makeText(this,"please select the file to download",Toast.LENGTH_SHORT).show()
                }

                return@setOnClickListener
            }
            custom_button.animation()
            download()

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            var description = ""
            var status = "Failed"
            when(url)
            {
                URL-> description = resources.getString(R.string.LoadApp)
                URL2-> description =  resources.getString(R.string.Glide)
                URL3 ->  description = resources.getString(R.string.Retrofit)
            }



            if (id == downloadID)
            {
                Log.e(null, "onReceive: ${id } , $downloadID" )
                status = "Success"


            }
            //stopping button animation after download process completed
            custom_button.isDownloadFinished()

            //Log.e(null, "onReceive: ${description + " " + title}" )

            showNotification(description,status)
        }
    }

    private fun download() {
        Log.e(null, "download: entered" )

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


       downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.





    }

    //handle user selection
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

    fun showNotification(descripe:String , state:String)
    {
        val bundle = Bundle()
        bundle.putString("desc",descripe)
        bundle.putString("st",state)
        val intent = Intent(this,DetailActivity::class.java).apply {
            putExtras(bundle)
        }
        pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        action = NotificationCompat.Action(0,"Check the status" ,pendingIntent)
        val notifiCationManager = NotificationManagerCompat.from(this)

        val notification =NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setSmallIcon(R.drawable.ic_notification)
                setContentTitle("Udacity: Android Kotlin Nanodegree")
                setContentText("The Project 3 Repository is Downloaded")
                //updating Customize notification UI based on the status of the download/upload ( fail, success)
                setSubText(state)
                priority = NotificationCompat.PRIORITY_HIGH
                setAutoCancel(true)
                addAction(action)


            }.build()

        notifiCationManager.notify(1,notification)

    }






}
