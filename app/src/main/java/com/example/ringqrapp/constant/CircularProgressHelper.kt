package com.example.ringqrapp.constant

import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.ringqrapp.R

object CircularProgressHelper {

    fun setupCircularProgress(
        progressView: View,
        valueText: android.widget.TextView,
        labelText: android.widget.TextView,
        targetText: android.widget.TextView,
        value: String,
        label: String,
        target: String,
        progress: Int,
        color: Int
    ) {
        // Update text values
        valueText.text = value
        labelText.text = label
        targetText.text = target

        // Update progress color
        val drawable = ContextCompat.getDrawable(
            progressView.context,
            R.drawable.circle_progress_foreground
        ) as? GradientDrawable

        drawable?.let {
            it.setColor(ContextCompat.getColor(progressView.context, color))
            progressView.background = it
        }

        // Animate progress
        animateProgress(progressView, progress)
    }

    private fun animateProgress(progressView: View, targetProgress: Int) {
        val animator = ObjectAnimator.ofFloat(progressView, "rotation", 0f, (targetProgress * 3.6f))
        animator.duration = 1500
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }
}