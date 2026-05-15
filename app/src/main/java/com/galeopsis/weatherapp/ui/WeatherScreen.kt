package com.galeopsis.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.galeopsis.weatherapp.model.repository.ForecastUiItem
import com.galeopsis.weatherapp.ui.theme.WeatherTheme
import com.galeopsis.weatherapp.viewmodel.MainViewModel
import com.galeopsis.weatherapp.viewmodel.UiEvent
import com.galeopsis.weatherapp.viewmodel.WeatherUiState
import androidx.compose.material3.Scaffold
import com.galeopsis.weatherapp.ui.navigation.AppRoute

@Composable
fun WeatherRoute(
    viewModel: MainViewModel,
    versionName: String,
    snackbarHostState: SnackbarHostState,
    onLocationClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    WeatherScreen(
        state = state,
        versionName = versionName,
        onSearch = viewModel::loadByCity,
        onLocationClick = onLocationClick,
        onRefresh = viewModel::refresh,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WeatherScreen(
    state: WeatherUiState,
    versionName: String,
    onSearch: (String) -> Unit,
    onLocationClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        WeatherContent(
            state = state,
            versionName = versionName,
            onSearch = onSearch,
            onLocationClick = onLocationClick,
            modifier = Modifier.fillMaxSize()
        )

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun WeatherContent(
    state: WeatherUiState,
    versionName: String,
    onSearch: (String) -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        SearchPanel(
            value = searchText,
            onValueChange = { searchText = it },
            onSearch = {
                focusManager.clearFocus()
                onSearch(searchText)
                searchText = ""
            },
            onLocationClick = {
                focusManager.clearFocus()
                onLocationClick()
            }
        )

        Spacer(modifier = Modifier.height(22.dp))

        when {
            state.isLoading && !state.hasData -> LoadingBlock()
            state.hasData -> WeatherDataBlock(state = state, versionName = versionName)
            else -> EmptyBlock(onLocationClick = onLocationClick)
        }

        if (state.isLoading && state.hasData) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Обновление данных…", color = Color.White)
            }
        }
    }
}

@Composable
private fun SearchPanel(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLocationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            placeholder = { Text(text = "Город...") },
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Поиск")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF0FECD7),
                unfocusedBorderColor = Color(0x990FECD7),
                focusedContainerColor = Color(0x66000000),
                unfocusedContainerColor = Color(0x66000000),
                cursorColor = Color(0xFF0FECD7)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onLocationClick) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Определить местоположение",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp), strokeWidth = 5.dp)
    }
}

@Composable
private fun EmptyBlock(onLocationClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Данных пока нет",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Разрешите геолокацию или найдите город по названию.",
                color = Color(0xFFB8D7FF),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLocationClick) {
                Text(text = "Определить местоположение")
            }
        }
    }
}

@Composable
private fun WeatherDataBlock(state: WeatherUiState, versionName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.cityName,
                color = Color(0xFFE7E0A3),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.shadow(2.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${state.currentTemp}${state.temperatureUnit}",
                    color = Color(0xFFE6D005),
                    fontSize = 64.sp,
                    lineHeight = 66.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.shadow(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = state.iconUrl,
                    contentDescription = state.description,
                    modifier = Modifier.size(62.dp)
                )
            }

            Text(
                text = state.description.ifBlank { "Описание недоступно" },
                color = Color(0xFFB8D7FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            DetailsGrid(state = state)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    ForecastCard(items = state.forecastItems, temperatureUnit = state.temperatureUnit)

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Версия приложения: $versionName",
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x66000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = Color(0xFFB8D7FF),
        fontSize = 12.sp,
        textAlign = TextAlign.End
    )
}

@Composable
private fun DetailsGrid(state: WeatherUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x66000000), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WeatherMetric(
                title = "Скорость ветра",
                value = "${state.windSpeed.ifBlank { "—" }} ${state.windSpeedUnit}",
                modifier = Modifier.weight(1f)
            )
            WeatherMetric(
                title = "Влажность",
                value = "${state.humidity.ifBlank { "—" }} %",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            WeatherMetric(
                title = "Восход",
                value = state.sunrise.ifBlank { "—" },
                modifier = Modifier.weight(1f)
            )
            WeatherMetric(
                title = "Закат",
                value = state.sunset.ifBlank { "—" },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeatherMetric(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = Color(0xFF81C784),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color(0xFFB8D7FF),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ForecastCard(
    items: List<ForecastUiItem>,
    temperatureUnit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Прогноз",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            if (items.isEmpty()) {
                Text(
                    text = "Прогноз пока недоступен",
                    color = Color(0xFFB8D7FF),
                    fontSize = 16.sp
                )
            } else {
                items.forEach { item ->
                    ForecastRow(item = item, temperatureUnit = temperatureUnit)
                }
            }
        }
    }
}

@Composable
private fun ForecastRow(
    item: ForecastUiItem,
    temperatureUnit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.iconUrl,
            contentDescription = item.description,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dateLabel,
                color = Color(0xFF81C784),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.description,
                color = Color(0xFFB8D7FF),
                fontSize = 17.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "${item.temperature}$temperatureUnit",
            color = Color(0xFFE6D005),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(
    name = "Weather data",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun WeatherScreenPreview() {
    WeatherTheme {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AppBottomNavigationBar(
                    currentRoute = AppRoute.Weather.route,
                    onRouteClick = {}
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF102030))
                    .padding(innerPadding)
            ) {
                WeatherScreen(
                    state = WeatherUiState(
                        hasData = true,
                        cityName = "Красноярск",
                        currentTemp = "18",
                        temperatureUnit = "°C",
                        description = "переменная облачность",
                        humidity = "54",
                        windSpeed = "4",
                        windSpeedUnit = "м/с",
                        sunrise = "04:19:05",
                        sunset = "21:11:52",
                        iconUrl = "",
                        forecastItems = listOf(
                            ForecastUiItem("пятница, 15 мая", "19", "облачно", ""),
                            ForecastUiItem("суббота, 16 мая", "21", "ясно", ""),
                            ForecastUiItem("воскресенье, 17 мая", "17", "дождь", "")
                        )
                    ),
                    versionName = "1.3.0",
                    onSearch = { _ -> },
                    onLocationClick = {},
                    onRefresh = {},
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
