package com.beballer.beballer.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}