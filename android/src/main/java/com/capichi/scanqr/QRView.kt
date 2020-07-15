package com.capichi.scanqr
import android.graphics.PointF
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter



class QRView(context: ThemedReactContext?) : ConstraintLayout(context), QRCodeReaderView.OnQRCodeReadListener {
    lateinit var viewCamera: QRCodeReaderView
    lateinit var viewTop: View
    lateinit var viewBottom: View
    lateinit var viewLeft: View
    lateinit var viewRight: View
    lateinit var viewCenter: View

//    var receivedQR: Boolean = false

    // biến này dùng để delay thời gian nhận QR code giữa các lần
    var canReceiveQR: Boolean = true

    // thời gian delay nhận QR code (mặc định 2000)
    var delayReceiveQR: Long = 2000

    var useOverlay: Boolean = false

    var enaDelay: Boolean = true


    init {
        LayoutInflater.from(context).inflate(R.layout.scan_qr_view, this)
        initQRCodeReaderView()
        onCameraReadyEvent()
    }

    // khoi tạo view đê quét mã QR (bao gồm camera hardware, surfaceview: preview)
    fun initQRCodeReaderView() {
        viewCamera = findViewById(R.id.qrdecoderview)
        viewTop = findViewById(R.id.view_top)
        viewBottom = findViewById(R.id.view_bottom)
        viewLeft = findViewById(R.id.view_left)
        viewRight = findViewById(R.id.view_right)
        viewCenter = findViewById(R.id.view_center)


        viewCamera.setAutofocusInterval(2000L)
        viewCamera.setOnQRCodeReadListener(this)
        viewCamera.setBackCamera()
        viewCamera.setLoggingEnabled(false)

        if(useOverlay) {
            showOverlay()
        }


    }

    // set props
    fun setDelay(delay: Int) {
        delayReceiveQR =  delay.toLong()
    }

    fun setEnableOverlay(enable: Boolean) {
        useOverlay = enable
        if(useOverlay) {
            showOverlay()
        }
        else {
            hideOverlay()
        }

    }

    fun setEnableDelay(enable: Boolean) {
        enaDelay = enable
    }


    // func hiển thị overlay
    fun showOverlay () {
        viewTop.visibility = View.VISIBLE
        viewBottom.visibility = View.VISIBLE
        viewLeft.visibility = View.VISIBLE
        viewRight.visibility = View.VISIBLE

    }

    // func ẩn overlay
    fun hideOverlay() {
        viewTop.visibility = View.INVISIBLE
        viewBottom.visibility = View.INVISIBLE
        viewLeft.visibility = View.INVISIBLE
        viewRight.visibility = View.INVISIBLE
    }


    // func lắng nghe khi đọc đc QR code
    override fun onQRCodeRead(text: String, points: Array<PointF>) {


        if(!canReceiveQR) return
        Log.e("onQRCodeRead", text)
        canReceiveQR = false

        if(!checkOvelayConstainQRcode(points)) {
            canReceiveQR = true
            return
        }

        onQRCodeReadEvent(text, points)
        if(enaDelay) {
            delayReceiveQR()
        }
    }

    /**
     * func kiem tra xem qr code quets dc có năm trong vùng được quét hay không
     * dựa vào mảng point trả về, tính toán ra 4 vị trí min, max và so sánh với center_view
     */
    fun checkOvelayConstainQRcode(points: Array<PointF>): Boolean {
        // nếu không sử dụng overlay thì mặc định bỏ qua
        if(!useOverlay) return true

        var minX : Float = Float.MAX_VALUE
        var minY: Float = Float.MAX_VALUE
        var maxX: Float = Float.MIN_VALUE
        var maxY: Float = Float.MIN_VALUE

        for (p: PointF in points) {
            if(minX > p.x) minX = p.x
            if(minY > p.y) minY = p.y
            if(maxX < p.x) maxX = p.x
            if(maxY < p.y) maxY - p.y
        }

        if(minX >= viewCenter.left && minY >= viewCenter.top && maxX <= viewCenter.right && maxY <= viewCenter.bottom) return true

        return false
    }

    fun delayReceiveQR() {
        if(canReceiveQR) canReceiveQR = false
        android.os.Handler().postDelayed({
            canReceiveQR = true
        }, delayReceiveQR )
    }

    // func method
    fun stopPreview() {
        viewCamera.stopCamera()
    }
    fun resumePreview() {
        viewCamera.startCamera()
        delayReceiveQR()
    }

    fun getArray(points: Array<PointF>): WritableArray?{
        val array = Arguments.createArray()
        for (p: PointF in points) {
            val writableMap = Arguments.createMap()
            writableMap.putDouble("x", p.x.toDouble())
            writableMap.putDouble("y", p.y.toDouble())
            array.pushMap(writableMap)
        }
        return array
    }

    // func props => react_native
    fun onQRCodeReadEvent(text: String, points: Array<PointF>) {
        val event = Arguments.createMap()
        val reactContext = context as ReactContext
        event.putString("code", text)
        val androidMap = Arguments.createMap()
        androidMap.putArray("points", getArray(points))
        event.putMap("android", androidMap)
        reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(
                id,
                "onQRCodeRead",
                event)
    }
    fun onCameraReadyEvent() {
        Log.e("haha", "haha")
        val event = Arguments.createMap()
        val reactContext = context as ReactContext
        reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(
                id,
                "onScanQRReady",
                event)
    }
}