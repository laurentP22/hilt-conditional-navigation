package com.example.hiltconditionalnavigation.ui.home

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hiltconditionalnavigation.R
import com.example.hiltconditionalnavigation.data.result.EventObserver
import com.example.hiltconditionalnavigation.databinding.FragmentHomeBinding
import com.example.hiltconditionalnavigation.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HomeViewModel by viewModels()
    private val navController by lazy { findNavController() }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openProfile.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.openProfile()
            findNavController().navigate(action)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_action_bar, menu)
        mainViewModel.user.observe(viewLifecycleOwner, {
            val res = if (it) R.string.sign_out else R.string.sign_in
            menu.findItem(R.id.action_auth).setTitle(res)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_auth -> {
            if (mainViewModel.user.value == true) {
                viewModel.signOut()
            } else {
                navController.navigate(R.id.loginFragment)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}