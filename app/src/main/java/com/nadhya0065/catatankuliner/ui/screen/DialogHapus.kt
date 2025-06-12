package com.nadhya0065.catatankuliner.ui.screen

import android.content.res.Configuration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nadhya0065.catatankuliner.R
import com.nadhya0065.catatankuliner.ui.theme.CatatanKulinerTheme

@Composable
fun DialogHapus(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit

){
    AlertDialog(
        text = { Text(text = stringResource(id = R.string.pesan_hapus)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.hapus))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.batal))
            }
        },
        onDismissRequest = { onDismiss() },

        )
}

@Preview(showSystemUi = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showSystemUi = true)
@Composable
fun DialogHapusPreview() {
    CatatanKulinerTheme {
        DialogHapus(
            onDismiss = { },
            onConfirm = { }
        )
    }
}