package com.sample.basicscodelab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.billingclient.api.SkuDetails
import com.sample.basicscodelab.repository.SubscriptionDataRepository

//Implement dependency injection (Hilt)
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var repo = SubscriptionDataRepository(application)

    val hasBasic: LiveData<Boolean> = repo.hasBasic
    val hasPremium: LiveData<Boolean> = repo.hasPremium
    val basicSkuDetails: LiveData<SkuDetails> = repo.basicSkuDetails
    val premiumSkuDetails: LiveData<SkuDetails> = repo.premiumSkuDetails

    fun retrieveSubs() {
        repo.retrieveSubs()
    }

    //
    fun retrieveSkuDetails() {
        repo.retrieveSkuDetails()
    }

    //TODO(cassigbe@ to implement a logic to call the above functions: https://scratchpad.corp.google.com/52a3a37b-8677-4a87-923c-6d0c71917cd7)
    fun isAuthenticated() {

    }


    companion object {
        private const val TAG: String = "UserProfileViewModel"
        private const val basicSub: String = "up_basic_sub"
        private const val premiumSub: String = "up_premium_sub"
    }
}