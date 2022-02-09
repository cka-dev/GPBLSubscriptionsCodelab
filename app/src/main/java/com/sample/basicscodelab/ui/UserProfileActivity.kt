package com.sample.basicscodelab.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.billingclient.api.BillingFlowParams
import com.sample.basicscodelab.billing.AppBillingClient
import com.sample.basicscodelab.repository.SubscriptionDataRepository
import com.sample.basicscodelab.ui.theme.BasicsCodelabTheme

class UserProfileActivity : AppCompatActivity() {
    private lateinit var appBillingClient: AppBillingClient
    private lateinit var repo: SubscriptionDataRepository
//    private val viewModel =
//        ViewModelProvider(this, UserProfileViewModelFactory(application))[UserProfileViewModel::class.java]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel =
            ViewModelProvider(this, UserProfileViewModelFactory(application))[UserProfileViewModel::class.java]
        repo = SubscriptionDataRepository(this)
        appBillingClient = AppBillingClient(this)
        appBillingClient.startBillingConnection()
//        repo.retrieveSubs()
//        repo.retrieveSkuDetails()

        setContent {
            BasicsCodelabTheme {
                val hasBasicSub by viewModel.hasBasic.observeAsState()
                val hasPremiumSub by viewModel.hasPremium.observeAsState()
                when {
                    hasBasicSub == true -> BasicUserProfile()
                    hasPremiumSub == true -> PremiumUserProfile()
                    else -> Subscription()
                }
            }
        }
    }

    @Composable
    private fun Subscription(viewModel: UserProfileViewModel = viewModel()) {
//        val viewModel =
//            ViewModelProvider(this, UserProfileViewModelFactory(application))[UserProfileViewModel::class.java]

        Surface {

            viewModel.retrieveSubs()
            val basicSkuDetails by viewModel.basicSkuDetails.observeAsState()
            val premiumSkuDetails by viewModel.premiumSkuDetails.observeAsState()
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    viewModel.retrieveSkuDetails()
                    Log.wtf(TAG, "Basic button clicked")
                    Log.wtf(TAG, "basicSkuDetails: $basicSkuDetails")
                    if (basicSkuDetails == null) {
                        Log.wtf(TAG, "basicSkuDetails is null")
                    } else{
                        val billingParams = BillingFlowParams.newBuilder().setSkuDetails(basicSkuDetails!!).build()

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
                    val billingParams =
                        premiumSkuDetails?.let { BillingFlowParams.newBuilder().setSkuDetails(it).build() }

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
            Subscription()
        }
    }

    @Composable
    private fun BasicUserProfile() {
        Surface() {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column() {
                    Text(text = "Your basic profile email is:")
                    Text(text = "")
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
        Surface() {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column() {
                    Text(text = "Your premium profile email is: ")
                    Text(text = "")
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

    companion object {
        private const val TAG: String = "UserProfileActivity"
        private const val basicSub: String = "up_basic_sub"
        private const val premiumSub: String = "up_premium_sub"
    }
}


