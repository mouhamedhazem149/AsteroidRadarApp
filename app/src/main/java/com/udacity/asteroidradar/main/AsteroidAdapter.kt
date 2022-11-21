package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.bindAsteroidStatusImage
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import java.text.SimpleDateFormat
import java.util.*

class AsteroidAdapter (val onClickListener: OnClickListener ) :
    ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(private var binding: AsteroidItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(_asteroid: Asteroid,clickListener: OnClickListener) {
            binding.asteroid = _asteroid
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    private var list = listOf<Asteroid>()

    fun setData(list: List<Asteroid>?){
        this.list = list!!
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(AsteroidItemBinding.inflate(
            LayoutInflater.from(parent.context),
        parent,
        false))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    fun filter(asteroidFilter : AsteroidApiFilter) {

        val dateFormatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

        val today = dateFormatter.format(Calendar.getInstance().time)

        when (asteroidFilter) {
            AsteroidApiFilter.SHOW_TODAY -> {
                val filtered = list.filter {
                    it.closeApproachDate == today
                }
                submitList(filtered)
            }

            AsteroidApiFilter.SHOW_WEEK -> {
                val _calender = Calendar.getInstance()
                _calender.add(Calendar.DAY_OF_YEAR,7)
                val endOfWeek = dateFormatter.format(_calender.time)

                val filtered = list.filter {
                    it.closeApproachDate >= today && it.closeApproachDate <= endOfWeek
                }
                submitList(filtered)
            }

            AsteroidApiFilter.SHOW_SAVED -> {
                submitList(list)
            }
        }
    }

    class OnClickListener(val clickListener: (asteroid :Asteroid) -> Unit) {
        fun onClick(asteroid :Asteroid) = clickListener(asteroid)
    }
}
