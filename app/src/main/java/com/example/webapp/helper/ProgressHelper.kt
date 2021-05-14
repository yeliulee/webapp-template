package com.example.webapp.helper

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import android.widget.ProgressBar

/**
 * 进度条 Helper
 * @author yeliulee
 * Created at 2021/5/14 17:48
 */
class ProgressHelper(private val mProgressBar: ProgressBar) {
    private var animator: ObjectAnimator? = null
    private var lastProgress: Int = 0

    fun progress(newProgress: Int) {
        if (newProgress == lastProgress) {
            return
        }
        if (newProgress < lastProgress) {
            mProgressBar.progress = 0
        }
        lastProgress = newProgress
        if (animator != null && animator!!.isRunning) {
            animator!!.end()
            animator = null
        }
        Log.d("wtr", "progress:$newProgress")
        if (newProgress < 100) {
            mProgressBar.visibility = View.VISIBLE
            animator = ObjectAnimator.ofInt(mProgressBar, "progress", mProgressBar.progress, newProgress)
            animator?.duration = 400
            animator?.start()
        } else {
            animator = ObjectAnimator.ofInt(mProgressBar, "progress", mProgressBar.progress, newProgress)
            animator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    mProgressBar.visibility = View.GONE
                    mProgressBar.progress = 0
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            animator?.duration = 400
            animator?.start()
        }
    }
}
