package com.themovielist.ui

import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.themovielist.R

class FavoriteButton(context: Context, attributeSet: AttributeSet): ToggleButton(context, attributeSet), CompoundButton.OnCheckedChangeListener {
    var onFavorite: ((Boolean) -> Unit)? = null

    init {
        this.textOn = ""
        this.textOff = ""

        setOnCheckedChangeListener(this)
        // I had to set the icon here too, because the default value of the ToggleButton is false, and when we set false again, the onCheckedChanged is not fired.
        setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.heart_outline))
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked)
            setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.heart))
        else
            setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.heart_outline))

        onFavorite?.invoke(isChecked)
    }
}