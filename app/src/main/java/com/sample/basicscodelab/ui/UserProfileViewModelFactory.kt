package com.sample.basicscodelab.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.basicscodelab.repository.SubscriptionDataRepository

class UserProfileViewModelFactory(
    private val application: Application,
    private val repo: SubscriptionDataRepository
) : ViewModelProvider.Factory {


    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @return a newly created ViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(application, repo = repo) as T
    }
}