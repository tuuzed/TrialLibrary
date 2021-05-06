package com.github.tuuzed.libtrial

import android.content.Context
import androidx.startup.Initializer

class TrialInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        TrialUtils.init(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}