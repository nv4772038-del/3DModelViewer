# 3D Model Viewer — Android

A single-activity Android app that loads and displays multiple 3D models (.glb) on a free-form canvas. Built with Jetpack Compose and Filament (via SceneView).

---

## Features

- **Single Activity** — no fragments, no second screen
- **Multiple Models** — up to 5 models on screen simultaneously
- **Draggable Containers** — move any model anywhere on the canvas
- **Resizable Containers** — pinch to resize each model container
- **Two Interaction Modes**
  - Normal mode: drag moves container, pinch resizes it
  - Interact mode: drag rotates 3D model, pinch zooms camera
- **Close Button** — removes model from canvas instantly

---

## Performance Decisions

| Decision | Reason |
|---|---|
| 5-model cap | Each SceneView maintains its own Filament engine. >5 simultaneous GL surfaces causes OOM crashes on low-end devices (2GB RAM, Mali-400) |
| Filament via SceneView | Handles material cache and geometry buffer pools automatically — no manual GL resource management |
| Jetpack Compose UI | Declarative, no XML inflation overhead |
| `hardwareAccelerated=true` | FrameLayout compositing goes through RenderThread, not CPU |
| GLB loaded from res/raw via ByteBuffer | Avoids asset pipeline overhead, direct memory mapping |

---

## Tech Stack

- **Language** — Kotlin
- **UI** — Jetpack Compose (Material3)
- **3D Rendering** — SceneView 2.3.0 (Filament)
- **Min SDK** — 24
- **Target SDK** — 37

---

## Project Structure







com.example.a3dmodelviewer/
├── model/
│   ├── BundledModel.kt       — enum of bundled .glb assets
│   └── ModelData.kt          — data class for model state
├── utils/
│   └── GestureHandler.kt     — drag/pinch gesture logic
├── ui/
│   ├── MainActivity.kt       — single entry point
│   ├── ModelViewerScreen.kt  — canvas + FAB + empty state
│   ├── ModelContainerView.kt — draggable SceneView container
│   ├── ModelPickerSheet.kt   — bottom sheet model selector
│   └── EmptyStateView.kt     — empty canvas hint
└── res/
└── raw/                  — bundled .glb model files
