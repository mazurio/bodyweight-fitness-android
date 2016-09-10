package com.bodyweight.fitness.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.github.paolorotolo.appintro.AppIntro
import com.bodyweight.fitness.R

class Slide : Fragment() {
    private var layoutResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments.containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = arguments.getInt(ARG_LAYOUT_RES_ID)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(layoutResId, container, false)
    }

    companion object {
        private val ARG_LAYOUT_RES_ID = "layoutResId"

        fun with(layoutResId: Int): Slide {
            val sampleSlide = Slide()

            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            sampleSlide.arguments = args

            return sampleSlide
        }
    }
}

class IntroductionActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(Slide.with(R.layout.introduction_slide1))
        addSlide(Slide.with(R.layout.introduction_slide2))
        addSlide(Slide.with(R.layout.introduction_slide3))
        addSlide(Slide.with(R.layout.introduction_slide4))
        addSlide(Slide.with(R.layout.introduction_slide5))
        addSlide(Slide.with(R.layout.introduction_slide6))

        showSkipButton(false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        loadMainActivity()
    }

    fun loadMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))

        finish()
    }
}