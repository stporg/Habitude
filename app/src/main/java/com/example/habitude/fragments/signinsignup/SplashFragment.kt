package com.example.habitude.fragments.signinsignup

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.habitude.R
import com.example.habitude.activities.IntroductionActivity
import com.example.habitude.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment: Fragment(R.layout.fragment_splash) {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        moveToNextActivity()
    }

    /**
     * This function shows the Splash activity for 2.5 second, then moves to the Main/Intro activity,
     * depending if the user is already logged in or not.
     **/
    private fun moveToNextActivity() {
        val delayMillis = 500L

        /*
        Handler(Looper.getMainLooper()) ensures the postDelayed method will run on the main thread,
        which is where UI related code should be executed
        */
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_introFragment2)
        }, delayMillis)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IntroductionActivity) {
            context.makeSplashFullScreen()
        }
    }
}