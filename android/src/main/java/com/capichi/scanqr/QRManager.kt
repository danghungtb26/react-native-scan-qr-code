package com.capichi.scanqr

import android.view.View
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.common.MapBuilder

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext

import com.facebook.react.uimanager.annotations.ReactProp

class QRManager : SimpleViewManager<QRView>() {

    override fun getName(): String {
        // Tell React the name of the module
        // https://facebook.github.io/react-native/docs/native-components-android.html#1-create-the-viewmanager-subclass
        return REACT_CLASS
    }

    public override fun createViewInstance(context: ThemedReactContext): QRView {
        // Create a view here
        return QRView(context)
    }

    @ReactProp(name = "delayReceive")
    fun setExampleProp(view: QRView, delay: Int) {
        view.setDelay(delay)
    }

    @ReactProp(name = "enableOverlay")
    fun setEnabelOverlay(view: QRView, enable: Boolean) {
        view.setEnableOverlay(enable)
    }

    @ReactProp(name = "enableDelay")
    fun setEnableDelay(view: QRView, enable: Boolean) {
        view.setEnableDelay(enable)
    }

    @ReactMethod
    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
        return MapBuilder.builder<String, Any>()
                .put("onQRCodeRead",
                        MapBuilder.of("registrationName", "onQRCodeRead"))
                .put("onScanQRReady",
                        MapBuilder.of("registrationName", "onScanQRReady"))
                .build()
    }

    companion object {
        val REACT_CLASS = "QRScan"
    }
}