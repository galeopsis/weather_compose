package com.galeopsis.weatherapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.galeopsis.weatherapp.model.settings.AppSettings
import com.galeopsis.weatherapp.model.settings.ThemeMode
import com.galeopsis.weatherapp.model.settings.WeatherUnits
import com.galeopsis.weatherapp.ui.theme.WeatherTheme
import com.galeopsis.weatherapp.viewmodel.SettingsViewModel
import com.galeopsis.weatherapp.viewmodel.UiEvent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.statusBars

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    SettingsScreen(
        settings = settings,
        onSaveApiKey = viewModel::saveApiKey,
        onThemeModeClick = viewModel::saveThemeMode,
        onUnitsClick = viewModel::saveUnits
    )
}

@Composable
private fun SettingsScreen(
    settings: AppSettings,
    onSaveApiKey: (String) -> Unit,
    onThemeModeClick: (ThemeMode) -> Unit,
    onUnitsClick: (WeatherUnits) -> Unit
) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var apiKeyInput by rememberSaveable(settings.apiKey) { mutableStateOf(settings.apiKey) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(statusBarTopPadding + 16.dp))

        Text(
            text = "Настройки",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(title = "API-ключ OpenWeatherMap") {
            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(text = "API-ключ") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSaveApiKey(apiKeyInput) })
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Если поле пустое, используется ключ из файла apikey.properties, который попадает в BuildConfig.API_KEY.",
                color = Color(0xFFB8D7FF),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onSaveApiKey(apiKeyInput) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Сохранить API-ключ")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(title = "Тема") {
            SelectableButtons(
                values = ThemeMode.entries,
                selectedValue = settings.themeMode,
                titleProvider = { it.title },
                onClick = onThemeModeClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsCard(title = "Единицы измерения") {
            SelectableButtons(
                values = WeatherUnits.entries,
                selectedValue = settings.units,
                titleProvider = { it.title },
                onClick = onUnitsClick
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "После изменения единиц измерения обновите погоду свайпом вниз или повторным поиском города.",
                color = Color(0xFFB8D7FF),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SelectableButtons(
    values: List<T>,
    selectedValue: T,
    titleProvider: (T) -> String,
    onClick: (T) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        values.forEach { value ->
            if (value == selectedValue) {
                Button(onClick = { onClick(value) }) {
                    Text(text = titleProvider(value))
                }
            } else {
                OutlinedButton(onClick = { onClick(value) }) {
                    Text(text = titleProvider(value))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SettingsScreenPreview() {
    WeatherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF102030))
        ) {
            SettingsScreen(
                settings = AppSettings(),
                onSaveApiKey = {},
                onThemeModeClick = {},
                onUnitsClick = {}
            )
        }
    }
}
