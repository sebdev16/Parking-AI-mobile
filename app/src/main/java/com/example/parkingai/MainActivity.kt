package com.example.parkingai

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.example.parkingai.ui.theme.ParkingAITheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.util.*

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
                    composable("parking") { ParkingScreen(navController) }
                    composable("reserve/{spotNumber}") { backStackEntry ->
                        val spotNumber = backStackEntry.arguments?.getString("spotNumber")?.toIntOrNull() ?: 1
                        ReservationScreen(navController, spotNumber)
                    }
                    composable("myReservations") { MyReservationsScreen(navController) }
                }
            }
        }
    }
}


//---Clase para las notificaciones------------------------------------------------------------------

class NotificationHelper(private val context: Context) {
    private val channelId = "parking_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Parking Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for parking spot availability"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(spotNumber: Int) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_car)
            .setContentTitle("¡Espacio disponible!")
            .setContentText("El lugar $spotNumber se ha liberado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(spotNumber, notification)
    }
}



//--------------------------fin de la clase notificaciones------------------------------------------


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
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
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

        Spacer(modifier = Modifier.height(16.dp))
//--------------------modifique esta session del boton-----------------------------------------------------------
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    navController.navigate("parking") // Redirige después del login
                } else {
                    Toast.makeText(context, "Usuario/contraseña requeridos", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Iniciar Sesión")
        }
//----------------------------------------------------------------------------------------------------------------
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿No tienes cuenta? Regístrate",
            modifier = Modifier.clickable {
                navController.navigate("register")
            }
        )
    }
}

