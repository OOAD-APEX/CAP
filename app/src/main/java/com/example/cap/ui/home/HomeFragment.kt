package com.example.cap.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cap.R
import com.example.cap.databinding.FragmentHomeBinding
import com.example.cap.game.GameDialog
import com.example.cap.game.GameDialogObserver
import com.example.cap.ui.fortune.DailyFortuneDialog

class HomeFragment : Fragment() , GameDialogObserver{

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
//        val startGameButton: Button = binding.startGameButton
//        startGameButton.setOnClickListener {
//            val gameDialog = GameDialog(requireContext())
//            gameDialog.addObserver(this)
//            gameDialog.show()
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGameComplete() {
        showDailyFortuneDialog()
    }

    private fun showDailyFortuneDialog() {
        DailyFortuneDialog(requireContext()).show()
    }
}
