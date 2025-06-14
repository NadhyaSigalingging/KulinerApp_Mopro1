package com.nadhya0065.catatankuliner.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.nadhya0065.catatankuliner.BuildConfig
import com.nadhya0065.catatankuliner.R
import com.nadhya0065.catatankuliner.model.Kuliner
import com.nadhya0065.catatankuliner.model.User
import com.nadhya0065.catatankuliner.model.gambarUrl
import com.nadhya0065.catatankuliner.network.ApiStatus
import com.nadhya0065.catatankuliner.network.KulinerApi
import com.nadhya0065.catatankuliner.network.UserDataStore
import com.nadhya0065.catatankuliner.ui.theme.CatatanKulinerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showKulinerDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showKulinerDialog = true
    }

    var editKuliner by remember { mutableStateOf<Kuliner?>(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(
                        onClick = {
                            if (user.email.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                            } else {
                                showDialog = true
                            }
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editKuliner = null
                bitmap = null
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah_makanan)
                )
            }
        }
    ) { innerPadding ->
        ScreenContent(
            viewModel = viewModel,
            userId = user.email,
            modifier = Modifier.padding(innerPadding),
            onEditClicked = { kuliner ->
                editKuliner = kuliner
                bitmap = null
                showKulinerDialog = true
            }
        )
    }

    if (showDialog) {
        ProfilDialog(
            user = user,
            onDismissRequest = { showDialog = false }
        ) {
            CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
            showDialog = false
        }
    }

    if (showKulinerDialog) {
        EditDialog(
            bitmap = bitmap,
            imageUrl = editKuliner?.gambarUrl, //
            defaultNama = editKuliner?.nama_makanan ?: "",
            defaultLokasi = editKuliner?.lokasi ?: "",
            defaultReview = editKuliner?.review ?: "",
            onDismissRequest = { showKulinerDialog = false },
            onPickImage = {
                val options = CropImageContractOptions(
                    null,
                    CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            },
            onSave = { nama_makanan, lokasi, review ->
                val kuliner = editKuliner
                val gambar = bitmap

                if (kuliner == null) {
                    if (gambar != null) {
                        viewModel.saveData(
                            userId = user.email,
                            nama_makanan = nama_makanan,
                            lokasi = lokasi,
                            review = review,
                            bitmap = gambar
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Gambar belum dipilih!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@EditDialog
                    }
                } else {
                    viewModel.editData(
                        id = kuliner.id,
                        userId = user.email,
                        nama_makanan = nama_makanan,
                        lokasi = lokasi,
                        review = review,
                        bitmap = gambar
                    )
                }

                showKulinerDialog = false
            }

        )
    }



    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        viewModel.clearMessage()
    }
}

@Composable
fun ScreenContent(
    viewModel: MainViewModel,
    userId: String,
    modifier: Modifier = Modifier,
    onEditClicked: (Kuliner) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedKuliner by remember { mutableStateOf<Kuliner?>(null) }

    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    LaunchedEffect(userId) {
        viewModel.retriveData(userId)
    }

    if (showDeleteDialog && selectedKuliner != null) {
        DialogHapus(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteKuliner(selectedKuliner!!.id, userId)
                showDeleteDialog = false
            }
        )
    }

    when (status) {
        ApiStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        ApiStatus.SUCCESS -> {
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize().padding(4.dp),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(data) { kuliner ->
                    ListItem(
                        kuliner = kuliner,
                        onDeleteClicked = {
                            selectedKuliner = it
                            showDeleteDialog = true
                        },
                        onEditClicked = { onEditClicked(it) }
                    )
                }
            }
        }
        ApiStatus.FAILED -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retriveData(userId) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.try_again))
                }
            }
        }
    }
}

@Composable
fun ListItem(
    kuliner: Kuliner,
    onDeleteClicked: (Kuliner) -> Unit,
    onEditClicked: (Kuliner) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(KulinerApi.getKulinerUrl(kuliner.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.gambar, kuliner.nama_makanan),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color(0f, 0f, 0f, 0.5f))
                .padding(8.dp)
        ) {
            Text(text = kuliner.nama_makanan, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = kuliner.lokasi, fontStyle = FontStyle.Italic, fontSize = 14.sp, color = Color.White)
            Text(text = kuliner.review, fontStyle = FontStyle.Italic, fontSize = 14.sp, color = Color.White)
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .background(Color(0f, 0f, 0f, 0.4f), shape = RoundedCornerShape(8.dp))
        ) {
            IconButton(
                onClick = { onEditClicked(kuliner) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = { onDeleteClicked(kuliner) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


private suspend fun signIn(context: Context,dataStore: UserDataStore){
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context,request)
        handSignIn(result, dataStore)
    }catch (e: GetCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handSignIn(
    result: GetCredentialResponse,
    dataStore: UserDataStore
){
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama,email,photoUrl))
        }catch (e: GoogleIdTokenParsingException){
            Log.e("SIGN-IN", "Error: unreconized custum credential type.")
        }
    }
}

private suspend fun signOut(context: Context,dataStore: UserDataStore){
    try {
        val credentialManager =CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    }catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }
    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    CatatanKulinerTheme  {
        MainScreen()
    }
}