//
//  MUXNetworkManager.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/30/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

struct MuxLiveStreamMetadata {
  let id: String
  let streamKey: String
  let playbackId: String
}

class MUXNetworkManager {
  class var authString: String {
    get {
      let id = "ff535f88-063e-48ad-ab6d-15f0b77ed40f"
      let secret = "1PXsKHnqc2m9GhxI6rfex7Y0ssAR+Au9uE67s4rLtFCd3qn5Drx6xOX2iwuvbcUvDRuy+h10fYT"
      
      let authString = "\(id):\(secret)"
      guard let authData = authString.data(using: .ascii) else {
        assertionFailure()
        return ""
      }
      let authValue = authData.base64EncodedString()
      return "Basic \(authValue)"
    }
  }
  
  /// Creates a livestream, returning id/playbackID/streamKey or an error in the callback.
  class func createLiveStream(completion: @escaping (Error?, MuxLiveStreamMetadata?) -> Void) {
    let urlString = "https://api.mux.com/video/v1/live-streams"
    guard let url = URL(string: urlString) else {
      assertionFailure("Failed to create URL for MUX API")
      return
    }
    
    let dataDictionary: [String: Any] = ["playback_policy": "public",
                                         "new_asset_settings": ["playback_policy": "public"]]
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.setValue(self.authString, forHTTPHeaderField: "Authorization")
    guard let httpBody = try? JSONSerialization.data(withJSONObject: dataDictionary,
                                                     options: []) else {
      assertionFailure("Failed to serialize JSON body for creating MUX stream")
      return
    }
    request.httpBody = httpBody
    URLSession.shared.dataTask(with: request) { (data, response, error) in
      if error != nil {
        completion(error, nil)
        return
      }
      
      if let data = data {
        do {
          let json = try JSONSerialization.jsonObject(with: data, options: [])
          guard let result = json as? [String: AnyObject] else {
            assertionFailure("Response for MUX create live stream wasn't valid")
            return
          }
          
          guard let dataDictionary = result["data"] as? [String: AnyObject],
               let streamKey = dataDictionary["stream_key"] as? String,
               let id = dataDictionary["id"] as? String else {
            assertionFailure("Response for MUX create live stream didn't have a stream key")
            return
          }
          
          guard let playbackIds = dataDictionary["playback_ids"] as? [[String: String]],
            playbackIds.count >= 1 else {
            assertionFailure("Response for MUX create live stream didn't have playback ids")
            return
          }
          
          guard let firstId = playbackIds.first, let playbackId = firstId["id"] else {
            assertionFailure("Playback id dictionary was malformed")
            return
          }
          
          completion(nil, MuxLiveStreamMetadata(id: id,
                                                streamKey: streamKey,
                                                playbackId: playbackId))
        } catch {
          completion(error, nil)
        }
      }
    }.resume()
  }
}
