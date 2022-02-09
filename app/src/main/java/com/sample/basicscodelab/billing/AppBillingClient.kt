package com.sample.basicscodelab.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.android.billingclient.api.SkuDetailsResponseListener

class AppBillingClient (
    context: Context
) : PurchasesUpdatedListener {

    val skusWithSkuDetails = MutableLiveData<Map<String, SkuDetails>>()
//    val skusWithSkuDetails: LiveData<Map<String, SkuDetails>> = _skusWithSkuDetails

    //    private val skuDetailsMap: MutableMap<String, SkuDetails?> = HashMap()
    private val _purchases = MutableLiveData<List<Purchase>>()
    val purchases: LiveData<List<Purchase>> = _purchases

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
        // Query for existing in app products that have been purchased. This does NOT include subscriptions.
        billingClient.queryPurchasesAsync(
            BillingClient.SkuType.SUBS
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "Existing purchases: $purchaseList")
                _purchases.postValue(purchaseList)
            }
        }

    }

    fun querySkuDetails(): MutableLiveData<Map<String, SkuDetails>> {
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
                    if (skuDetailsList == null || skuDetailsList.isEmpty()) {
                        Log.e(
                            TAG,
                             "onSkuDetailsResponse: " +
                                    "Found null or empty SkuDetails. " +
                                    "Check to see if the SKUs you requested are correctly published " +
                                    "in the Google Play Console."
                        )
                    } else {
                        Log.wtf(TAG, "SkuDetailsResponse not empty")
                        val newMap = mutableMapOf<String, SkuDetails>()
                        for (skuDetails in skuDetailsList) {
                            Log.wtf(TAG, "skuDetails: $skuDetails")
//                            Log.wtf(TAG, "sku: ${skuDetails.sku}")
//                            _skusWithSkuDetails.postValue(HashMap<String, SkuDetails>().apply {
//                                put(skuDetails.sku, skuDetails)
//                            })

                            newMap[skuDetails.sku] = skuDetails
                            Log.wtf(TAG, "newMap: $newMap")

                        }
                        skusWithSkuDetails.postValue(newMap)
                        Log.wtf(TAG, "_skusWithSkuDetails: ${skusWithSkuDetails.value}")
//                        Log.wtf(TAG, "skusWithSkuDetails: ${skusWithSkuDetails.value}")
                    }
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
                BillingClient.BillingResponseCode.DEVELOPER_ERROR,
                BillingClient.BillingResponseCode.ERROR -> {
                    Log.e(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                }
                BillingClient.BillingResponseCode.USER_CANCELED,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED,
                BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                    // These response codes are not expected.
                    Log.wtf(TAG, "onSkuDetailsResponse: $responseCode $debugMessage")
                }
            }
        }
        return skusWithSkuDetails
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

//    fun launchBasicSubFlow(activity: Activity) {
//        val skuList: MutableList<String> = ArrayList()
//        skuList.add(basicSub)
//        val params =
//    }

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

//    override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
//        TODO("Not yet implemented")
//    }
}