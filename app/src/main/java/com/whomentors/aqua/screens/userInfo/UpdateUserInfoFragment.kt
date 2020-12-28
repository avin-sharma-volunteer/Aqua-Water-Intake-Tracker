package com.whomentors.aqua.screens.userInfo

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.whomentors.aqua.AppUtils.Thisapp
import com.whomentors.aqua.Helpers.Alarm
import com.whomentors.aqua.Helpers.Sqlite
import com.whomentors.aqua.R
import com.whomentors.aqua.databinding.FragmentUpdateUserInfoBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateUserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateUserInfoFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences
    private var weight: String = ""
    private var workTime: String = ""
    private var customTarget: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private var notificMsg: String = ""
    private var Constant: String = "%02d:%02d"

    // DataBinding object
    private lateinit var binding: FragmentUpdateUserInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_user_info, container, false)
        val context = binding.root.context

        val etWakeUpTime: TextInputLayout = binding.etWakeUpTime
        val etSleepTime: TextInputLayout = binding.etSleepTime
        val etWeight: TextInputLayout = binding.etWeight
        val etWorkTime: TextInputLayout = binding.etWorkTime
        val etTarget: TextInputLayout = binding.etTarget
        val etNotificationText: TextInputLayout = binding.etNotificationText
        val btnUpdate: Button = binding.btnUpdate

        sharedPref = context.getSharedPreferences(Thisapp.USERS_SHARED_PREF, Thisapp.PRIVATE_MODE)

        // Grab values from shared preferences and display them in the EditText
        etWeight.editText!!.setText("" + sharedPref.getInt(Thisapp.WEIGHT_KEY, 0))
        etWorkTime.editText!!.setText("" + sharedPref.getInt(Thisapp.WORK_TIME_KEY, 0))
        etTarget.editText!!.setText("" + sharedPref.getInt(Thisapp.TOTAL_INTAKE, 0))
        etNotificationText.editText!!.setText(
            sharedPref.getString(
                Thisapp.NOTIFICATION_MSG_KEY,
                "Hey...Let's drink some water"
            )
        )

        wakeupTime = sharedPref.getLong(Thisapp.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(Thisapp.SLEEPING_TIME_KEY, 1558369800000)
        val cal = Calendar.getInstance()
        cal.timeInMillis = wakeupTime
        etWakeUpTime.editText!!.setText(
            String.format(
                Constant,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )
        cal.timeInMillis = sleepingTime
        etSleepTime.editText!!.setText(
            String.format(
                Constant,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )

        // Set onClick listeners

        // Show timePicker dialog when wakeupTime is clicked
        etWakeUpTime.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

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
                        String.format(Constant, selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }

        // Show timePicker dialog when sleepTime is clicked
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

                    // Assign selected time to the wakeupTime
                    // variable to be saved later
                    sleepingTime = time.timeInMillis

                    etSleepTime.editText!!.setText(
                        String.format(Constant, selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        // Perform checks on fields and save data when
        // Update is clicked
        btnUpdate.setOnClickListener {

            val currentTarget = sharedPref.getInt(Thisapp.TOTAL_INTAKE, 0)

            weight = etWeight.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()
            notificMsg = etNotificationText.editText!!.text.toString()
            customTarget = etTarget.editText!!.text.toString()

            // Check all the required fields are not empty
            when {
                TextUtils.isEmpty(notificMsg) -> Toast.makeText(
                    context,
                    "Please a notification message",
                    Toast.LENGTH_SHORT
                ).show()


                TextUtils.isEmpty(weight) -> Toast.makeText(
                    context, "Please input your weight", Toast.LENGTH_SHORT
                ).show()
                weight.toInt() > 200 || weight.toInt() < 20 -> Toast.makeText(
                    context,
                    "Please input a valid weight",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(workTime) -> Toast.makeText(
                    context,
                    "Please input your workout time",
                    Toast.LENGTH_SHORT
                ).show()
                workTime.toInt() > 500 || workTime.toInt() < 0 -> Toast.makeText(
                    context,
                    "Please input a valid workout time",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(customTarget) -> Toast.makeText(
                    context,
                    "Please input your custom target",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    // If the fields are not empty then
                    // save the data in shared preferences
                    val editor = sharedPref.edit()
                    editor.putInt(Thisapp.WEIGHT_KEY, weight.toInt())
                    editor.putInt(Thisapp.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(Thisapp.WAKEUP_TIME, wakeupTime)
                    editor.putLong(Thisapp.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putString(Thisapp.NOTIFICATION_MSG_KEY, notificMsg)


                    val sqliteHelper =
                        Sqlite(context)

                    // If the new target is not equal to previous target
                    // save the new target in the database
                    if (currentTarget != customTarget.toInt()) {
                        editor.putInt(Thisapp.TOTAL_INTAKE, customTarget.toInt())

                        sqliteHelper.updateTotalIntake(
                            Thisapp.getCurrentDate()!!,
                            customTarget.toInt()
                        )
                    } else {
                        // If the new target is equal to previous target
                        // calculate intake based on weight and workout time
                        // and save the calculated intake to the database.

                        // why calculate a new value when a value already exists?
                        // I don't know

                        val totalIntake = Thisapp.calculateIntake(weight.toInt(), workTime.toInt())
                        val df = DecimalFormat("#")
                        df.roundingMode = RoundingMode.CEILING
                        editor.putInt(Thisapp.TOTAL_INTAKE, df.format(totalIntake).toInt())

                        sqliteHelper.updateTotalIntake(
                            Thisapp.getCurrentDate()!!,
                            df.format(totalIntake).toInt()
                        )
                    }

                    editor.apply()

                    // Cancel the previous notification alarm and
                    // create a new alarm with the new notification frequency
                    Toast.makeText(context, "Values updated successfully", Toast.LENGTH_SHORT).show()
                    val alarmHelper =
                        Alarm()
                    alarmHelper.cancelAlarm(context)
                    alarmHelper.setAlarm(
                        context,
                        sharedPref.getInt(Thisapp.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                    )
                    it.findNavController().navigate(R.id.action_updateUserInfoFragment_to_waterIntakeFragment)

                }
            }
        }
        return binding.root
    }
}