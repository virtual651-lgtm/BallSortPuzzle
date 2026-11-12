/*
 * com.example.ballsort.ui.settings
 * Ayarlar ekranı UI.
 */
package com.example.ballsort.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.BuildConfig
import com.example.ballsort.R
import com.example.ballsort.ui.components.TubeView
import com.example.ballsort.ui.theme.BallSkin
import com.example.ballsort.ui.theme.TubeSkin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val totalStars by viewModel.totalStars.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingToggleRow(
                title = stringResource(R.string.settings_sound),
                checked = settings.sound,
                onCheckedChange = viewModel::setSound,
            )
            SettingToggleRow(
                title = stringResource(R.string.settings_vibration),
                checked = settings.vibration,
                onCheckedChange = viewModel::setVibration,
            )
            SettingToggleRow(
                title = stringResource(R.string.settings_multi_move),
                subtitle = stringResource(R.string.settings_multi_move_desc),
                checked = settings.multiMove,
                onCheckedChange = viewModel::setMultiMove,
            )
            SettingToggleRow(
                title = stringResource(R.string.settings_color_blind),
                subtitle = stringResource(R.string.settings_color_blind_desc),
                checked = settings.colorBlind,
                onCheckedChange = viewModel::setColorBlind,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingToggleRow(
                    title = stringResource(R.string.settings_dynamic_color),
                    checked = settings.dynamicColor,
                    onCheckedChange = viewModel::setDynamicColor,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = settings.theme == "system",
                    onClick = { viewModel.setTheme("system") },
                    label = { Text(stringResource(R.string.theme_system)) },
                )
                FilterChip(
                    selected = settings.theme == "light",
                    onClick = { viewModel.setTheme("light") },
                    label = { Text(stringResource(R.string.theme_light)) },
                )
                FilterChip(
                    selected = settings.theme == "dark",
                    onClick = { viewModel.setTheme("dark") },
                    label = { Text(stringResource(R.string.theme_dark)) },
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.settings_language),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = settings.language == "system",
                    onClick = { viewModel.setLanguage("system") },
                    label = { Text(stringResource(R.string.lang_system)) },
                )
                FilterChip(
                    selected = settings.language == "tr",
                    onClick = { viewModel.setLanguage("tr") },
                    label = { Text(stringResource(R.string.lang_tr)) },
                )
                FilterChip(
                    selected = settings.language == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text(stringResource(R.string.lang_en)) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = stringResource(R.string.settings_skins_stars_hint, totalStars),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.settings_ball_skin),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                BallSkin.entries.forEach { skin ->
                    val unlocked = totalStars >= skin.requiredStars
                    val name = stringResource(skin.nameRes)
                    FilterChip(
                        selected = settings.ballSkin == skin.id,
                        enabled = unlocked,
                        onClick = { viewModel.setBallSkin(skin.id) },
                        leadingIcon = if (!unlocked) {
                            { Icon(Icons.Default.Lock, contentDescription = null) }
                        } else null,
                        label = {
                            Text(
                                if (unlocked) name else stringResource(R.string.skin_locked_format, name, skin.requiredStars)
                            )
                        },
                    )
                }
            }

            Text(
                text = stringResource(R.string.settings_tube_skin),
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                TubeSkin.entries.forEach { skin ->
                    val unlocked = totalStars >= skin.requiredStars
                    val name = stringResource(skin.nameRes)
                    FilterChip(
                        selected = settings.tubeSkin == skin.id,
                        enabled = unlocked,
                        onClick = { viewModel.setTubeSkin(skin.id) },
                        leadingIcon = if (!unlocked) {
                            { Icon(Icons.Default.Lock, contentDescription = null) }
                        } else null,
                        label = {
                            Text(
                                if (unlocked) name else stringResource(R.string.skin_locked_format, name, skin.requiredStars)
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.settings_skin_preview),
                style = MaterialTheme.typography.titleMedium,
            )
            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                ) {
                    TubeView(
                        index = 0,
                        tube = listOf(0, 1, 2),
                        capacity = 4,
                        tubeWidth = 48.dp,
                        ballSize = 30.dp,
                        isSelected = false,
                        isHintSource = false,
                        isHintTarget = false,
                        shakeToken = 0L,
                        hiddenSlots = IntRange.EMPTY,
                        colorBlind = settings.colorBlind,
                        ballSkin = settings.ballSkin,
                        tubeSkin = settings.tubeSkin,
                        onSlotMeasured = { _, _ -> },
                        onTopExitMeasured = {},
                        onClick = {},
                    )
                    TubeView(
                        index = 1,
                        tube = listOf(3, 3, 3, 3),
                        capacity = 4,
                        tubeWidth = 48.dp,
                        ballSize = 30.dp,
                        isSelected = false,
                        isHintSource = false,
                        isHintTarget = false,
                        shakeToken = 0L,
                        hiddenSlots = IntRange.EMPTY,
                        colorBlind = settings.colorBlind,
                        ballSkin = settings.ballSkin,
                        tubeSkin = settings.tubeSkin,
                        onSlotMeasured = { _, _ -> },
                        onTopExitMeasured = {},
                        onClick = {},
                    )
                }
            }

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:attemption@activist.com")
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                context.getString(R.string.settings_feedback_subject, BuildConfig.VERSION_NAME)
                            )
                        }
                        runCatching { context.startActivity(intent) }
                    }
                    .padding(vertical = 8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.settings_feedback),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(R.string.settings_feedback_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
                Text(
                    text = stringResource(
                        R.string.settings_app_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subtitle: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

