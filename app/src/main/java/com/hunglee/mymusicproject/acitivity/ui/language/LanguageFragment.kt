package com.hunglee.mymusicproject.acitivity.ui.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hunglee.mymusicproject.databinding.FragmentLanguageBinding

class LanguageFragment : Fragment() {

    private lateinit var languageViewModel: LanguageViewModel
    private var _binding: FragmentLanguageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        languageViewModel =
            ViewModelProvider(this).get(LanguageViewModel::class.java)

        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textLanguage
        languageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}