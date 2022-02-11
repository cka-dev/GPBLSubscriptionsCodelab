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
    private var billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    /**
     * Establish a connection to Google Play
     *
     */
    fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingresult: BillingResult) {
                Log.wtf(TAG, "Billing set up finished")
                if (billingresult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.wtf(TAG, "Billing response OK")
                    // The BillingClient is ready. You can query purchases and sku details here
                    queryPurchases()
                    querySkuDetails()
                } else {
                    Log.wtf(TAG, billingresult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "Billing connection disconnected")
                startBillingConnection()
            }
        })
    }


    /**
     * Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to PurchasesUpdatedListener.
     */
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
        }
        // Query for existing subscription products that have been purchased.
        billingClient.queryPurchasesAsync(
            BillingClient.SkuType.SUBS
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "Existing purchases: $purchaseList")
                GlobalScope.launch {
                    _purchases.emit(purchaseList)
                }
            }
        }
    }

    /**
     * Query Google Play Billing for products available to sell
     * and present them in the UI
     */
    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add(BASIC_SUB)
        skuList.add(PREMIUM_SUB)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuDetailsList ->
            // Process the result
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            when (responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    Log.i(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                    if (skuDetailsList.isNullOrEmpty()) {
                        Log.e(
                            TAG,
                            "onSkuDetailsResponse: " +
                                    "Found null or empty SkuDetails. " +
                                    "Check to see if the SKUs you requested are correctly published " +
                                    "in the Google Play Console."
                        )
                    } else {
                        Log.wtf(TAG, "SkuDetailsResponse not empty")
                        val newMap = skuDetailsList.associateBy {
                            it.sku
                        }
                        Log.wtf(TAG, "newMap: $newMap")

                        GlobalScope.launch {
                            _skusWithSkuDetails.emit(newMap)
                            Log.wtf(
                                TAG,
                                "_skusWithSkuDetails: ${_skusWithSkuDetails.firstOrNull()}"
                            )
                        }
                    }
                }
                BillingClient.BillingResponseCode.ERROR -> {
                    Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")

                }
                BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                    // These response codes are not expected.
                    Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                }
            }
        }
    }

    /**
     * Launch Purchase flow
     */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        Log.wtf(TAG, "In launchBillingFlow")
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        billingClient.launchBillingFlow(activity, params)

    }

    /**
     * PurchasesUpdatedListener that helps handle purchases returned from the API
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
            && purchases != null
        ) {
            // Handle the purchases
            for (purchase in purchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e(TAG, "User has cancelled")
        } else {
            // Handle any other error codes.
        }
    }

    private fun acknowledgePurchases(purchase: Purchase?) {
        if (purchase != null) {
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(
                    params
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                        && purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        GlobalScope.launch {
                            _isNewPurchaseAcknowledged.emit(true)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG: String = "BillingClient"
        private const val BASIC_SUB: String = "up_basic_sub"
        private const val PREMIUM_SUB: String = "up_premium_sub"
    }
}