package com.sample.basicscodelab.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppBillingClient(
    context: Context
) : PurchasesUpdatedListener {

    private val _skusWithSkuDetails = MutableSharedFlow<Map<String, SkuDetails>>()
    var skusWithSkuDetails: SharedFlow<Map<String, SkuDetails>> = _skusWithSkuDetails

    private val _purchases = MutableSharedFlow<List<Purchase>>()
    val purchases: SharedFlow<List<Purchase>> = _purchases

    private val _isNewPurchaseAcknowledged = MutableSharedFlow<Boolean>()
    var isNewPurchaseAcknowledged: SharedFlow<Boolean> = _isNewPurchaseAcknowledged

    // Initialize the BillingClient


    /**
     * Establish a connection to Google Play
     *
     */



    /**
     * Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to PurchasesUpdatedListener.
     */


    /**
     * Query Google Play Billing for products available to sell
     * and present them in the UI
     */


    /**
     * Launch Purchase flow
     */


    /**
     * PurchasesUpdatedListener that helps handle purchases returned from the API
     */


    companion object {
        private const val TAG: String = "BillingClient"
        private const val BASIC_SUB: String = "up_basic_sub"
        private const val PREMIUM_SUB: String = "up_premium_sub"
    }
}