package com.whomentors.aqua.screens.WaterIntake

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.whomentors.aqua.Activity.Start
import com.whomentors.aqua.Activity.UserInfo
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.Helpers.Alarm
import com.whomentors.aqua.Helpers.Sqlite
import com.whomentors.aqua.R
import kotlinx.android.synthetic.main.water_activity_main.*


/**
 * A simple [Fragment] subclass.
 * Use the [WaterIntakeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WaterIntakeFragment : Fragment() {

    private var totalIntake: Int = 0
    private var inTook: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: Sqlite
    private lateinit var dateNow: String
    private var notificStatus: Boolean = false
    private var selectedOption: Int? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layoutView = inflater.inflate(R.layout.fragment_water_intake, container, false)
        val context = layoutView.context

//        if (context.getSharedPreferences("user_pref", 0)?.getBoolean("firstrun", true) == true) {
//            findNavController().navigate(R.id.action_waterIntakeFragment_to_userInfoFragment)
//            Log.d("WaterIntakeFragment", "nav waterIntake to userInfo")
//        }

        sharedPref = context.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)
        sqliteHelper = Sqlite(context)

        val mAdView: AdView = layoutView.findViewById(R.id.adView)
        val adRequest =
            AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Load interstitialAd
        // https://developers.google.com/admob/android/interstitial
        val mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = getString(R.string.g_inr)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                Log.d("", "hello")
            }

            override fun onAdOpened() {
                Log.d("", "hello")
            }

            override fun onAdClicked() {
                Log.d("", "hello")
            }

            override fun onAdLeftApplication() {
                Log.d("", "hello")
            }

            override fun onAdClosed() {
                Log.d("", "hello")
            }
        }

        // Goes back to Start Activity
//        btnBack.setOnClickListener {
//            startActivity(Intent(this, Start::class.java))
//            finish()
//        }

        totalIntake = sharedPref.getInt(Thisapp.TOTAL_INTAKE, 0)

        // Why should we go back to UserInfo if the intake is 0 and how would intake go below 0?
        if (totalIntake <= 0) {
            findNavController().navigate(R.id.action_waterIntakeFragment_to_userInfoFragment)
        }

        dateNow = Thisapp.getCurrentDate()!!

        return layoutView
    }

    fun updateValues() {
        totalIntake = sharedPref.getInt(Thisapp.TOTAL_INTAKE, 0)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)
    }

    override fun onStart() {
        super.onStart()

        val outValue = TypedValue()
        context?.applicationContext?.theme?.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValue,
            true
        )

        val context = requireContext()

        // Get notification status from shared preferences
        notificStatus = sharedPref.getBoolean(Thisapp.NOTIFICATION_STATUS_KEY, true)
        val alarm = Alarm()
        if (!context?.let { alarm.checkAlarm(it) }!! && notificStatus) {
            btnNotific.setImageDrawable(requireContext().getDrawable(R.drawable.ic_bell))
            alarm.setAlarm(
                requireContext(),
                sharedPref.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }

        // Change notification icon based on notification status
        if (notificStatus) {
            btnNotific.setImageDrawable(requireContext().getDrawable(R.drawable.ic_bell))
        } else {
            btnNotific.setImageDrawable(requireContext().getDrawable(R.drawable.ic_bell_disabled))
        }

        sqliteHelper.addAll(dateNow, 0, totalIntake)

        updateValues()

        // Add the selected amount to total intake
        // when + fab is pressed
        fabAdd.setOnClickListener {


            if (selectedOption != null) {
                if ((inTook * 100 / totalIntake) <= 140) {

                    if (sqliteHelper.addIntook(dateNow, selectedOption!!) > 0) {
                        inTook += selectedOption!!
                        setWaterLevel(inTook, totalIntake)

                        Snackbar.make(it, "Your water intake was saved...!!", Snackbar.LENGTH_SHORT)
                            .show()

                    }
                } else {
                    Snackbar.make(it, "You already achieved the goal", Snackbar.LENGTH_SHORT).show()
                }
                selectedOption = null
                t6.text = "Custom"
                op50ml.background = context.getDrawable(outValue.resourceId)
                op100ml.background = context.getDrawable(outValue.resourceId)
                op150ml.background = context.getDrawable(outValue.resourceId)
                op200ml.background = context.getDrawable(outValue.resourceId)
                op250ml.background = context.getDrawable(outValue.resourceId)
                opCustom.background = context.getDrawable(outValue.resourceId)
            } else {
                YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(carddd)
                Snackbar.make(it, "Please select an option", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnNotific.setOnClickListener {
            notificStatus = !notificStatus
            sharedPref.edit().putBoolean(Thisapp.NOTIFICATION_STATUS_KEY, notificStatus).apply()
            if (notificStatus) {
                btnNotific.setImageDrawable(context!!.getDrawable(R.drawable.ic_bell))
                Snackbar.make(it, "Notification Enabled..", Snackbar.LENGTH_SHORT).show()
                alarm.setAlarm(
                    context!!,
                    sharedPref.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                )
            } else {
                btnNotific.setImageDrawable(context!!.getDrawable(R.drawable.ic_bell_disabled))
                Snackbar.make(it, "Notification Disabled..", Snackbar.LENGTH_SHORT).show()
                alarm.cancelAlarm(context!!)
            }
        }



        op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 50
            op50ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            op100ml.background = context!!.getDrawable(outValue.resourceId)
            op150ml.background = context!!.getDrawable(outValue.resourceId)
            op200ml.background = context!!.getDrawable(outValue.resourceId)
            op250ml.background = context!!.getDrawable(outValue.resourceId)
            opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 100
            op50ml.background = context!!.getDrawable(outValue.resourceId)
            op100ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            op150ml.background = context!!.getDrawable(outValue.resourceId)
            op200ml.background = context!!.getDrawable(outValue.resourceId)
            op250ml.background = context!!.getDrawable(outValue.resourceId)
            opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 150
            op50ml.background = context!!.getDrawable(outValue.resourceId)
            op100ml.background = context!!.getDrawable(outValue.resourceId)
            op150ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            op200ml.background = context!!.getDrawable(outValue.resourceId)
            op250ml.background = context!!.getDrawable(outValue.resourceId)
            opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 200
            op50ml.background = context!!.getDrawable(outValue.resourceId)
            op100ml.background = context!!.getDrawable(outValue.resourceId)
            op150ml.background = context!!.getDrawable(outValue.resourceId)
            op200ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            op250ml.background = context!!.getDrawable(outValue.resourceId)
            opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 250
            op50ml.background = context!!.getDrawable(outValue.resourceId)
            op100ml.background = context!!.getDrawable(outValue.resourceId)
            op150ml.background = context!!.getDrawable(outValue.resourceId)
            op200ml.background = context!!.getDrawable(outValue.resourceId)
            op250ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        opCustom.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }

            val li = LayoutInflater.from(context)
            val promptsView = li.inflate(R.layout.custom_input_dialog, null)

            val alertDialogBuilder = AlertDialog.Builder(context!!)
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                .findViewById(R.id.etCustomInput) as TextInputLayout

            alertDialogBuilder.setPositiveButton("OK") { dialog, id ->
                val inputText = userInput.editText!!.text.toString()
                if (!TextUtils.isEmpty(inputText)) {
                    t6.text = "${inputText} ml"
                    selectedOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            op50ml.background = context!!.getDrawable(outValue.resourceId)
            op100ml.background = context!!.getDrawable(outValue.resourceId)
            op150ml.background = context!!.getDrawable(outValue.resourceId)
            op200ml.background = context!!.getDrawable(outValue.resourceId)
            op250ml.background = context!!.getDrawable(outValue.resourceId)
            opCustom.background = context!!.getDrawable(R.drawable.option_select_bg)

        }

    }


    private fun setWaterLevel(inTook: Int, totalIntake: Int) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(tvIntook)
        tvIntook.text = "$inTook"
        tvTotalIntake.text = "$totalIntake ml"
        val progress = ((inTook / totalIntake.toFloat()) * 100).toInt()
        YoYo.with(Techniques.Pulse)
            .duration(500)
            .playOn(intakeProgress)
        intakeProgress.currentProgress = progress
        if ((inTook * 100 / totalIntake) > 140) {
            Snackbar.make(main_activity_parent, "You achieved the goal", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

}