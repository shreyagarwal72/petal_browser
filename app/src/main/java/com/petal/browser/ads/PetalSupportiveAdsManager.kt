package com.petal.browser.ads

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

/**
 * Petal Supportive Ads Manager
 * Coordinates Google Mobile Ads SDK initialization and renders supportive ads
 * exclusively when the user has enabled the supportive ads toggle in Settings -> Ads.
 */
object PetalSupportiveAdsManager {

    private const val TAG = "PetalSupportiveAds"

    const val KEY_SUPPORTIVE_ADS_ENABLED = "sp_supportive_ads_enabled"
    const val AD_UNIT_ID = "ca-app-pub-5680314597533936/9963205175"

    private var isInitialized = false

    /**
     * Initializes Google Mobile Ads SDK.
     */
    @JvmStatic
    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { status ->
                isInitialized = true
                Log.i(TAG, "Google Mobile Ads SDK initialized: $status")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    /**
     * Checks if supportive ads are enabled by the user.
     */
    @JvmStatic
    fun isSupportiveAdsEnabled(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(KEY_SUPPORTIVE_ADS_ENABLED, false)
    }

    /**
     * Sets supportive ads preference.
     */
    @JvmStatic
    fun setSupportiveAdsEnabled(context: Context, enabled: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean(KEY_SUPPORTIVE_ADS_ENABLED, enabled).apply()
    }
}

/**
 * Supportive Ad Banner Card for Petal Home Screen.
 * Renders only when supportive ads toggle is enabled in Settings -> Ads.
 */
@Composable
fun PetalSupportiveAdBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sp = remember { PreferenceManager.getDefaultSharedPreferences(context) }
    var isEnabled by remember { mutableStateOf(sp.getBoolean(PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED, false)) }
    var isAdLoaded by remember { mutableStateOf(false) }

    DisposableEffect(sp) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED) {
                isEnabled = sp.getBoolean(PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED, false)
            }
        }
        sp.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sp.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    if (!isEnabled) return

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header tag indicating supportive ad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Supportive Ad",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Real AdMob AdView container
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        val adView = AdView(ctx).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = PetalSupportiveAdsManager.AD_UNIT_ID
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    isAdLoaded = true
                                    Log.i("PetalSupportiveAds", "Supportive Banner Ad Loaded Successfully")
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    super.onAdFailedToLoad(loadAdError)
                                    isAdLoaded = false
                                    Log.w("PetalSupportiveAds", "Supportive Banner Ad Failed to Load: ${loadAdError.message}")
                                }
                            }
                        }
                        addView(adView)
                        val adRequest = AdRequest.Builder().build()
                        adView.loadAd(adRequest)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}
