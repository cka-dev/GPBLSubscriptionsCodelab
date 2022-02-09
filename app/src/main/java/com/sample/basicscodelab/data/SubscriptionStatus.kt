package com.sample.basicscodelab.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local subscription data. This is stored on disk in a database.
 */
@Entity(tableName = "subscriptions")
data class SubscriptionStatus(
    // Local fields.
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0,
    var subscriptionStatusJson: String? = null,
    var subAlreadyOwned: Boolean = false,
    var isLocalPurchase: Boolean = false,

    // Remote fields.
    var sku: String? = null,
    var purchaseToken: String? = null,
    var isEntitlementActive: Boolean = false,
    var willRenew: Boolean = false,
    var activeUntilMillisec: Long = 0,
    var isFreeTrial: Boolean = false,
    var isGracePeriod: Boolean = false,
    var isAccountHold: Boolean = false,
    var isPaused: Boolean = false,
    var autoResumeTimeMillis: Long = 0
) {

    companion object {
        /**
         * Create a record for a subscription that is already owned by a different user.
         *
         * The server does not return JSON for a subscription that is already owned by
         * a different user, so we need to construct a local record with the basic fields.
         */
        fun alreadyOwnedSubscription(
            sku: String,
            purchaseToken: String
        ): SubscriptionStatus {
            return SubscriptionStatus().apply {
                this.sku = sku
                this.purchaseToken = purchaseToken
                isEntitlementActive = false
                subAlreadyOwned = true
            }
        }
    }

}

