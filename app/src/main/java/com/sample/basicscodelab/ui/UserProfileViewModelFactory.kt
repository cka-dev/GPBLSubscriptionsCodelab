package com.sample.basicscodelab.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.Appendable

class UserProfileViewModelFactory(private val application: Application): ViewModelProvider.Factory {


    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(application) as T
    }
}