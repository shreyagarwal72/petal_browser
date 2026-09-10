package com.petal.browser.compose.settings.screens

import android.content.SharedPreferences
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.petal.browser.ads.PetalSupportiveAdsManager
import com.petal.browser.ads.PetalSupportiveAdBanner
import com.petal.browser.ui.components.ExpressiveHeader
import com.petal.browser.ui.components.M3ExpressiveVariableBackground

@Composable
fun SupportiveAdsSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    targetHighlightItemId: String? = null
) {
    val context = LocalContext.current
    val sp = remember { PreferenceManager.getDefaultSharedPreferences(context) }
    var isSupportiveAdsEnabled by remember {
        mutableStateOf(sp.getBoolean(PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED, false))
    }

    DisposableEffect(sp) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED) {
                isSupportiveAdsEnabled = sp.getBoolean(PetalSupportiveAdsManager.KEY_SUPPORTIVE_ADS_ENABLED, false)
            }
        }
        sp.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sp.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        M3ExpressiveVariableBackground(pageSeed = "supportive_ads_settings")

        Column(modifier = Modifier.fillMaxSize()) {
            ExpressiveHeader(
                title = "Supportive Ads",
                subtitle = "Configure optional non-intrusive ad banners",
                onBack = onNavigateBack
            )

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main toggle card
                SettingsCategoryCard(
                    title = "Monetization & Support",
                    icon = Icons.Rounded.VolunteerActivism,
                    cardId = "ads_supportive",
                    targetHighlightId = targetHighlightItemId
                ) {
                    SettingsSwitchRow(
                        title = "Enable Supportive Ads",
                        subtitle = "Displays an unobtrusive banner on the home screen to support development",
                        checked = isSupportiveAdsEnabled,
                        onCheckedChange = { enabled ->
                            isSupportiveAdsEnabled = enabled
                            PetalSupportiveAdsManager.setSupportiveAdsEnabled(context, enabled)
                            if (enabled) {
                                PetalSupportiveAdsManager.initialize(context)
                            }
                        }
                    )
                }

                // Info card
                SettingsCategoryCard(
                    title = "Privacy & User Respect",
                    icon = Icons.Rounded.Favorite,
                    cardId = "ads_privacy",
                    targetHighlightId = targetHighlightItemId
                ) {
                    Text(
                        text = "Petal Browser remains 100% ad-block enabled for all web browsing. Supportive ads are entirely optional, non-intrusive banner ads that only appear on your home screen when explicitly enabled. We do not track personal data across websites.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Live Preview when enabled
                AnimatedVisibility(visible = isSupportiveAdsEnabled) {
                    SettingsCategoryCard(
                        title = "Live Banner Preview",
                        icon = Icons.Rounded.Paid
                    ) {
                        Text(
                            text = "Preview of the supportive banner as displayed on the home screen:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PetalSupportiveAdBanner()
                    }
                }
            }
        }
    }
}
