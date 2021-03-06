package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.domain.Asteroid


@BindingAdapter("asteroidDetailsStatusImage")
fun ImageView.asteroidDetailsStatusImage(isHazardous: Boolean) {
    isHazardous.let {
        setImageResource(when (it) {
            true -> R.drawable.asteroid_hazardous
            false -> R.drawable.asteroid_safe
        })
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

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Picasso.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

@BindingAdapter("asteroidListStatusImage")
fun ImageView.asteroidListStatusImage(item: Asteroid) {
    item.let {
        setImageResource(when (item.isPotentiallyHazardous) {
            true -> R.drawable.ic_status_potentially_hazardous
            false -> R.drawable.ic_status_normal
        })
    }
}

@BindingAdapter("asteroidDetailImageDescription")
fun ImageView.asteroidDetailImageDescription(item: Asteroid) {
    item.let {
        contentDescription = getResources().getString(
            when(item.isPotentiallyHazardous) {
                true -> R.string.potentially_hazardous_asteroid_image
                false -> R.string.not_hazardous_asteroid_image
            }
        )
    }
}
