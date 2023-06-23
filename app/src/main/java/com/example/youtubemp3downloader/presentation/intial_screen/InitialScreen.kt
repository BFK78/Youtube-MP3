package com.example.youtubemp3downloader.presentation.intial_screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavHostController
import com.example.youtubemp3downloader.R
import com.example.youtubemp3downloader.presentation.common.CustomButton
import com.example.youtubemp3downloader.presentation.common.CustomLoading
import com.example.youtubemp3downloader.presentation.ui.theme.PlusJakartaSans
import com.example.youtubemp3downloader.presentation.util.FileHelper
import com.example.youtubemp3downloader.presentation.util.Screens
import com.example.youtubemp3downloader.presentation.viewmodel.GrabbingScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun InitialScreen(
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: GrabbingScreenViewModel
) {

    val gettingVideoInfoLoading = remember {
        mutableStateOf(false)
    }

    var storageLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val storageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val documentFile = DocumentFile.fromTreeUri(context, it)
            val path = FileHelper.getDirectoryPath(documentFile)
            viewModel.onFilePathChanged(path)
        }
    }

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp),
        topBar = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.youtube_icon),
                        contentDescription = "Youtube Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )

                Text(
                    text = "MP3 Downloader",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    fontFamily = PlusJakartaSans
                )
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                CustomTextField(
                    modifier = Modifier,
                    label = stringResource(R.string.youtube_link),
                    value = viewModel.youtubeLink,
                    trailingIcon = Icons.Default.Link,
                    trailingIconContentDescription = stringResource(id = R.string.content_description_link_icon),
                    leadingIcon = Icons.Default.Close,
                    leadingIconContentDescription = stringResource(id = R.string.content_description_close_icon),
                    enabled = !gettingVideoInfoLoading.value,
                    onClick = {
                    },
                    onTextChange = {
                        viewModel.onYoutubeLinkChange(it)
                    }
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                CustomTextField(
                    modifier = Modifier,
                    label = stringResource(R.string.destination_folder),
                    value = viewModel.filePath,
                    trailingIcon = Icons.Default.Folder,
                    trailingIconContentDescription = stringResource(R.string.content_description_folder_name),
                    onTextChange = {
                    },
                    onClick = {
                        storageLauncher.launch(null)
                    },
                    infoText = stringResource(R.string.where_you_want_to_save_the_mp3),
                    readOnly = true,
                    enabled = !gettingVideoInfoLoading.value,
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "Download",
                    loading = gettingVideoInfoLoading.value,
                    loadingText = "Grabbing Info...",
                    enabled = !gettingVideoInfoLoading.value,
                ) {
                    scope.launch {
                        if (viewModel.youtubeLink.isEmpty() || viewModel.filePath.isEmpty()) {
                            snackbarHostState.showSnackbar("Please provide all the inputs.")
                            return@launch
                        }
                        gettingVideoInfoLoading.value = true
                        if (viewModel.getVideoInformation()) {
                            gettingVideoInfoLoading.value = false
                            navHostController.navigate(Screens.GrabbingScreen.route)
                        } else {
                            gettingVideoInfoLoading.value = false
                            snackbarHostState.showSnackbar("Something went wrong please try again!!, or contact support.")
                        }
                    }
                }

            }
            if (storageLoading) {
                CustomLoading()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trailingIcon: ImageVector,
    trailingIconContentDescription: String,
    leadingIcon: ImageVector? = null,
    infoText: String? = null,
    leadingIconContentDescription: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean,
    onClick: () -> Unit,
    onTextChange: (String) -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        Text(
            text = label,
            fontFamily = PlusJakartaSans,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            value = value,
            onValueChange = onTextChange,
            leadingIcon = {
                IconButton(
                    onClick = onClick
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = trailingIconContentDescription,
                        modifier = if (trailingIcon == Icons.Default.Link) Modifier.rotate(-45f) else Modifier
                    )
                }
            },
            trailingIcon = {
                leadingIcon?.let {
                    IconButton(onClick = { onTextChange("") }) {
                        Icon(
                            imageVector = it,
                            contentDescription = leadingIconContentDescription ?: ""
                        )
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFFAFAFA)
            ),
            readOnly = readOnly,
            maxLines = 1,
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(8.dp))

        infoText?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(16.dp),
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.text_field_info),
                    tint = Color.Gray,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    modifier = Modifier,
                    text = it,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = PlusJakartaSans
                )
            }
        }
    }
}




