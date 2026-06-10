package com.example.a3dmodelviewer.ui.theme

import android.graphics.PointF
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a3dmodelviewer.model.ModelData
import com.example.a3dmodelviewer.utils.GestureHandler
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView
import java.nio.ByteBuffer
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModelContainerView(
    model: ModelData,
    onClose: () -> Unit,
    onModelDataChange: (ModelData) -> Unit
) {
    val context = LocalContext.current

    var offsetX by remember { mutableFloatStateOf(model.containerPosition.x) }
    var offsetY by remember { mutableFloatStateOf(model.containerPosition.y) }
    var size by remember { mutableFloatStateOf(model.containerSize) }
    var isInteractMode by remember { mutableStateOf(model.isInteractMode) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val collisionSystem = rememberCollisionSystem(view)
    val modelNodeState = remember { mutableStateOf<ModelNode?>(null) }

    val cameraNode = rememberCameraNode(engine) {
        position = Position(z = 4.0f)
    }

    val gestureHandler = remember {
        GestureHandler(context, object : GestureHandler.Callbacks {
            override fun isInteractMode() = isInteractMode

            override fun onContainerTranslate(dx: Float, dy: Float) {
                offsetX += dx
                offsetY += dy
                onModelDataChange(
                    model.copy(containerPosition = PointF(offsetX, offsetY))
                )
            }

            override fun onContainerScale(scaleFactor: Float) {
                size = (size * scaleFactor)
                    .coerceIn(ModelData.MIN_SIZE, ModelData.MAX_SIZE)
                onModelDataChange(model.copy(containerSize = size))
            }

            override fun onModelRotate(dx: Float, dy: Float) {
                modelNodeState.value?.let { node ->
                    val current = node.rotation
                    node.rotation = io.github.sceneview.math.Rotation(
                        current.x + dy * 0.5f,
                        current.y + dx * 0.5f,
                        current.z
                    )
                }
            }

            override fun onModelZoom(scaleFactor: Float) {
                val pos = cameraNode.position
                cameraNode.position = Position(
                    pos.x, pos.y,
                    (pos.z / scaleFactor).coerceIn(1f, 16f)
                )
            }
        })
    }

    // res/raw/ → ByteBuffer → ModelInstance
    val childNodes = rememberNodes {
        try {
            val rawId = context.resources.getIdentifier(
                model.assetName, "raw", context.packageName
            )
            if (rawId != 0) {
                val inputStream = context.resources.openRawResource(rawId)
                val bytes = inputStream.readBytes()
                inputStream.close()
                val buffer = ByteBuffer.wrap(bytes)
                val instance = modelLoader.createModelInstance(buffer)
                instance?.let {
                    add(
                        ModelNode(
                            modelInstance = it,
                            scaleToUnits = 1f
                        ).also { node ->
                            modelNodeState.value = node
                        }
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(size.dp)
            .border(
                width = if (isInteractMode) 2.dp else 0.5.dp,
                color = if (isInteractMode)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInteropFilter { event ->
                gestureHandler.onTouchEvent(event)
                true
            }
    ) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            view = view,
            renderer = renderer,
            scene = scene,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            environmentLoader = environmentLoader,
            collisionSystem = collisionSystem,
            cameraNode = cameraNode,
            cameraManipulator = rememberCameraManipulator(),
            mainLightNode = rememberMainLightNode(engine) {
                intensity = 100_000.0f
            },
            childNodes = childNodes
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    isInteractMode = !isInteractMode
                    onModelDataChange(model.copy(isInteractMode = isInteractMode))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isInteractMode)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = if (isInteractMode) "Exit" else "Interact",
                    fontSize = 11.sp,
                    color = if (isInteractMode)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    text = "✕",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Text(
            text = model.displayName,
            fontSize = 11.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}