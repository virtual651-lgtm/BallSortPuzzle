/*
 * com.example.ballsort.ui.settings
 * Ayarlar ekranı UI.
 */
package com.example.ballsort.ui.settings

import android.os.Build
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

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

