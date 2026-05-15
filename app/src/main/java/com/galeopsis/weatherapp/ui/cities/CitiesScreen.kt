package com.galeopsis.weatherapp.ui.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.galeopsis.weatherapp.ui.theme.WeatherTheme
import com.galeopsis.weatherapp.viewmodel.CitiesViewModel
import com.galeopsis.weatherapp.viewmodel.UiEvent

@Composable
fun CitiesRoute(
    viewModel: CitiesViewModel,
    snackbarHostState: SnackbarHostState,
    onCityClick: (String) -> Unit
) {
    val cities by viewModel.cities.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    CitiesScreen(
        cities = cities,
        onCityClick = onCityClick,
        onRemoveCityClick = viewModel::removeCity,
        onClearClick = viewModel::clearCities
    )
}

@Composable
private fun CitiesScreen(
    cities: List<String>,
    onCityClick: (String) -> Unit,
    onRemoveCityClick: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Сохранённые города",
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cities.isEmpty()) {
            EmptyCitiesBlock()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onClearClick) {
                    Text(text = "Очистить")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = cities,
                    key = { city -> city.lowercase() }
                ) { city ->
                    CityRow(
                        cityName = city,
                        onCityClick = onCityClick,
                        onRemoveCityClick = onRemoveCityClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCitiesBlock() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Список пока пуст. Найдите город на главном экране — он автоматически появится здесь.",
                color = Color(0xFFB8D7FF),
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CityRow(
    cityName: String,
    onCityClick: (String) -> Unit,
    onRemoveCityClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x99000000))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCityClick(cityName) }
                .padding(start = 18.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cityName,
                modifier = Modifier.weight(1f),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onRemoveCityClick(cityName) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить город",
                    tint = Color(0xFFFFB4AB)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CitiesScreenPreview() {
    WeatherTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF102030))
        ) {
            CitiesScreen(
                cities = listOf("Красноярск", "Москва", "Новосибирск"),
                onCityClick = {},
                onRemoveCityClick = {},
                onClearClick = {}
            )
        }
    }
}
