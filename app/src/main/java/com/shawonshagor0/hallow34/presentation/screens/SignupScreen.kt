package com.shawonshagor0.hallow34.presentation.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.shawonshagor0.hallow34.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.shawonshagor0.hallow34.presentation.screens.DropdownSelector


@Composable
fun SignupScreen(navController: NavController, bpNumber: String) {
    var fullName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var range by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf("") }

    val districts = listOf(
        "Dhaka", "Chattogram", "Rajshahi", "Khulna", "Barishal", "Sylhet", "Rangpur", "Mymensingh"
    )

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        imageUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Profile Picture
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Upload Photo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = designation, onValueChange = { designation = it }, label = { Text("Designation") }, modifier = Modifier.fillMaxWidth())
        DropdownSelector(label = "District", options = districts, selected = district, onSelect = { district = it })
        OutlinedTextField(value = range, onValueChange = { range = it }, label = { Text("Current Range") }, modifier = Modifier.fillMaxWidth())
        DropdownSelector(label = "Blood Group", options = bloodGroups, selected = bloodGroup, onSelect = { bloodGroup = it })
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (fullName.isBlank() || designation.isBlank() || district.isBlank() || range.isBlank() || bloodGroup.isBlank() || phone.isBlank() || email.isBlank()) {
                error = "All fields are required"
            } else {
                error = ""
                // Upload profile image first then save user
                uploadImageAndSaveUser(bpNumber, fullName, designation, district, range, bloodGroup, phone, email, imageUri, navController)
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Signup")
        }

        if (error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}


private fun uploadImageAndSaveUser(
    bpNumber: String,
    fullName: String,
    designation: String,
    district: String,
    range: String,
    bloodGroup: String,
    phone: String,
    email: String,
    imageUri: android.net.Uri?,
    navController: androidx.navigation.NavController
) {
    if (imageUri != null) {
        val ref = FirebaseStorage.getInstance().reference.child("profiles/$bpNumber.jpg")
        ref.putFile(imageUri)
            .continueWithTask { ref.downloadUrl }
            .addOnSuccessListener { downloadUri ->
                saveUser(bpNumber, fullName, designation, district, range, bloodGroup, phone, email, downloadUri.toString(), navController)
            }
    } else {
        saveUser(bpNumber, fullName, designation, district, range, bloodGroup, phone, email, "", navController)
    }
}

private fun saveUser(
    bpNumber: String,
    fullName: String,
    designation: String,
    district: String,
    range: String,
    bloodGroup: String,
    phone: String,
    email: String,
    imageUrl: String,
    navController: androidx.navigation.NavController
) {
    val user = User(bpNumber, fullName, designation, district, range, bloodGroup, phone, email, imageUrl)
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(bpNumber)
        .set(user)
        .addOnSuccessListener {
            navController.navigate("home") // navigate to Home after signup
        }
        .addOnFailureListener {
            println("Error saving user: ${it.message}")
        }
}
