package com.whomentors.aqua.screens.WaterIntake

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whomentors.aqua.R


/**
 * A simple [Fragment] subclass.
 * Use the [WaterIntakeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WaterIntakeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_water_intake, container, false)
    }

}