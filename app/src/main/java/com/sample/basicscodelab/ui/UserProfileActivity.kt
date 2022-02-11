package com.sample.basicscodelab.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.android.billingclient.api.BillingFlowParams
import com.sample.basicscodelab.billing.AppBillingClient
import com.sample.basicscodelab.repository.SubscriptionDataRepository
import com.sample.basicscodelab.ui.theme.BasicsCodelabTheme


class UserProfileActivity : AppCompatActivity() {
    private lateinit var appBillingClient: AppBillingClient
    private lateinit var repo: SubscriptionDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBillingClient = AppBillingClient(this)
        appBillingClient.startBillingConnection()
        repo = SubscriptionDataRepository(appBillingClient = appBillingClient)
        val viewModel =
            ViewModelProvider(
                this,
                UserProfileViewModelFactory(application, repo = repo)
            )[UserProfileViewModel::class.java]
        setContent {
            BasicsCodelabTheme {
                val state by viewModel.state.collectAsState(initial = UserProfileState())
                when {
                    state.hasPremium -> PremiumUserProfile()
                    state.hasBasic -> BasicUserProfile()
                    else -> Subscription(state, appBillingClient)
                }
                val isNewPurchaseAcknowledgedState by viewModel.isisNewPurchaseAcknowledgedState.collectAsState(
                    initial = false
                )
                when {
                    isNewPurchaseAcknowledgedState -> reloadActivity()
                }
            }
        }
    }

    @Composable
    private fun Subscription(state: UserProfileState, appBillingClient: AppBillingClient) {
        appBillingClient.querySkuDetails()
        Surface {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    Log.wtf(TAG, "Basic button clicked")
                    Log.wtf(TAG, "basicSkuDetails: ${state.basicSkuDetails}")

                    if (state.basicSkuDetails == null) {
                        Log.wtf(TAG, "basicSkuDetails is null")
                    } else {
                        val billingParams =
                            BillingFlowParams.newBuilder().setSkuDetails(state.basicSkuDetails)
                                .build()

                        appBillingClient.launchBillingFlow(
                            this@UserProfileActivity,
                            billingParams
                        )
                    }

                }) {
                    Text(text = "Basic Subscription")
                }
                Button(onClick = {
                    Log.wtf(TAG, "Premium button clicked")
                    Log.wtf(TAG, "basicSkuDetails: ${state.premiumSkuDetails}")
                    val billingParams =
                        state.premiumSkuDetails?.let {
                            BillingFlowParams.newBuilder().setSkuDetails(it).build()
                        }

                    if (billingParams != null) {
                        appBillingClient.launchBillingFlow(
                            this@UserProfileActivity,
                            billingParams
                        )
                    }

                }) {
                    Text(text = "Premium Subscription")
                }
            }
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun SubscriptionPreview() {
        BasicsCodelabTheme {
            Subscription(state = UserProfileState(), appBillingClient)
        }
    }

    @Composable
    private fun BasicUserProfile() {
        Log.wtf(TAG, "Loading Basic Profile")
        Surface {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Text(text = "This is your basic profile")
                }
            }
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun BasicProfilePreview() {
        BasicsCodelabTheme {
            BasicUserProfile()
        }
    }

    @Composable
    private fun PremiumUserProfile() {
        Log.wtf(TAG, "Loading Premium Profile")
        Surface {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Text(text = "This is your premium profile")
                }
            }
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun PremiumProfilePreview() {
        BasicsCodelabTheme {
            PremiumUserProfile()
        }
    }

    private fun reloadActivity() {
        val intent: Intent = intent
        finish()
        startActivity(intent)
    }

    companion object {
        private const val TAG: String = "UserProfileActivity"
    }

}


