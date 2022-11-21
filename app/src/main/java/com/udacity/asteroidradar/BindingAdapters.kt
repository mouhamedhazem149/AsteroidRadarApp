package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.UpdateStatus

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription =  imageView.context.getString(R.string.potentially_hazard)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription =  imageView.context.getString(R.string.potentially_no_hazard)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription =  imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription =  imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("imageOfDay")
fun bindImageOfTheDay(imageView: ImageView,pictureOfDay: PictureOfDay?){
    pictureOfDay?.let {
        Picasso
            .get()
            .load(it.url)
            .into(imageView)

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.contentDescription = it.title
    }
}

@BindingAdapter("imageOfDayTitle")
fun bindImageOfTheDayTitle(textView: TextView,pictureOfDay: PictureOfDay?) {
    textView.text = pictureOfDay?.title
        ?: ""
}

@BindingAdapter("updateStatus")
fun bindUpdateStatus(swipeRefreshLayout: SwipeRefreshLayout,status: UpdateStatus){
    when (status) {
        UpdateStatus.Loading -> swipeRefreshLayout.isRefreshing = true
        UpdateStatus.Success -> swipeRefreshLayout.isRefreshing = false
        UpdateStatus.Fail -> {
            swipeRefreshLayout.isRefreshing = false
        }
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
