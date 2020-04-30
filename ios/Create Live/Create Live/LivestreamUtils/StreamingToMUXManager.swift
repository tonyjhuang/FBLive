//
//  StreamingToMUXManager.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/30/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import StreamaxiaSDK

/// Starts streaming to the MUX stream with the given streamkey. Shows the preview in the given view. Returns recorder object in a
/// completion handler.
class StreamingToMUXManager {
  class func startStreaming(with streamKey: String,
                            view: UIView,
                            completion:@escaping (AXRecorder?, AXError?) -> Void) {
    let info = AXStreamInfo.init()
    
    info.useSecureConnection = false
    info.customStreamURLString = "rtmp://global-live.mux.com:5222/app/\(streamKey)"
    
    let recordSettings = AXRecorderSettings.init()
    
    if let recorder = AXRecorder.init(streamInfo: info, settings: recordSettings) {
      recorder.setup(with: view)
      recorder.prepareToRecord()
      
      var error: AXError?
      
      recorder.activateFeatureAdaptiveBitRateWithError(&error)
      if error != nil {
        print("LIVLIVLIV adaptive bitrate failed.")
      }
      
      recorder.startStreaming { (success, error) in
        if error != nil {
          assertionFailure()
          completion(nil, error)
        }
        completion(recorder, nil)
      }
    }
    completion(nil, AXError())
  }
}
