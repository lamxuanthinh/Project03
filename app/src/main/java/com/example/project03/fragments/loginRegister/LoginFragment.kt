package com.example.project03.fragments.loginRegister


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project03.R
import com.example.project03.activities.AdminMainActivity
import com.example.project03.activities.ShoppingActivity
import com.example.project03.databinding.FragmentLoginBinding
import com.example.project03.dialog.setupBottomSheetDialog
import com.example.project03.viewmodel.LoginViewModel
import com.example.project03.util.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment22)
        }
        binding.btnRegisterAd.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerAdminFragment)
        }
        binding.apply {
            buttonLoginLogin.setOnClickListener {
                val email = edEmailLogin.text.toString()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }

        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(), "Reset link was send to your Email", Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error : ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        binding.buttonLoginLogin.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginad.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        Intent(requireActivity(), AdminMainActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

    }
}
