package com.example.hiltconditionalnavigation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.example.hiltconditionalnavigation.R
import com.example.hiltconditionalnavigation.data.result.ResultObserver
import com.example.hiltconditionalnavigation.data.result.Status
import com.example.hiltconditionalnavigation.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    private val viewModel: LoginViewModel by viewModels()
    private val navController by lazy { findNavController() }

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        viewModel.login.observe(viewLifecycleOwner, ResultObserver {
            if (it.status == Status.ERROR) {
                Snackbar.make(requireView(), it.message ?: "Error", Snackbar.LENGTH_SHORT).show()
            } else if (it.status == Status.SUCCESS) {
                savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                navController.popBackStack()
            }
        })
    }
}