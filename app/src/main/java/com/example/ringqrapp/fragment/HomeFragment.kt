package com.example.ringqrapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.R
import com.example.ringqrapp.adapter.FeatureOptionAdapter
import com.example.ringqrapp.adapter.HealthItemAdapter
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.databinding.FragmentHomeBinding
import com.example.ringqrapp.databinding.FragmentProfileBinding


class HomeFragment : Fragment() {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(homeBinding == null) {
            homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHealthRecyclerView()
    }

    private fun setupHealthRecyclerView()
    {
        val listHealth = FunctionGlobal.listHealth(requireContext())
        val adapterHealth = HealthItemAdapter(listHealth)
        binding.rcvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvCategories.setHasFixedSize(false)

        binding.rcvCategories.adapter = adapterHealth
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeBinding = null
    }
}