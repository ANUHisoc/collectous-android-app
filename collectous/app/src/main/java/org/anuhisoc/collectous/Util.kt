package org.anuhisoc.collectous

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit


suspend fun <T>  DataStore<Preferences>.store(preferenceKey: Preferences.Key<T>, value:T){
    this.edit { mutablePreferences ->
        mutablePreferences[preferenceKey] = value }
}

/* Formula from https://developer.android.com/training/multiscreen/screendensities#dips-pels
 px = dp * (dpi / 160)*/
fun Int.toPx(dpi:Int) = this * (dpi/160)