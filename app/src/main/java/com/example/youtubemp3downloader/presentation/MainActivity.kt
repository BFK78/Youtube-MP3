package com.example.youtubemp3downloader.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.youtubemp3downloader.presentation.grabbing_screen.GrabbingScreen
import com.example.youtubemp3downloader.presentation.intial_screen.InitialScreen
import com.example.youtubemp3downloader.presentation.ui.theme.YoutubeMP3DownloaderTheme
import com.example.youtubemp3downloader.presentation.util.Screens
import com.example.youtubemp3downloader.presentation.viewmodel.GrabbingScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )

            val navHostController = rememberNavController()

            val snackbarHostState = remember {
                SnackbarHostState()
            }

            LaunchedEffect(key1 = permissionState) {
                if (!permissionState.allPermissionsGranted && !permissionState.permissionRequested) {
                    permissionState.launchMultiplePermissionRequest()
                }

                if (permissionState.shouldShowRationale) {
                    snackbarHostState.showSnackbar("Please give the permission.")
                }
            }

            YoutubeMP3DownloaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {

                        ApplicationNavHost(
                            navController = navHostController,
                            snackbarHostState = snackbarHostState
                        )

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: GrabbingScreenViewModel = hiltViewModel()
) {
    NavHost(navController = navController, startDestination = Screens.InitialScreen.route) {
        composable(Screens.InitialScreen.route) {
            InitialScreen(
                navHostController = navController,
                snackbarHostState = snackbarHostState,
                viewModel = viewModel
            )
        }

        composable(Screens.GrabbingScreen.route) {
            GrabbingScreen(
                navHostController = navController,
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YoutubeMP3DownloaderTheme {
        Greeting("Android")
    }
}