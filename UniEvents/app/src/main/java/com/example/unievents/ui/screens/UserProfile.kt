package com.example.unievents.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.unievents.R
import com.example.unievents.data.AuthRepository
import com.example.unievents.data.User

@Composable
fun UserProfile(navController: NavController) {
    val user = remember { mutableStateOf<User?>(null) }
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        authRepository.getUser { result ->
            user.value = result
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(8.dp)
        ) {
            Text(
                text = "User Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            user.value?.let {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "User",
                modifier = Modifier
                    .padding(8.dp)
                    .height(100.dp)
            )
            Text(
                    text = "Email: ${it.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Name: ${it.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Role: ${it.role}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier =  Modifier.height(8.dp))


            }
    }
}}