package com.whomentors.aqua.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.whomentors.aqua.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.startscreen.*


class Start : AppCompatActivity() {

    var start: TextView? = null
    var share: ImageView? = null
    var rate: ImageView? = null
    var privacy: ImageView? = null
    private var nativeAd: UnifiedNativeAd? = null

    /**
     * Find and populate the views in the ad.
     * Views are found in adView and data is
     * found in nativeAd.
     */
    private fun populateUnifiedNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        // If nativeAd is empty make the views invisible otherwise
        // make them visible and fill them in with the data.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)

         nativeAd.videoController
    }

    /**
     * Creates a native ad on app exit dialog. Native ads
     * can have custom layout to better fit the app theme.
     */
    private fun refreshAd(dialog: Dialog) {
        val builder = AdLoader.Builder(this, getString(R.string.g_native))
        builder.forUnifiedNativeAd { unifiedNativeAd ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            val frameLayout =
                dialog.findViewById<FrameLayout>(R.id.f2_adplaceholderr)
            val adView = layoutInflater
                .inflate(R.layout.ad_unified2, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            frameLayout.removeAllViews()
            frameLayout.addView(adView)
        }
        val adOptions = NativeAdOptions.Builder().build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("","hello")
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startscreen)

        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest =
            AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // onClickListener for "Let's start" button
        start = findViewById<View>(R.id.start) as TextView
        start!!.setOnClickListener {
            val intent = Intent(this, MainWater::class.java)
            startActivity(intent)
        }

        // onClickListener for "Rate App" button
        rate = findViewById<View>(R.id.rate) as ImageView
        rate!!.setOnClickListener {

            // Try opening app on the PlayStore if its installed otherwise open the
            // PlayStore url
            try {
                startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (unused: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }

        // onClickListener for "Share App" button
        share = findViewById<View>(R.id.share) as ImageView
        share!!.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=" + getPackageName()
            )
            intent.type = "text/plain"
            startActivity(intent)
        }

        // onClickListener for "Privacy policy" button
        privacy = findViewById<View>(R.id.privacy) as ImageView
        privacy!!.setOnClickListener {
            val url = "https://www.google.com/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        // Show stats screen when stats button is pressed.
        btnStats.setOnClickListener {
            startActivity(Intent(this, WaterStatus::class.java))
        }

        // Show update user info screen when update user info button is pressed.
        btnMenu.setOnClickListener {
            startActivity(Intent(this, UpdateUserInfo::class.java))
        }


    }

    override fun onBackPressed() {

        // Show exit dialog with the ad.
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.exit_dialog)

        refreshAd(dialog);

        (dialog.findViewById<View>(R.id.btnno) as Button).setOnClickListener { dialog.dismiss() }

        (dialog.findViewById<View>(R.id.btnyes) as Button).setOnClickListener {
            dialog.dismiss()
            this.finish()
            System.exit(1)
        }
        dialog.show()
    }


}
