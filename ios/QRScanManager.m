//
//  QRScanManager.m
//  RNQRCode
//
//  Created by HungDV on 7/2/20.
//

#if __has_include(<React/RCTViewManager.h>)
#import <React/RCTViewManager.h>
#elif __has_include(“RCTViewManager.h”)
#import “RCTViewManager.h”
#else
#import “React/RCTViewManager.h” // Required when used as a Pod in a Swift project
#endif


#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(QRScan, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(enableOverlay, BOOL)
RCT_EXPORT_VIEW_PROPERTY(enableDelay, BOOL)
RCT_EXPORT_VIEW_PROPERTY(delayReceive, NSInteger)
RCT_EXPORT_VIEW_PROPERTY(onQRCodeRead, RCTDirectEventBlock)

RCT_EXTERN_METHOD(stopPreview: (nonnull NSNumber *) node)
RCT_EXTERN_METHOD(resumePreview: (nonnull NSNumber *) node)
@end
