package com.example.parkingai

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.* //
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.parkingai.ui.theme.ParkingAITheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.filled.ExitToApp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            ParkingAITheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home") { HomeScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_carro))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(500)
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "SmartParking", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val db = Firebase.firestore
    val auth = Firebase.auth

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0ED2F7), Color(0xFFB2FEFA)) // Fondo degradado
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SmartParking",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF0ED2F7)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)), // Fondo blanco para el campo
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF0ED2F7),
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)), // Fondo blanco para el campo
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF0ED2F7),
                        unfocusedIndicatorColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            db.collection("usuarios")
                                .whereEqualTo("usuario", username)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        val userDoc = documents.documents[0]
                                        val email = userDoc.getString("correo")

                                        if (!email.isNullOrEmpty()) {
                                            auth.signInWithEmailAndPassword(email, password)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("home") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                                }
                                        } else {
                                            Toast.makeText(context, "Correo no encontrado para este usuario", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error al buscar el usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp)), // Botón con bordes redondeados
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0ED2F7))
                ) {
                    Text("Iniciar Sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¿No tienes cuenta? Regístrate",
                    color = Color(0xFF0ED2F7),
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }
        }
    }
}


@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthdate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = calendar.timeInMillis
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0ED2F7), Color(0xFFB2FEFA))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color(0xFF0ED2F7),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                val textFieldColors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF0ED2F7),
                    unfocusedIndicatorColor = Color.LightGray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre de usuario", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0ED2F7).copy(alpha = 0.1f),
                        contentColor = Color(0xFF0ED2F7)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF0ED2F7))
                ) {
                    Text(
                        text = if (birthdate.isNotEmpty()) "Fecha: $birthdate"
                        else "Seleccionar fecha de nacimiento"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || username.isBlank() || password.isBlank() || birthdate.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val auth = Firebase.auth
                        val db = Firebase.firestore

                        db.collection("usuarios")
                            .whereEqualTo("usuario", username)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnSuccessListener {
                                            val user = hashMapOf(
                                                "nombre" to name,
                                                "correo" to email,
                                                "usuario" to username,
                                                "fechaNacimiento" to birthdate,
                                                "rfidTag" to ""
                                            )

                                            db.collection("usuarios")
                                                .document(it.user?.uid ?: "")
                                                .set(user)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("login") {
                                                        popUpTo("register") { inclusive = true }
                                                    }
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0ED2F7),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Text("Registrarse", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = Color(0xFF0ED2F7),
                    modifier = Modifier.clickable {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    var espacios by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("parking_spaces")
            .get()
            .addOnSuccessListener { result ->
                espacios = result.documents.mapNotNull { doc ->
                    mapOf(
                        "number" to (doc.getString("number") ?: "0"),
                        "isAvailable" to (doc.getBoolean("isAvailable") ?: false)
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SmartParking",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0ED2F7)
                )
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                        )
                    )
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tarjeta de bienvenida
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Bienvenido",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color(0xFF0ED2F7),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Estado de espacios disponibles",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }

                    // Lista de espacios
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(espacios, key = { it["number"].toString() }) { espacio ->
                            val numero = espacio["number"] as? String ?: "N/A"
                            val disponible = espacio["isAvailable"] as? Boolean ?: false
                            val colorFondo = if (disponible) Color(0xFF4CAF50) else Color(0xFFF44336)
                            val colorTexto = Color.White

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = colorFondo),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "Espacio #$numero",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                color = colorTexto,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            if (disponible) "Disponible" else "Ocupado",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = colorTexto.copy(alpha = 0.9f)
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector = if (disponible) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = "Estado del espacio",
                                        tint = colorTexto,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Botón de cerrar sesión
                    Button(
                        onClick = {
                            auth.signOut()
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0ED2F7),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar sesión",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ParkingAITheme {
        LoginScreen(navController = rememberNavController())
    }
}

// MainActivity.kt
/*

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthdate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { datePickerDialog.show() }) {
            Text(text = if (birthdate.isNotEmpty()) "Fecha: $birthdate" else "Seleccionar fecha de nacimiento")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val auth = Firebase.auth
                val db = Firebase.firestore

                db.collection("usuarios")
                    .whereEqualTo("usuario", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            Toast.makeText(context, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = task.result?.user?.uid

                                        val nuevoUsuario = hashMapOf(
                                            "nombre" to name,
                                            "correo" to email,
                                            "usuario" to username,
                                            "fechaNacimiento" to birthdate,
                                            "rfidTag" to ""
                                        )

                                        uid?.let {
                                            db.collection("usuarios").document(it)
                                                .set(nuevoUsuario)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("login")
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al verificar el nombre de usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Ya tienes cuenta? Inicia sesión",
            modifier = Modifier.clickable {
                navController.navigate("login")
            }
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = Firebase.firestore
    var espacios by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(Unit) {
        db.collection("parking_spaces")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    mapOf(
                        "number" to (doc.getString("number") ?: "0"),
                        "isAvailable" to (doc.getBoolean("isAvailable") ?: false)
                    )
                }
                espacios = lista
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("SmartParking")
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(32.dp))

                Text("Espacios de estacionamiento:", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                espacios.forEachIndexed { index, espacio ->
                    val numero = espacio["number"] as? String ?: "N/A"
                    val disponible = espacio["isAvailable"] as? Boolean ?: false
                    val color = if (disponible) Color(0xFFA5D6A7) else Color(0xFFEF9A9A)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(color),
                        colors = CardDefaults.cardColors(containerColor = color)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Lugar: $numero", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Disponible: ${if (disponible) "Sí" else "No"}")
                        }
                    }
                }

                Button(
                    onClick = {
                        auth.signOut()
                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }

            }
        }
    )
}


*/


