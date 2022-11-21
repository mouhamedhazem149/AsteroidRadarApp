package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import androidx.recyclerview.widget.RecyclerView.OnScrollListener

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })
        binding.asteroidRecycler.adapter =  adapter

        viewModel.asteroids.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        viewModel.filter.observe(viewLifecycleOwner) {
            adapter.filter(it)
        }

        binding.refreshSwipe.setOnRefreshListener {
            viewModel.updateAsteroids(context!!)
            binding.refreshSwipe.isRefreshing = false
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.status.value == UpdateStatus.Fail)
            viewModel.updateAsteroids(context!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.show_buy_menu -> AsteroidApiFilter.SHOW_TODAY
                R.id.show_rent_menu -> AsteroidApiFilter.SHOW_SAVED
                R.id.show_all_menu -> AsteroidApiFilter.SHOW_WEEK
                else -> AsteroidApiFilter.SHOW_SAVED
            }
        )
        return true
    }
}