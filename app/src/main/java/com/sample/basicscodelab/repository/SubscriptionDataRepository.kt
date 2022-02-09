package com.sample.basicscodelab.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.SkuDetails
import com.sample.basicscodelab.billing.AppBillingClient

class SubscriptionDataRepository(context: Context) {
    private var appBillingClient: AppBillingClient = AppBillingClient(context)


    private val subscriptions = appBillingClient.querySkuDetails().value

    val hasBasic = MutableLiveData(false)
    val hasPremium = MutableLiveData(false)
    val basicSkuDetails = MutableLiveData<SkuDetails>()
    val premiumSkuDetails = MutableLiveData<SkuDetails>()

    /**
     * Retrieves existing subscriptions
     */
    fun retrieveSubs() {
//        appBillingClient.startBillingConnection()
        Log.wtf(TAG, "In retrieveSubs")
        appBillingClient.purchases.observeForever { purchaseList ->
            if (purchaseList.isNotEmpty()) {
                for (purchases in purchaseList) {
                    purchases.skus.forEach {
                        if (it == basicSub) {
                            hasBasic.postValue(true)
                        } else if (it == premiumSub) {
                            hasPremium.postValue(true)
                        }
                    }
                }
            }
        }
    }


    fun retrieveSkuDetails() {
//        appBillingClient.startBillingConnection()
        Log.wtf(TAG, "In retrieveProducts")
        appBillingClient.skusWithSkuDetails.observeForever { skusDetailsMap ->
            if (skusDetailsMap.isNotEmpty()) {
                Log.wtf(TAG, "skuDetailsMap is not Empty: $skusDetailsMap")
                for (skuDetails in skusDetailsMap) {
                    Log.wtf(TAG, "skuDetails: $skuDetails" )
                    if (skuDetails.key == basicSub) {
                        basicSkuDetails.postValue(skuDetails.value)
                    } else if (skuDetails.key == premiumSub) {
                        premiumSkuDetails.postValue(skuDetails.value)
                    }
                }
            } else {
                Log.wtf(TAG, "SkuDetailsMap is Empty: $skusDetailsMap")
            }
        }
    }

    companion object {
        private const val TAG: String = "UserProfileViewModel"
        private const val basicSub: String = "up_basic_sub"
        private const val premiumSub: String = "up_premium_sub"
    }
}