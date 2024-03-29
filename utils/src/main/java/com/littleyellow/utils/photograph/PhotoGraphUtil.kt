package com.littleyellow.utils.photograph

import android.graphics.Bitmap
import androidx.camera.core.TorchState
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView

/**
 * dependencies {
// CameraX core library using the camera2 implementation
val camerax_version = "1.3.0-alpha04"
// The following line is optional, as the core library is included indirectly by camera-camera2
implementation("androidx.camera:camera-core:${camerax_version}")
implementation("androidx.camera:camera-camera2:${camerax_version}")
// If you want to additionally use the CameraX Lifecycle library
implementation("androidx.camera:camera-lifecycle:${camerax_version}")
// If you want to additionally use the CameraX VideoCapture library
implementation("androidx.camera:camera-video:${camerax_version}")
// If you want to additionally use the CameraX View class
implementation("androidx.camera:camera-view:${camerax_version}")
// If you want to additionally add CameraX ML Kit Vision Integration
implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
// If you want to additionally use the CameraX Extensions library
implementation("androidx.camera:camera-extensions:${camerax_version}")
}
 */
class PhotoGraphUtil {}

fun cropBitmap(
    bitmap: Bitmap,
    vW: Int,
    vH: Int,
    vX: Int,
    vY: Int,
    vCropW: Int,
    vCropH: Int
): Bitmap {
    val srcW = bitmap.width // 得到图片的宽，高
    val srcH = bitmap.height
    val wRatio = vW * 1.0f / srcW
    val hRatio = vH * 1.0f / srcH
    return Bitmap.createBitmap(
        bitmap,
        (vX / wRatio).toInt(),
        (vY / hRatio).toInt(),
        (vCropW / wRatio).toInt(),
        (vCropH / hRatio).toInt(),
        null,
        false
    )
}

fun LifecycleCameraController.toggleTorch(){
    if(TorchState.ON == torchState.value){
        enableTorch(false)
    }else{
        enableTorch(true)
    }
}