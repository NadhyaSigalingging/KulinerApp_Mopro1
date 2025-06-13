package com.nadhya0065.catatankuliner.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nadhya0065.catatankuliner.R

@Composable
fun EditDialog(
    bitmap: Bitmap?,
    imageUrl: String? = null,
    defaultNama: String = "",
    defaultLokasi: String = "",
    defaultReview: String = "",
    onDismissRequest: () -> Unit,
    onSave: (String, String, String) -> Unit,
    onPickImage: () -> Unit
) {
    var nama by remember { mutableStateOf(defaultNama) }
    var lokasi by remember { mutableStateOf(defaultLokasi) }
    var review by remember { mutableStateOf(defaultReview) }
    var showError by remember { mutableStateOf(false) }

    val isEdit = defaultNama.isNotBlank() || defaultLokasi.isNotBlank() || defaultReview.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                if (nama.isBlank() || lokasi.isBlank() || review.isBlank()) {
                    showError = true
                } else {
                    onSave(nama, lokasi, review)
                }
            }) {
                Text(stringResource(R.string.simpan))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.batal))
            }
        },
        title = {
            Text(
                text = if (isEdit)
                    stringResource(R.string.edit_makanan)
                else
                    stringResource(R.string.tambah_makanan)
            )
        },
        text = {
            Column {
                when {
                    bitmap != null -> {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.preview_gambar),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                        TextButton(onClick = onPickImage) {
                            Text(stringResource(R.string.ganti_foto))
                        }
                    }
                    !imageUrl.isNullOrBlank() -> {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(R.string.preview_gambar),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(bottom = 8.dp)
                        )
                        TextButton(onClick = onPickImage) {
                            Text(stringResource(R.string.ganti_foto))
                        }
                    }
                    else -> {
                        TextButton(onClick = onPickImage) {
                            Text(stringResource(R.string.pilih_foto))
                        }
                    }
                }

                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        showError = false
                    },
                    label = { Text(stringResource(R.string.nama_makanan)) },
                    isError = showError && nama.isBlank(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = lokasi,
                    onValueChange = {
                        lokasi = it
                        showError = false
                    },
                    label = { Text(stringResource(R.string.lokasi)) },
                    isError = showError && lokasi.isBlank(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = review,
                    onValueChange = {
                        review = it
                        showError = false
                    },
                    label = { Text(stringResource(R.string.review)) },
                    isError = showError && review.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                if (showError) {
                    Text(
                        text = stringResource(R.string.input_kosong),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}
