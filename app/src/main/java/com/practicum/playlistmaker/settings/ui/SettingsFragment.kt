package com.practicum.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: FragmentSettingsBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val themeSwitcher = binding.themeSwitcher
        val sharingButton = binding.sharingButton
        val supportButton = binding.supportButton
        val termsOfUseButton = binding.termsOfUse

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)

        val offerIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.offer)))

        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(getString(R.string.support_mail))
        )
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
        supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))

        viewModel.observeSwitchState().observe(viewLifecycleOwner) { switcherIsChecked ->
            themeSwitcher.setChecked(switcherIsChecked)
            viewModel.setTheme()
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.putSwitchState(checked)
        }

        sharingButton.setOnClickListener {
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            startActivity(supportIntent)
        }

        termsOfUseButton.setOnClickListener {
            startActivity(offerIntent)
        }

        return binding.root
    }
}