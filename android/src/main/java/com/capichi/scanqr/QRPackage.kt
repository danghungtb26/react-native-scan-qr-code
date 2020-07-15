package com.capichi.scanqr

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JavaScriptModule
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

import java.util.Arrays

class QRPackage : ReactPackage {

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return Arrays.asList(
                QRModule(reactContext)
        )
    }


    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        // https://facebook.github.io/react-native/docs/native-components-android.html#4-register-the-viewmanager
        // Register your native component's view manager
        return Arrays.asList<ViewManager<*, *>>(
                QRManager()
        )
    }

}