package com.example.parkingai

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun ParkingSpacesScreen(navController: NavController) {
    val database = Firebase.database
    val spacesRef = database.getReference("parking_spaces")

    //val parkingSpaces = remember { mutableStateListOf<ParkingSpace>() }

    val parkingSpaces = remember {
        mutableStateListOf(
            ParkingSpace(1, true),
            ParkingSpace(2, true),
            ParkingSpace(3, true)
        )
    }

    // Escuchar cambios en Firebase
//    LaunchedEffect(Unit) {
//        spacesRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val spaces = snapshot.children.mapNotNull { spaceSnapshot ->
//                    spaceSnapshot.getValue(ParkingSpace::class.java)
//                }
//                parkingSpaces.clear()
//                parkingSpaces.addAll(spaces)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("ParkingSpaces", "Error en Firebase", error.toException())
//            }
//        })
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Estado de Espacios",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Mostrar los espacios
        parkingSpaces.forEach { space ->
            ParkingSpaceItem(space = space)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun ParkingSpaceItem(space: ParkingSpace) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (space.isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Espacio ${space.number}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = if (space.isAvailable) "DISPONIBLE" else "OCUPADO",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

data class ParkingSpace(
    val number: Int = 0,
    val isAvailable: Boolean = false,
    val occupiedBy: String? = null
) {
    constructor() : this(0, false, null)
}


/*
@Composable
fun ParkingSpacesScreen(navController: NavController) {
    // Estado para los espacios (simulado - luego conectaremos a Firebase)
    val parkingSpaces = remember {
        mutableStateListOf(
            ParkingSpace(1, true),
            ParkingSpace(2, false),
            ParkingSpace(3, true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Estado de Espacios",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Mostrar los 3 espacios
        parkingSpaces.forEach { space ->
            ParkingSpaceItem(space = space)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón de cerrar sesión
        Button(
            onClick = {
                Firebase.auth.signOut()
                navController.navigate("login") { popUpTo("home") { inclusive = true } }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun ParkingSpaceItem(space: ParkingSpace) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (space.isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Espacio ${space.number}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = if (space.isAvailable) "DISPONIBLE" else "OCUPADO",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

data class ParkingSpace(
    val number: Int,
    val isAvailable: Boolean
)

*/