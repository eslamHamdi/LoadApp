package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        name.text = intent.getStringExtra("desc")
        status.text = intent.getStringExtra("st")
        stateCheck(status.text.toString())
motion.setTransition(R.id.start,R.id.end)
        motion.transitionToEnd()
    }

    fun onfabClick(view: View)
    {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun stateCheck(state:String)
    {
        if (state == "Failed")
            status.setTextColor(Color.RED)

    }

}
