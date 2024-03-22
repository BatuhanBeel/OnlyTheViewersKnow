package com.example.quizapp.ui.quizzes

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
import com.example.quizapp.databinding.FragmentQuizzesBinding
import com.example.quizapp.model.SeriesAndResults
import com.example.quizapp.ui.series.SeriesAdapter
import com.example.quizapp.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "SeriesQuizzesFragment"

@AndroidEntryPoint
class QuizzesFragment : Fragment(R.layout.fragment_quizzes), SeriesAdapter.OnItemClickListener {

    private val viewModel: SeriesQuizzesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentQuizzesBinding.bind(view)
        val adapter = SeriesAdapter(this)
        val searchItem = binding.quizzesTopAppBar.menu.findItem(R.id.action_search)

        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        binding.apply {
            quizzesRecyclerView.adapter = adapter
            quizzesRefreshLayout.setOnRefreshListener {
                quizzesProgressBar.visibility = View.VISIBLE
                viewModel.updateLoadingState()
                viewModel.pageRefreshed()
                quizzesRefreshLayout.isRefreshing = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.uiState.collect(){ event ->
                        if (!event.isLoading){
                            adapter.submitList(event.series)
                            binding.quizzesProgressBar.visibility = View.GONE
                        }
                        event.userMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                            viewModel.userMessageShown()
                        }
                    }
                }
                launch {
                    viewModel.seriesFlow.collect(){ seriesUiState ->
                        viewModel.updateUiState(seriesUiState)
                    }
                }
            }
        }

        binding.quizzesTopAppBar.setOnMenuItemClickListener { menuItem ->
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
            QuizzesFragmentDirections.actionUserQuizzesFragmentToLevelFragment(
                item
            )
        findNavController().navigate(action)
    }
}