package com.google.android.gms.example.bannerexample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowMetrics
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.example.bannerexample.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean
private const val TAG = "DelayInitAndLoad"
/** Main Activity. Inflates main activity xml and child fragments. */
class DelayInitAndLoad : AppCompatActivity() {

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val initialLayoutComplete = AtomicBoolean(false)
    private lateinit var binding: ActivityMainBinding
    private lateinit var adView: AdView
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    // Get the ad size with screen width.
    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adView = AdView(this)
        binding.adViewContainer.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d(TAG, "onAdFailedToLoad...${p0.message}")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "onAdLoaded...cost = ${System.currentTimeMillis() - startTime}")
            }
        }

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                // Consent not obtained in current session.
                Log.d(TAG, "${error.errorCode}: ${error.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        binding.adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete.getAndSet(true) && googleMobileAdsConsentManager.canRequestAds) {
                loadBanner()
            }
        }

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("213059401FBA5F1FEEE9B16F516CAB06")).build()
        )
    }

    /** Called when leaving the activity. */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity. */
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called before the activity is destroyed. */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        val moreMenu = menu?.findItem(R.id.action_more)
        moreMenu?.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemView = findViewById<View>(item.itemId)
        PopupMenu(this, menuItemView).apply {
            menuInflater.inflate(R.menu.popup_menu, menu)
            show()
            setOnMenuItemClickListener { popupMenuItem ->
                when (popupMenuItem.itemId) {
                    R.id.privacy_settings -> {
                        // Handle changes to user consent.
                        googleMobileAdsConsentManager.showPrivacyOptionsForm(this@DelayInitAndLoad) { formError ->
                            if (formError != null) {
                                Toast.makeText(
                                    this@DelayInitAndLoad,
                                    formError.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        true
                    }

                    else -> false
                }
            }
            return super.onOptionsItemSelected(item)
        }
    }

    var startTime = 0L
    var initStartTime = 0L
    private fun loadBanner() {
        // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        adView.setAdSize(adSize)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        Log.d(TAG, "start load....")
        startTime = System.currentTimeMillis()
        adView.loadAd(adRequest)
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MainScope().launch {
            Log.d(TAG, "call init....")
            kotlinx.coroutines.delay(10_000)
            initStartTime = System.currentTimeMillis()
            MobileAds.initialize(this@DelayInitAndLoad) {
                it.adapterStatusMap.values.forEach { status ->
                    if (status.initializationState == AdapterStatus.State.READY) {
                        Log.d(TAG, "after init = ${System.currentTimeMillis() - initStartTime}")
                    }
                }
            }
        }

        // Load an ad.
        if (initialLayoutComplete.get()) {
            loadBanner()
        }
    }
}