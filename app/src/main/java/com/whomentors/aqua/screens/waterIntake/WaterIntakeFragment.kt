package com.whomentors.aqua.screens.waterIntake

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.Helpers.Alarm
import com.whomentors.aqua.Helpers.Sqlite
import com.whomentors.aqua.MainActivity
import com.whomentors.aqua.R
import com.whomentors.aqua.databinding.FragmentWaterIntakeBinding
import com.whomentors.aqua.databinding.FragmentWaterIntakeUpdatedBinding
import kotlinx.android.synthetic.main.fragment_water_intake_updated.*


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

    private lateinit var binding: FragmentWaterIntakeUpdatedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentWaterIntakeUpdatedBinding>(inflater, R.layout.fragment_water_intake_updated, container, false)

        val context = binding.root.context


        sharedPref = context.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)
        sqliteHelper = Sqlite(context)

        val mAdView: AdView = binding.adView
        val adRequest =
            AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Load interstitialAd
        // https://developers.google.com/admob/android/interstitial
        val mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = getString(R.string.g_inr)
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
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

        dateNow = Thisapp.getCurrentDate()

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Create options menu, bell icon in our case.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)

        // Check notification status
        notificStatus = sharedPref.getBoolean(Thisapp.NOTIFICATION_STATUS_KEY, true)

        // Change notification icon based on notification status
        if (notificStatus) {
            menu.getItem(0).setIcon(R.drawable.ic_bell)
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_bell_disabled)
        }

    }

    /**
     * Handle item clicks. We handle clicks on bell icon here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_notification) {
            notificStatus = !notificStatus
            sharedPref.edit().putBoolean(Thisapp.NOTIFICATION_STATUS_KEY, notificStatus).apply()

            val alarm = Alarm()
            if (notificStatus) {
                item.setIcon(R.drawable.ic_bell)
                Snackbar.make((activity as MainActivity).findViewById(R.id.action_notification), "Notification Enabled..", Snackbar.LENGTH_SHORT).show()
                context?.let {
                    alarm.setAlarm(
                        it,
                        sharedPref.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                    )
                }
            } else {
                item.setIcon(R.drawable.ic_bell_disabled)
                Snackbar.make((activity as MainActivity).findViewById(R.id.action_notification), "Notification Disabled..", Snackbar.LENGTH_SHORT).show()
                context?.let { alarm.cancelAlarm(it) }
            }
            return true
        }

        return super.onOptionsItemSelected(item)
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
            alarm.setAlarm(
                requireContext(),
                sharedPref.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
            )
        }


        sqliteHelper.addAll(dateNow, 0, totalIntake)

        updateValues()

        // Add the selected amount to total intake
        // when + fab is pressed
        binding.addWaterFab.setOnClickListener {


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
                binding.t6.text = "Custom"
                binding.op50ml.background = context.getDrawable(outValue.resourceId)
                binding.op100ml.background = context.getDrawable(outValue.resourceId)
                binding.op150ml.background = context.getDrawable(outValue.resourceId)
                binding.op200ml.background = context.getDrawable(outValue.resourceId)
                binding.op250ml.background = context.getDrawable(outValue.resourceId)
                binding.opCustom.background = context.getDrawable(outValue.resourceId)
            } else {
                YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(binding.carddd)
                Snackbar.make(it, "Please select an option", Snackbar.LENGTH_SHORT).show()
            }
        }




        binding.op50ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 50
            binding.op50ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            binding.op100ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op150ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op200ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op250ml.background = context!!.getDrawable(outValue.resourceId)
            binding.opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        binding.op100ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 100
            binding.op50ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op100ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            binding.op150ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op200ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op250ml.background = context!!.getDrawable(outValue.resourceId)
            binding.opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        binding.op150ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 150
            binding.op50ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op100ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op150ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            binding.op200ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op250ml.background = context!!.getDrawable(outValue.resourceId)
            binding.opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        binding.op200ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 200
            binding.op50ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op100ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op150ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op200ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            binding.op250ml.background = context!!.getDrawable(outValue.resourceId)
            binding.opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        binding.op250ml.setOnClickListener {
            if (snackbar != null) {
                snackbar?.dismiss()
            }
            selectedOption = 250
            binding.op50ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op100ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op150ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op200ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op250ml.background = context!!.getDrawable(R.drawable.option_select_bg)
            binding.opCustom.background = context!!.getDrawable(outValue.resourceId)

        }

        binding.opCustom.setOnClickListener {
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
                    binding.t6.text = "${inputText} ml"
                    selectedOption = inputText.toInt()
                }
            }.setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            binding.op50ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op100ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op150ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op200ml.background = context!!.getDrawable(outValue.resourceId)
            binding.op250ml.background = context!!.getDrawable(outValue.resourceId)
            binding.opCustom.background = context!!.getDrawable(R.drawable.option_select_bg)

        }

    }


    private fun setWaterLevel(inTook: Int, totalIntake: Int) {

        YoYo.with(Techniques.SlideInDown)
            .duration(500)
            .playOn(binding.tvIntake)
        binding.tvIntake.text = "$inTook"
        binding.tvTotalIntake.text = "$totalIntake ml"
//        val progress = ((inTook / totalIntake.toFloat()) * 100).toInt()
//        YoYo.with(Techniques.Pulse)
//            .duration(500)
//            .playOn(binding.intakeProgress)
//        binding.intakeProgress.currentProgress = progress

        val progress = inTook.toFloat()/totalIntake.toFloat()
        Log.d("WaterIntakeFragment", (0.5f * progress).toString())
        val set = ConstraintSet()
        set.clone(parent_constraint_layout)
        set.constrainPercentHeight(R.id.bottle_progress_view, 0.5f * progress)
        set.setVerticalBias(bottle_progress_view.id, .93f)
        set.applyTo(parent_constraint_layout)

        if ((inTook * 100 / totalIntake) > 140) {
            Snackbar.make(binding.root, "You achieved the goal", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

}