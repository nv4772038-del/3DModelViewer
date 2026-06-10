package com.example.a3dmodelviewer.ui.theme


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3dmodelviewer.model.BundledModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelPickerSheet(
    onDismiss: () -> Unit,
    onModelSelected: (BundledModel) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Choose a model",
                fontSize = 16.sp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    bottom = 8.dp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider()
            BundledModel.entries.forEach { model ->
                TextButton(
                    onClick = { onModelSelected(model) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = model.displayName,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}