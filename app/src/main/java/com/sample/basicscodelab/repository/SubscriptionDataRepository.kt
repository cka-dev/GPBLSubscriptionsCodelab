package com.sample.basicscodelab.repository

import com.android.billingclient.api.SkuDetails
import com.sample.basicscodelab.billing.AppBillingClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class SubscriptionDataRepository(appBillingClient: AppBillingClient) {

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

    val isNewPurchaseAcknowledged: Flow<Boolean> = appBillingClient.isNewPurchaseAcknowledged

    companion object {
        private const val BASIC_SUB: String = "up_basic_sub"
        private const val PREMIUM_SUB: String = "up_premium_sub"
    }
}