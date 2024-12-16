package com.example.sibantubaru

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sibantubaru.adapter.OnboardingAdapter
import com.example.sibantubaru.databinding.ActivityOnboardingBinding
import com.example.sibantubaru.model.OnboardingItem

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val onboardingItems = listOf(
        OnboardingItem(
            image = R.drawable.onboarding1,
            title = "Welcome to SiBantu!",
            subtitle = "Find trusted service providers for your home needs, quickly and easily. Your home repair solutions are just a few taps away!"
        ),
        OnboardingItem(
            image = R.drawable.onboarding2,
            title = "Choose Your Service",
            subtitle = "From AC repairs to electrical installations, we’ve got you covered. Select the right service based on your home’s needs."
        ),
        OnboardingItem(
            image = R.drawable.onboarding3,
            title = "Fast & Secure Process",
            subtitle = "Get connected with nearby pros using interactive maps. Enjoy safe payments and seamless service every time."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPagerOnboarding.adapter = onboardingAdapter

        // Setup progress indicator
        binding.dotsIndicator.attachTo(binding.viewPagerOnboarding)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavigationButtons(position)
            }
        })

        binding.btnSkip.setOnClickListener {
            navigateToLogin()
        }

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPagerOnboarding.currentItem
            if (currentItem < onboardingItems.size - 1) {
                binding.viewPagerOnboarding.currentItem = currentItem + 1
            } else {
                navigateToLogin()
            }
        }
    }

    private fun updateNavigationButtons(position: Int) {
        binding.btnSkip.isEnabled = position < onboardingItems.size - 1
        binding.btnSkip.visibility = if (position < onboardingItems.size - 1) View.VISIBLE else View.INVISIBLE

        binding.btnNext.text = if (position == onboardingItems.size - 1) "Start with SiBantu" else "Next"
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}