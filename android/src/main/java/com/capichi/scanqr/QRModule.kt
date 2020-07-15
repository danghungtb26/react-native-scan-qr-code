package com.capichi.scanqr

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.uimanager.NativeViewHierarchyManager
import com.facebook.react.uimanager.UIBlock
import com.facebook.react.uimanager.UIManagerModule

class QRModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    val REACT_CLASS = "QRScan"
    private val reactContext: ReactApplicationContext? = reactContext
    override fun getName(): String {
        return REACT_CLASS
    }

    @ReactMethod
    fun stopPreview(viewTag: Int) {
        val uiManager = reactContext?.getNativeModule(UIManagerModule::class.java)
        uiManager?.addUIBlock(UIBlock { nativeViewHierarchyManager ->
            val recordView: QRView

            try {
                recordView = nativeViewHierarchyManager.resolveView(viewTag) as QRView
                recordView.stopPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    @ReactMethod
    fun resumePreview(viewTag: Int) {
        val uiManager = reactContext?.getNativeModule(UIManagerModule::class.java)
        uiManager?.addUIBlock(UIBlock { nativeViewHierarchyManager ->
            val recordView: QRView

            try {
                recordView = nativeViewHierarchyManager.resolveView(viewTag) as QRView
                recordView.resumePreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }
}