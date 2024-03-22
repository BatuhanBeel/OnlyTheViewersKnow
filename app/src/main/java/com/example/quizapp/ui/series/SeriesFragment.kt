package com.example.quizapp.ui.series

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.quizapp.R
import com.example.quizapp.data.SortOrder
import com.example.quizapp.databinding.FragmentSeriesBinding
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "SeriesFragment"

@AndroidEntryPoint
class SeriesFragment : Fragment(R.layout.fragment_series), SeriesAdapter.OnItemClickListener {

    private val viewModel: SeriesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSeriesBinding.bind(view)
        val adapter = SeriesAdapter(this)
        val searchItem = binding.topAppBar.menu.findItem(R.id.action_search)

        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        binding.apply {
            seriesRecyclerView.adapter = adapter
            seriesRefreshLayout.setOnRefreshListener {
                progressBarRecyclerView.visibility = View.VISIBLE
                viewModel.updateLoadingState()
                viewModel.pageRefreshed()
                seriesRefreshLayout.isRefreshing = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.uiState.collect{ event ->
                        if (!event.isLoading){
                            adapter.submitList(event.series)
                            binding.progressBarRecyclerView.visibility = View.GONE
                        }
                        event.userMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.userMessageShown()
                        }
                    }
                }
                launch {
                    viewModel.seriesFlow.collect{ seriesUiState ->
                        viewModel.updateUiState(seriesUiState)
                    }
                }
            }
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_sort_by_name ->{
                    viewModel.updateSortOrder(SortOrder.BY_NAME)
                    true
                }
                R.id.action_sort_by_level ->{
                    viewModel.updateSortOrder(SortOrder.BY_LEVEL)
                    true
                }
                R.id.action_sort_by_series ->{
                    viewModel.updateSortOrder(SortOrder.BY_SERIES)
                    true
                }
                R.id.action_sort_by_movie ->{
                    viewModel.updateSortOrder(SortOrder.BY_MOVIE)
                    true
                }
                else -> false
            }
        }
    }

    override fun onItemClick(item: SeriesAndResults) {
        val action =
            SeriesFragmentDirections.actionSeriesFragmentToSeriesInformationFragment(
                item
            )
        findNavController().navigate(action)
    }
}