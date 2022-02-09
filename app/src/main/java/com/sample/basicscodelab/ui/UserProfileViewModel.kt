package com.sample.basicscodelab.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.sample.basicscodelab.repository.SubscriptionDataRepository
import kotlinx.coroutines.flow.combine

//TODO (Implement dependency injection (Hilt))
class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    private var repo = SubscriptionDataRepository(application)

    val state = combine(
        repo.hasBasic,
        repo.hasPremium,
        repo.basicSkuDetails,
        repo.premiumSkuDetails
    ) { hasBasic, hasPremium, basicSkuDetails, premiumSkuDetails ->
        Log.wtf(TAG, "hasBasic: $hasBasic")
        Log.wtf(TAG, "hasPremium: $hasPremium")
        Log.wtf(TAG, "basicSkuDetails: $basicSkuDetails")
        Log.wtf(TAG, "premiumSkuDetails: $premiumSkuDetails")
        UserProfileState(
            hasBasic = hasBasic,
            hasPremium = hasPremium,
            basicSkuDetails = basicSkuDetails,
            premiumSkuDetails = premiumSkuDetails
        )
    }


    //TODO(cassigbe@ to implement a logic to call the above functions: See Scratchpad)
    fun isAuthenticated() {

    }


    companion object {
        private const val TAG: String = "UserProfileViewModel"
    }
}