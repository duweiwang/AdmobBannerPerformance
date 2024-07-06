package com.google.android.gms.example.bannerexample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mainactivity)


        findViewById<Button>(R.id.dont_init)
            .setOnClickListener {
                startActivity(Intent(
                    this,DontInit::class.java
                ))
            }
        findViewById<Button>(R.id.load_after_init)
            .setOnClickListener {
                startActivity(Intent(
                    this,LoadAfterInit::class.java
                ))
            }
        findViewById <Button>(R.id.init_and_load)
                .setOnClickListener {
                    startActivity(Intent(
                        this,InitAndLoad::class.java
                    ))
        }
    }

}