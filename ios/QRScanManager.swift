//
//  QRScanManager.swift
//  RNQRCode
//
//  Created by HungDV on 7/2/20.
//

import Foundation
import UIKit
//import QRScanView

@objc(QRScan)
class QRScanManager : RCTViewManager {
  // Return the native view that represents your React component
  override func view() -> UIView! {
    return QRScanView()
  }

  
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
//  
  @objc func stopPreview(_ node: NSNumber) {
    DispatchQueue.main.async {
      let view = self.bridge.uiManager.view(forReactTag: node) as! QRScanView
      view.stopScanning()
    }
  }
  
  @objc func resumePreview(_ node: NSNumber) {
    DispatchQueue.main.async {
      let view = self.bridge.uiManager.view(forReactTag: node) as! QRScanView
      view.startScanning()
    }
  }

}
