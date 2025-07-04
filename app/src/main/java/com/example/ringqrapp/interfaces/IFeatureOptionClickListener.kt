package com.example.ringqrapp.interfaces

import com.example.ringqrapp.model.FeatureOption

interface IFeatureOptionClickListener {
    fun onNextClick(featureOption: FeatureOption,position:Int)
    fun onSwitchToggle(featureOption: FeatureOption, isChecked: Boolean)
} 