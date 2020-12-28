package com.whomentors.aqua.screens.userInfo

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.R
import kotlinx.android.synthetic.main.fragment_user_info.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserInfoFragment : Fragment() {

    private var weight: String = ""
    private var workTime: String = ""
    private var name: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private lateinit var sharedPref: SharedPreferences
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layoutView = inflater.inflate(R.layout.fragment_user_info, container, false)
        val context = layoutView.context


        val etWakeUpTime: TextInputLayout = layoutView.findViewById(R.id.etWakeUpTime)
        val etSleepTime: TextInputLayout = layoutView.findViewById(R.id.etSleepTime);
        val btnContinue: Button = layoutView.findViewById(R.id.btnContinue)
        val etWeight: TextInputLayout = layoutView.findViewById(R.id.etWeight)
        val etName: TextInputLayout = layoutView.findViewById(R.id.etName)
        val etWorkTime: TextInputLayout = layoutView.findViewById(R.id.etWorkTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        // Find and initialize AdView
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



        // Fetch wakeup and sleeping time from shared preferences

        sharedPref = context.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)

        wakeupTime = sharedPref.getLong(Thisapp.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(Thisapp.SLEEPING_TIME_KEY, 1558369800000)

        etWakeUpTime.editText!!.setOnClickListener {
            // Create a calendar and set it's time to
            // saved wakeup time
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            // Create a timerDialog and set it's time to
            // saved wakeup time
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)

                    // Assign selected time to the wakeupTime
                    // variable to be saved later
                    wakeupTime = time.timeInMillis

                    etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }

        // Display sleep time
        etSleepTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)

                    // Assign selected time to the sleepTime
                    // variable to be saved later
                    sleepingTime = time.timeInMillis

                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        btnContinue.setOnClickListener {

            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(init_user_info_parent_layout.windowToken, 0)

            weight = etWeight.editText!!.text.toString()
            name = etName.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()

            // Do empty checks
            when {
                TextUtils.isEmpty(name) -> Snackbar.make(
                    layoutView,
                    "Please Enter Your Name",
                    Snackbar.LENGTH_SHORT
                ).show()

                TextUtils.isEmpty(weight) -> Snackbar.make(
                    layoutView,
                    "Please input your weight",
                    Snackbar.LENGTH_SHORT
                ).show()
                weight.toInt() > 200 || weight.toInt() < 20 -> Snackbar.make(
                    layoutView,
                    "Please input a valid weight",
                    Snackbar.LENGTH_SHORT
                ).show()

                TextUtils.isEmpty(workTime) -> Snackbar.make(
                    layoutView,
                    "Please input your workout time",
                    Snackbar.LENGTH_SHORT
                ).show()

                workTime.toInt() > 500 || workTime.toInt() < 0 -> Snackbar.make(
                    layoutView,
                    "Please input a valid workout time",
                    Snackbar.LENGTH_SHORT
                ).show()


                else -> {
                    // If all the required fields are filled save
                    // the values to shared preferences

                    val editor = sharedPref.edit()
                    editor.putInt(Thisapp.WEIGHT_KEY, weight.toInt())
                    editor.putInt(Thisapp.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(Thisapp.WAKEUP_TIME, wakeupTime)
                    editor.putLong(Thisapp.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putBoolean(Thisapp.FIRST_RUN_KEY, false)

                    val totalIntake = Thisapp.calculateIntake(weight.toInt(), workTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(Thisapp.TOTAL_INTAKE, df.format(totalIntake).toInt())

                    editor.apply()

                    it.findNavController().navigate(R.id.action_userInfoFragment_to_waterIntakeFragment)

                }
            }
        }
        return layoutView
    }

}