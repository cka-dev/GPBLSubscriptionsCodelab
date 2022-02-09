package com.sample.basicscodelab.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppBillingClient(
    context: Context
) : PurchasesUpdatedListener {

    private val _skusWithSkuDetails = MutableSharedFlow<Map<String, SkuDetails>>()
    val skusWithSkuDetails: Flow<Map<String, SkuDetails>> = _skusWithSkuDetails

    private val _purchases = MutableSharedFlow<List<Purchase>>()
    val purchases: Flow<List<Purchase>> = _purchases


    private var billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingresult: BillingResult) {
                Log.wtf(TAG, "Billing set up finished")
                if (billingresult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here
                    Log.wtf(TAG, "Billing response OK")
                    queryPurchases()
                    querySkuDetails()
                } else {
                    Log.wtf(TAG, billingresult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.e(TAG, "Billing connection disconnected")
                TODO("cassigbe to implement connection reload")
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

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("up_basic_sub")
        skuList.add("up_premium_sub")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)

        // leverage querySkuDetails Kotlin extension function

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


    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        Log.wtf(TAG, "In launchBillingFlow")
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Log.d(TAG, "launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
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
                    params,
                    object : AcknowledgePurchaseResponseListener {
                        override fun onAcknowledgePurchaseResponse(p0: BillingResult) {
                            TODO("Not yet implemented")
                        }
                    })
            }
        }
    }

    companion object {
        private const val TAG: String = "BillingClient"
        private val LIST_OF_SKUS = listOf("up_basic_sub", "up_premium_sub")
        private const val basicSub: String = "up_basic_sub"
        private const val premiumSub: String = "up_premium_sub"
    }
}