//added

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
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        } else {
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ParkingAITheme {
        Text("Bienvenido a Parking AI")
    }
}
//--------------------------------------------mgz añadidos------------------------------------------
@Composable
fun ParkingScreen(navController: NavController) {
    val database = Firebase.database.reference
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    // Estado de los espacios (ocupado, tiempo de ocupación)
    val parkingSpots = remember { mutableStateListOf<Pair<Boolean, Long?>>(
        Pair(false, null),
        Pair(false, null),
        Pair(false, null)
    )}

    // Escuchar cambios en Firebase
    LaunchedEffect(Unit) {
        database.child("parkingSpots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEachIndexed { index, child ->
                    val isOccupied = child.child("occupied").getValue(Boolean::class.java) ?: false
                    val timestamp = child.child("since").getValue(Long::class.java)

                    // Verificar si un espacio se acaba de liberar para notificar
                    if (parkingSpots[index].first && !isOccupied) {
                        notificationHelper.showNotification(index + 1)
                    }

                    parkingSpots[index] = Pair(isOccupied, timestamp)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para actualizar estado
    fun updateSpot(index: Int, occupied: Boolean) {
        val updates = hashMapOf<String, Any>(
            "occupied" to occupied,
            "since" to (if (occupied) System.currentTimeMillis() else null)
        )

        database.child("parkingSpots").child(index.toString()).updateChildren(updates)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Estacionamiento Inteligente", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar espacios
        parkingSpots.forEachIndexed { index, (isOccupied, occupiedSince) ->
            ParkingSpot(
                spotNumber = index + 1,
                isOccupied = isOccupied,
                occupiedSince = occupiedSince,
                onClick = {
                    if (!isOccupied) {
                        navController.navigate("reserve/${index + 1}")
                    } else {
                        updateSpot(index, false)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Botón para ver reservas
        Button(
            onClick = { navController.navigate("myReservations") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis Reservas")
        }
    }
}
//--------------------------------------------------------------------------------------------------
@Composable
fun ParkingSpot(spotNumber: Int, isOccupied: Boolean, occupiedSince: Long?, onClick: () -> Unit) {
    val occupationTime = if (isOccupied && occupiedSince != null) {
        val minutes = (System.currentTimeMillis() - occupiedSince) / 60000
        "$minutes min"
    } else {
        "Libre"
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = if (isOccupied) Color.Red else Color.Green,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isOccupied) Icons.Default.DirectionsCar else Icons.Default.Parking,
                contentDescription = "Estado",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lugar $spotNumber",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = occupationTime,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun ReservationScreen(navController: NavController, spotNumber: Int) {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var duration by remember { mutableStateOf(60) } // minutos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Reservar Lugar $spotNumber", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de hora
        Button(onClick = {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(0)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    set(Calendar.MINUTE, timePicker.minute)
                }
                selectedTime = calendar.timeInMillis
            }

            timePicker.show((context as Activity).fragmentManager, "timePicker")
        }) {
            Text("Seleccionar Hora")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de duración
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Duración (min):")
            Slider(
                value = duration.toFloat(),
                onValueChange = { duration = it.toInt() },
                valueRange = 30f..180f,
                steps = 5,
                modifier = Modifier.weight(1f)
            )
            Text("$duration")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val reservation = Reservation(
                    userId = Firebase.auth.currentUser?.uid ?: "",
                    spotNumber = spotNumber,
                    startTime = selectedTime,
                    endTime = selectedTime + duration * 60000,
                    isActive = true
                )

                Firebase.database.reference.child("reservations").push().setValue(reservation)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Reserva exitosa", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Reserva")
        }
    }
}
//--------------------------------------------------------------------------------------------------

@Composable
fun MyReservationsScreen(navController: NavController) {
    val reservations = remember { mutableStateListOf<Reservation>() }
    val userId = Firebase.auth.currentUser?.uid ?: ""
    val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }

    LaunchedEffect(userId) {
        Firebase.database.reference.child("reservations")
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    reservations.clear()
                    snapshot.children.forEach { child ->
                        child.getValue(Reservation::class.java)?.let {
                            reservations.add(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mis Reservas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (reservations.isEmpty()) {
            Text("No tienes reservas activas")
        } else {
            LazyColumn {
                items(reservations) { reservation ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Lugar ${reservation.spotNumber}", fontWeight = FontWeight.Bold)
                            Text("Inicio: ${dateFormat.format(Date(reservation.startTime))}")
                            Text("Fin: ${dateFormat.format(Date(reservation.endTime))}")

                            Button(
                                onClick = {
                                    Firebase.database.reference.child("reservations")
                                        .child(reservation.key ?: "").removeValue()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Cancelar Reserva")
                            }
                        }
                    }
                }
            }
        }
    }
}



/*

Solo por si acaso

Añade esta dependencia en build.gradle (Module:app):

implementation 'com.google.firebase:firebase-database-ktx:20.3.1'




NOTA EXTRA PARA ReservetionScreen:

Primero, actualiza el modelo de datos en Firebase para incluir reservas:

data class Reservation(
    val userId: String = "",
    val spotNumber: Int = 0,
    val startTime: Long = 0,
    val endTime: Long = 0,
    val isActive: Boolean = true
)



TAMBIEN ES IMPORTANTE QUE ...

Añadir estas dependencias en tu build.gradle:

implementation "androidx.compose.material:material-icons-extended:$compose_version"  si no te lo acepta asi quiza en este formato -> implementation ("androidx.compose.material:material-icons-extended:$compose_version")
implementation "com.google.accompanist:accompanist-permissions:0.28.0"

Configura las notificaciones en AndroidManifest.xml:

xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>




La estructura de datos en el FIREBASE DEBERIA SER ALGO ASI

parkingSpots
   0
      occupied: boolean
      since: timestamp
   1
      occupied: boolean
      since: timestamp
   2
      occupied: boolean
      since: timestamp

reservations
   -RandomId
      userId: string
      spotNumber: number
      startTime: timestamp
      endTime: timestamp
      isActive: boolean





TODO lo que hice aqui deberia:

* Iconos visuales para cada estado

* Temporizador de ocupación

* Notificaciones cuando se libera un espacio

* Sistema completo de reservas

* Visualización y cancelación de reservas activas

 */
