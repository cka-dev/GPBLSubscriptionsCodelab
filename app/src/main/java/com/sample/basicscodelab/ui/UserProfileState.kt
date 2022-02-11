package com.sample.basicscodelab.ui

import com.android.billingclient.api.SkuDetails

data class UserProfileState(
    val hasBasic: Boolean = false,
    val hasPremium: Boolean = false,
    val basicSkuDetails: SkuDetails? = null,
    val premiumSkuDetails: SkuDetails? = null,
//    val isNewPurchaseAcknowledged: Boolean = false
)
