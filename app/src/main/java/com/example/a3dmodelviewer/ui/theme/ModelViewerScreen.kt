package com.example.a3dmodelviewer.ui.theme
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.a3dmodelviewer.model.ModelData
import com.example.a3dmodelviewer.R

private const val MAX_MODELS = 5

@Composable
fun ModelViewerScreen() {
    val context = LocalContext.current
    val activeModels = remember { mutableStateListOf<ModelData>() }
    var showPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Empty state
            if (activeModels.isEmpty()) {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Render each model container
            activeModels.forEachIndexed { index, model ->
                ModelContainerView(
                    model = model,
                    onClose = {
                        activeModels.removeAt(index)
                    },
                    onModelDataChange = { updated ->
                        activeModels[index] = updated
                    }
                )
            }

            // FAB
            FloatingActionButton(
                onClick = {
                    if (activeModels.size >= MAX_MODELS) {
                        Toast.makeText(
                            context,
                            "Maximum $MAX_MODELS models on screen. Remove one first.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showPicker = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_model),
                    contentDescription = stringResource(R.string.add_model),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    // Model picker bottom sheet
    if (showPicker) {
        ModelPickerSheet(
            onDismiss = { showPicker = false },
            onModelSelected = { bundled ->
                showPicker = false
                val offset = activeModels.size * 40f
                activeModels.add(
                    ModelData(
                        assetName = bundled.assetName,
                        displayName = bundled.displayName,
                        containerPosition = PointF(offset, offset),
                        containerSize = ModelData.DEFAULT_SIZE
                    )
                )
            }
        )
    }
}