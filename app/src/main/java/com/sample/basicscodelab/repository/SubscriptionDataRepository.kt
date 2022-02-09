package com.sample.basicscodelab.repository

import android.content.Context
import com.android.billingclient.api.SkuDetails
import com.sample.basicscodelab.billing.AppBillingClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class SubscriptionDataRepository(context: Context) {

    private var appBillingClient: AppBillingClient = AppBillingClient(context)

    val hasBasic: Flow<Boolean> = appBillingClient.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            purchase.skus.contains(BASIC_SUB)
        }
    }
    val hasPremium: Flow<Boolean> = appBillingClient.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            purchase.skus.contains(PREMIUM_SUB)
        }
    }
    val basicSkuDetails: Flow<SkuDetails> = appBillingClient.skusWithSkuDetails.filter {
        it.containsKey(
            BASIC_SUB
        )
    }.map { it[BASIC_SUB]!! }
    val premiumSkuDetails: Flow<SkuDetails> = appBillingClient.skusWithSkuDetails.filter {
        it.containsKey(
            PREMIUM_SUB
        )
    }.map { it[PREMIUM_SUB]!! }

    companion object {
        private const val TAG: String = "UserProfileViewModel"
        private const val BASIC_SUB: String = "up_basic_sub"
        private const val PREMIUM_SUB: String = "up_premium_sub"
    }
}