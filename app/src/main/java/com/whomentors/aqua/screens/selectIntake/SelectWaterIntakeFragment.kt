package com.whomentors.aqua.screens.selectIntake

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.futured.donut.DonutSection
import com.whomentors.aqua.R
import com.whomentors.aqua.database.StatsDatabase
import com.whomentors.aqua.databinding.FragmentSelectWaterIntakeBinding
import com.whomentors.aqua.screens.waterIntake.MainViewModel
import com.whomentors.aqua.screens.waterIntake.MainViewModelFactory
import kotlinx.android.synthetic.main.fragment_select_water_intake.*
import java.lang.Float.min
import kotlin.math.min


/**
 * A simple [Fragment] subclass.
 * Use the [SelectWaterIntakeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectWaterIntakeFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentSelectWaterIntakeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_water_intake, container, false)

        initializeViewModel()
        setupDonut()

        mainViewModel.getTodayEntry().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                updatePercentagePie(it.intake, it.totalIntake)
            }
        })

        setupButtonOnClicks()

        return binding.root
    }

    /**
     * Initialize Main ViewModel
     */
    private fun initializeViewModel() {
        val application = requireNotNull(this.activity).application
        val statsDao = StatsDatabase.getInstance(application).statsDatabaseDao

        val viewModelFactory = MainViewModelFactory(statsDao, application)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    /**
     * Update the percentage and chart.
     */
    private fun updatePercentagePie(intake: Int, totalIntake: Int){
        val percent = min(intake*100/totalIntake, 100).toString()

        if (percent == "100"){
            binding.goalReachedTextView.visibility = View.VISIBLE
        }
        else {
            binding.goalReachedTextView.visibility = View.GONE
        }

        binding.intakePercentTextView.text = percent + "%"

        val waterAmount = DonutSection(
            name = "water_intake",
            color = Color.parseColor("#00796B"),
            amount = percent.toFloat()
        )
        binding.circularProgressBar.submitData(listOf(waterAmount))
    }

    /**
     * Setup onClick Listeners for all the buttons.
     */
    private fun setupButtonOnClicks(){
        binding.quantityOneButton.setOnClickListener{
            mainViewModel.addToTodayIntake(100)
        }
        binding.quantityTwoButton.setOnClickListener{
            mainViewModel.addToTodayIntake(200)
        }
        binding.quantityThreeButton.setOnClickListener{
            mainViewModel.addToTodayIntake(300)
        }
        binding.quantityFourButton.setOnClickListener{
            mainViewModel.addToTodayIntake(500)
        }
    }

    private fun setupDonut(){
        val waterAmount = DonutSection(
            name = "water_intake",
            color = Color.parseColor("#FB1D32"),
            amount = 0f
        )

        binding.circularProgressBar.cap = 100f
        binding.circularProgressBar.submitData(listOf(waterAmount))
    }
}