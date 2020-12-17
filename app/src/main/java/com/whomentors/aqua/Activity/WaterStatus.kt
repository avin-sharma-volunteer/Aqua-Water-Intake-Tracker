package com.whomentors.aqua.Activity

import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.whomentors.aqua.R
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.whomentors.aqua.Helpers.Sqlite
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.AppUtils.Chartres
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_stats.*

/**
 * Activity that shows stats.
 */
class WaterStatus : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: Sqlite
    private var totalPercentage: Float = 0f
    private var totalGlasses: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        sharedPref = getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)
        sqliteHelper = Sqlite(this)

        // Load interstitialAd
        // https://developers.google.com/admob/android/interstitial
        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.g_inr)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("","hjh");
            }

            override fun onAdOpened() {
                Log.d("","hjh");
            }

            override fun onAdClicked() {
                Log.d("","hjh");
            }

            override fun onAdLeftApplication() {
                Log.d("","hjh");
            }

            override fun onAdClosed() {
                Log.d("","hello")
            }
        }
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest =
            AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        // Finish the activity when back is clicked.
        btnBack.setOnClickListener {
            finish()
        }

        val entries = ArrayList<Entry>()
        val dateArray = ArrayList<String>()

        val cursor: Cursor = sqliteHelper.getAllStats()

        // get all the stats and add data to Entry object
        if (cursor.moveToFirst()) {

            for (i in 0 until cursor.count) {
                dateArray.add(cursor.getString(1))
                val percent = cursor.getInt(2) / cursor.getInt(3).toFloat() * 100
                totalPercentage += percent
                totalGlasses += cursor.getInt(2)
                entries.add(Entry(i.toFloat(), percent))
                cursor.moveToNext()
            }

        } else {
            Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
        }

        // If entries are not empty then create chart
        if (!entries.isEmpty()) {

            chart.description.isEnabled = false
            chart.animateY(1000, Easing.Linear)
            chart.viewPortHandler.setMaximumScaleX(1.5f)
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.position = XAxis.XAxisPosition.TOP
            chart.xAxis.isGranularityEnabled = true
            chart.legend.isEnabled = false
            chart.fitScreen()
            chart.isAutoScaleMinMaxEnabled = true
            chart.scaleX = 1f
            chart.setPinchZoom(true)
            chart.isScaleXEnabled = true
            chart.isScaleYEnabled = false
            chart.axisLeft.textColor = Color.BLACK
            chart.xAxis.textColor = Color.BLACK
            chart.axisLeft.setDrawAxisLine(false)
            chart.xAxis.setDrawAxisLine(false)
            chart.setDrawMarkers(false)
            chart.xAxis.labelCount = 5
            val rightAxix = chart.axisRight
            rightAxix.setDrawGridLines(false)
            rightAxix.setDrawZeroLine(false)
            rightAxix.setDrawAxisLine(false)
            rightAxix.setDrawLabels(false)

            val dataSet = LineDataSet(entries, "Label")
            dataSet.setDrawCircles(false)
            dataSet.lineWidth = 2.5f
            dataSet.color = ContextCompat.getColor(this,
                R.color.colorPrimary
            )
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable = getDrawable(R.drawable.graph_fill_gradiant)
            dataSet.setDrawValues(false)
            dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

            val lineData = LineData(dataSet)
            chart.xAxis.valueFormatter = (Chartres(
                dateArray
            ))
            chart.data = lineData
            chart.invalidate()

            val remaining = sharedPref.getInt(
                Thisapp.TOTAL_INTAKE,
                0
            ) - sqliteHelper.getIntook(Thisapp.getCurrentDate()!!)

            if (remaining > 0) {
                remainingIntake.text = "$remaining ml"
            } else {
                remainingIntake.text = "0 ml"
            }



            targetIntake.text = "${sharedPref.getInt(Thisapp.TOTAL_INTAKE, 0)} ml"

            val percentage = sqliteHelper.getIntook(Thisapp.getCurrentDate()!!) * 100 / sharedPref.getInt(
                Thisapp.TOTAL_INTAKE,
                0
            )
            waterLevelView.centerTitle = "$percentage%"
            waterLevelView.progressValue = percentage

        }
    }
}
