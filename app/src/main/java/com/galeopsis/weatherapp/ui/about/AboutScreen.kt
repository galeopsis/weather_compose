package com.galeopsis.weatherapp.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galeopsis.weatherapp.ui.theme.WeatherTheme

@Composable
fun AboutRoute(versionName: String) {
    AboutScreen(versionName = versionName)
}

@Composable
private fun AboutScreen(versionName: String) {
    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(statusBarTopPadding + 16.dp))

        Text(
            text = "О приложении",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        AboutCard(title = "Версия") {
            Text(
                text = versionName,
                color = Color(0xFFB8D7FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AboutCard(title = "Архитектура") {
            AboutText("MVVM + Jetpack Compose")
            AboutText("StateFlow для состояния экранов")
            AboutText("UiEvent для одноразовых сообщений")
            AboutText("Koin для dependency injection")
            AboutText("Room для локального кеша")
            AboutText("Retrofit + OkHttp для сетевых запросов")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AboutCard(title = "Экраны") {
            AboutText("Погода — текущие данные, прогноз и swipe refresh")
            AboutText("Города — список сохранённых городов")
            AboutText("Настройки — адрес сервера, одноразовый код привязки, статус устройства, тема и единицы измерения")
            AboutText("О приложении — версия и краткая информация")
        }


        Spacer(modifier = Modifier.height(16.dp))

        AboutCard(title = "Авторизация") {
            AboutText("Токен устройства хранится внутри приложения и не отображается пользователю")
            AboutText("Новые устройства подключаются через одноразовый код привязки")
            AboutText("Если доступ устройства отозван на сервере, достаточно создать новый код и выполнить привязку заново")
            AboutText("Имя устройства определяется автоматически и используется только в списке устройств на сервере")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AboutCard(
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

@Composable
private fun AboutText(text: String) {
    Text(
        text = "• $text",
        color = Color(0xFFB8D7FF),
        fontSize = 17.sp,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AboutScreenPreview() {
    WeatherTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF102030))
        ) {
            AboutScreen(versionName = "1.3.0.1")
        }
    }
}