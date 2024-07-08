package com.google.android.gms.example.bannerexample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdInspectorError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnAdInspectorClosedListener
import com.google.android.gms.ads.RequestConfiguration

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mainactivity)
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("213059401FBA5F1FEEE9B16F516CAB06","B8E2AB6693399122C45708937B03E0EA")).build()
        )

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


        findViewById <Button>(R.id.delay_init_and_load)
            .setOnClickListener {
                startActivity(Intent(
                    this,DelayInitAndLoad::class.java
                ))
            }

        findViewById <Button>(R.id.open_inspector)
            .setOnClickListener {
               MobileAds.openAdInspector(this@MainActivity
               ) {
                  Log.d("wdw","${it?.message}")
               }
            }
    }

}