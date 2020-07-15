//
//  QRScanView.swift
//  RNQRCode
//
//  Created by HungDV on 7/2/20.
//

import Foundation
import UIKit
import AVFoundation


let QRImageSize: CGFloat = 250

/// Delegate callback for the QRScannerView.
protocol QRScannerViewDelegate: class {
    func qrScanningDidFail()
    func qrScanningSucceededWithCode(_ str: String?)
    func qrScanningDidStop()
}

@objc(QRScanView)
class QRScanView: RCTView {
    @IBOutlet weak var viewTop:UIView!
    @IBOutlet weak var viewBottom:UIView!
    @IBOutlet weak var viewRight:UIView!
    @IBOutlet weak var viewLeft:UIView!
  
    @IBOutlet weak var viewCenter: UIView!
  
    weak var delegate: QRScannerViewDelegate?
  
    @objc var enableOverlay: Bool = false {
      didSet {
        setUpOverlay()
      }
    }
  
  @objc var enableDelay: Bool = true {
    didSet{
      NSLog("enableDelay")
    }
  }
  
    var canReceiveQRCode:Bool = true
  
  @objc var delayReceive: NSInteger = 2000
  
    @objc var onQRCodeRead: RCTDirectEventBlock?
    
    /// capture settion which allows us to start and stop scanning.
    var captureSession: AVCaptureSession?
    
    // Init methods..
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        doInitialSetup()
    }
    override init(frame: CGRect) {
        super.init(frame: frame)
//      let _ = loadViewFromNib()
      
      
      
      
        
    }
  
  
  override func reactSetFrame(_ frame: CGRect) {
    super.reactSetFrame(frame)
    self.frame = frame
    let _ = loadViewFromNib()
    doInitialSetup()
  }
    
    //MARK: overriding the layerClass to return `AVCaptureVideoPreviewLayer`.
    override class var layerClass: AnyClass  {
        return AVCaptureVideoPreviewLayer.self
    }
    override var layer: AVCaptureVideoPreviewLayer {
        return super.layer as! AVCaptureVideoPreviewLayer
    }
  
  
}
extension QRScanView {
  
  func setUpOverlay() {
    if(enableOverlay) {
      viewTop?.isHidden = false
      viewBottom?.isHidden = false
      viewRight?.isHidden = false
      viewLeft?.isHidden = false
      
    }
    else {
      viewTop?.isHidden = true
      viewBottom?.isHidden = true
      viewRight?.isHidden = true
      viewLeft?.isHidden = true
    }
  }
  
  func loadViewFromNib() -> UIView {
    let bundle = Bundle.init(for: type(of: self))
    let nib = UINib(nibName: "OverlayView", bundle: bundle)
    let view = nib.instantiate(withOwner: self, options: nil)[0] as! UIView
    
    view.frame = bounds
    view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    addSubview(view)
    
    return self
    
  }
  
  
    
  var isRunning: Bool {
      return captureSession?.isRunning ?? false
  }
  
  func startScanning() {
     captureSession?.startRunning()
     delayReceiveQR()
  }
  
  func stopScanning() {
      captureSession?.stopRunning()
      delegate?.qrScanningDidStop()
  }
  
  /// Does the initial setup for captureSession
  private func doInitialSetup() {
    setUpOverlay()

    // tính toán lại vị trí và kích thước của các view overlay
    // lý do: ko kéo thả để tự scale theo màn hình được 😔
    viewTop.frame.size.height = (frame.size.height - QRImageSize) / 2
    
    viewBottom.frame.size.height = (frame.size.height - QRImageSize) / 2
    viewBottom.frame.origin.y = viewTop.frame.size.height + QRImageSize
    
    viewRight.frame.size.width = (frame.size.width - QRImageSize) / 2
    viewRight.frame.size.width = (frame.size.width - QRImageSize) / 2
    
    viewLeft.frame.size.width = (frame.size.width - QRImageSize) / 2
    
    
    self.clipsToBounds = true
    captureSession = AVCaptureSession()
    
    guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else { return }
    let videoInput: AVCaptureDeviceInput
    do {
        videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
    } catch let error {
        print(error)
        return
    }
    
    if (captureSession?.canAddInput(videoInput) ?? false) {
        captureSession?.addInput(videoInput)
    } else {
        scanningDidFail()
        return
    }
    
    let metadataOutput = AVCaptureMetadataOutput()
    
    if (captureSession?.canAddOutput(metadataOutput) ?? false) {
        captureSession?.addOutput(metadataOutput)
        
        metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
        metadataOutput.metadataObjectTypes = [.qr]
    } else {
        scanningDidFail()
        return
    }
    
    self.layer.session = captureSession
    self.layer.videoGravity = .resizeAspectFill
    
    captureSession?.startRunning()
  }
  func scanningDidFail() {
//      delegate?.qrScanningDidFail()
      captureSession = nil
  }
  
  func found(code: String, rect: CGRect) {
    
    if(!canReceiveQRCode) {
      return
    }
    
    canReceiveQRCode = false
    
    if(!checkOvelayConstainQRcode(rect: rect)) {
      canReceiveQRCode = true
      return
    }
    
    if(onQRCodeRead != nil) {
      onQRCodeRead!(["code": code, "ios": ["x": rect.origin.x, "y": rect.origin.y, "width": rect.size.width, "height": rect.size.height]])
    }
    
    if(enableDelay) {
      delayReceiveQR()
    }
  }
  
  func checkOvelayConstainQRcode(rect: CGRect) -> Bool {
    // nếu ko sử dụng overlay => mặc định bỏ
    if(!enableOverlay) {
      return true
    }
    // tính toán vị trí của viewQr so với view của center
    // phải lấy width của viewLeft và height của viewTop làm x, y của viewCenter (bởi vì k thể lấy ra được x, y của viewCenter)
    if( rect.origin.x >= viewLeft.bounds.size.width
      && rect.origin.y >= viewTop.bounds.size.height
      && rect.size.width <= (viewCenter.bounds.size.width - (rect.origin.x - viewLeft.bounds.size.width))
      && rect.size.height <= (viewCenter.bounds.size.height - (rect.origin.y - viewTop.bounds.size.height))) {
      NSLog("checkOvelayConstainQRcode")
      return true
    }
    
    
    return false
  }
  
  func delayReceiveQR() {
    // delay sau 2s moi nhan qr tiep
    if(canReceiveQRCode) {
      canReceiveQRCode = false
    }
    DispatchQueue.main.asyncAfter(deadline: .now() + DispatchTimeInterval.milliseconds(delayReceive)) {
      self.canReceiveQRCode = true
    }
  }
  
  
    
}

extension QRScanView: AVCaptureMetadataOutputObjectsDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput,
                        didOutput metadataObjects: [AVMetadataObject],
                        from connection: AVCaptureConnection) {
        
        
        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
          
//          stopScanning()
          guard let transformed = layer.transformedMetadataObject(for: readableObject) else { return }
          
          
            guard let stringValue = readableObject.stringValue else { return }
//            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
          
          
          found(code: stringValue,rect: transformed.bounds)
        }
    }
    
}
