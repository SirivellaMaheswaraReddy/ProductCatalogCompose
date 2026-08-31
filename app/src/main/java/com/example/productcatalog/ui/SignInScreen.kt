package com.example.productcatalog.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.password
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.productcatalog.R
import com.example.productcatalog.ui.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onNavigateBack: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: LoginViewModel,
    isDarkTheme: Boolean = false
) {
    // Observe the UI State from the ViewModel (MVVM Pattern)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    // Local UI state for toggling password visibility (doesn't belong in ViewModel)
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Sign In", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1C1B20),
                            Color.Black
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_login_image),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(150.dp)
                            .padding(4.dp)
                    )
                }


                // Email Field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Gmail/Email") },
                    modifier = Modifier.fillMaxWidth(),
                    // Show error state if ViewModel has an error message
                    isError = uiState.errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    // Show error state if ViewModel has an error message
                    isError = uiState.errorMessage != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(image, contentDescription = "Toggle password visibility")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.signIn(onSuccess = onSignInSuccess)
                        }
                    ),
                    singleLine = true
                )

                // Error Message Display
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In Button
                Button(
                    onClick = { viewModel.signIn(onSuccess = onSignInSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    // Disable button while loading to prevent multiple clicks
                    enabled = !uiState.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        append("Please login using below credentials:\n")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("• browserstack@mailinator.com / 123456\n         OR          \n")
                            append("• demouser / testingfun99\n")
                        }
                        /*append("• testadmin@mailinator.com / 123456\n")
                    append("• demoapp@gamail.com / demoapp@123")*/
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)
                )
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_android_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(120.dp)
                            .padding(4.dp)
                    )
                }
            }
        }

    }
}
