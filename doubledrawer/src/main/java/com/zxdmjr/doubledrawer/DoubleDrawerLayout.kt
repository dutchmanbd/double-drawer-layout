package com.zxdmjr.doubledrawer

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ViewSwitcher


class DoubleDrawerLayout(
    context: Context,
    attrs: AttributeSet?
) : ViewSwitcher(context, attrs) {

    companion object{
        private val NONE = -1
        private val MAIN_VIEW_INDEX = 0
        private val DRAWER_VIEW_INDEX = 1
    }

    private var slideInAnimation: Animation? = null
    private  var slideOutAnimation:Animation? = null
    private  var noAnimation:Animation? = null
    private var animating = false

    private val listener: AnimationListener = object : AnimationListener {
        override fun onAnimationEnd(anim: Animation) {
            animating = false
        }
        override fun onAnimationStart(anim: Animation) {}
        override fun onAnimationRepeat(anim: Animation) {}
    }

    init {
        slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
        slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
        noAnimation = AnimationUtils.loadAnimation(context, R.anim.none)
        noAnimation?.setAnimationListener(listener)
    }

    fun openInnerDrawer() {
        if (displayedChild != DRAWER_VIEW_INDEX) {
            setChildAndAnimate(DRAWER_VIEW_INDEX, true)
        }
    }

    fun closeInnerDrawer() {
        if (displayedChild != MAIN_VIEW_INDEX) {
            setChildAndAnimate(MAIN_VIEW_INDEX, true)
        }
    }

    fun isInnerDrawerOpen(): Boolean {
        return displayedChild == DRAWER_VIEW_INDEX
    }

    private fun setChildAndAnimate(whichChild: Int, doAnimate: Boolean) {
        if (doAnimate) {
            setAnimationForChild(whichChild)
        } else {
            setAnimationForChild(NONE)
        }
        animating = doAnimate
        displayedChild = whichChild
    }

    private fun setAnimationForChild(whichChild: Int) {
        when (whichChild) {
            DRAWER_VIEW_INDEX -> {
                inAnimation = slideInAnimation
                outAnimation = noAnimation
            }
            MAIN_VIEW_INDEX -> {
                inAnimation = noAnimation
                outAnimation = slideOutAnimation
            }
            else -> {
                inAnimation = null
                outAnimation = null
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (animating) {
            true
        } else {
            super.onInterceptTouchEvent(ev)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.whichChild = displayedChild
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss: SavedState = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        setChildAndAnimate(ss.whichChild, false)
    }

    private class SavedState : BaseSavedState {
        var whichChild = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            whichChild = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(whichChild)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }


}