package com.example.cap.ui.game.linkGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cap.R
import com.example.cap.ui.game.GameFragment

class LinkGameFragment : Fragment(), GameFragment {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_link_game, container, false)
    }
}
