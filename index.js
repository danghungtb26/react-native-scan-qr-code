import React, { Component } from 'react'
import { requireNativeComponent, NativeModules, findNodeHandle, ViewProps } from 'react-native'

interface IProps extends ViewProps {
  delayReceive: Number;
  enableOverlay: Boolean;
  enableDelay: Boolean;
}

export default class QRView extends Component<IProps> {
  camera = React.createRef()

  stopPreview = () => {
    const { stopPreview } = NativeModules.QRScan
    if (stopPreview) {
      stopPreview(findNodeHandle(this.camera.current))
    }
  }

  resumePreview = () => {
    const { resumePreview } = NativeModules.QRScan
    if (resumePreview) {
      resumePreview(findNodeHandle(this.camera.current))
    }
  }

  render() {
    return <QR ref={this.camera} {...this.props} />
  }
}

const QR = requireNativeComponent('QRScan', QRView)
