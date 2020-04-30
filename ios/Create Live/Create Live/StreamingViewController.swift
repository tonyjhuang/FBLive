//
//  StreamingViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/28/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import StreamaxiaSDK

class StreamingViewController: UIViewController {

  var streamia: AXStreamaxiaSDK!
  
  override func viewDidLoad() {
    super.viewDidLoad()
    createMUXStream()
    
    // streamia = AXStreamaxiaSDK.sharedInstance()
    
    // streamia.setupSDK { (success, error) in
      // self.streamia.debugPrintStatus()
    // }
  }
  
  fileprivate func startRecording(with muxMetadata: MuxLiveStreamMetadata) {
    let info = AXStreamInfo.init()
    
    info.useSecureConnection = false
    info.customStreamURLString = "rtmp://global-live.mux.com:5222/app/\(muxMetadata.streamKey)"
    
    let recordSettings = AXRecorderSettings.init()
    
    if let recorder = AXRecorder.init(streamInfo: info, settings: recordSettings) {
      recorder.setup(with: self.view)
      recorder.prepareToRecord()
      
      var error: AXError?
      
      recorder.activateFeatureAdaptiveBitRateWithError(&error)
      if error != nil {
        print("LIVLIVLIV adaptive bitrate failed.")
      }
      
      recorder.startStreaming { (success, error) in
        if error != nil {
          self.displayError(errorString: error!.debugDescription, tryAgainAction: nil)
        }
      }
    }
    self.displayError(errorString: "Something went wrong", tryAgainAction: nil)
  }
  
  fileprivate func createMUXStream() {
    MUXNetworkManager.createLiveStream { (error, streamMetadata) in
      if error != nil {
        self.displayError(errorString: error!.localizedDescription) {
          self.createMUXStream()
        }
      } else {
        guard let streamMetadata = streamMetadata else {
          assertionFailure()
          return
        }
        // self.startRecording(with: streamMetadata)
      }
    }
  }
  
  fileprivate func displayError(errorString: String, tryAgainAction: (() -> Void)?) {
    let alert = UIAlertController(title: "Error",
                                  message: errorString,
                                  preferredStyle: .alert)
    if tryAgainAction != nil {
      alert.addAction(UIAlertAction(title: "Try again",
                                    style: .default,
                                    handler: { (action) in
        tryAgainAction!()
      }))
    }
    alert.addAction(UIAlertAction(title: "Dismiss", style: .default, handler: nil))
  }
}
