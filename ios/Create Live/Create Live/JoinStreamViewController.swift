//
//  JoinStreamViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/29/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import AVKit
import CodableFirebase
import FirebaseFirestore

extension Timestamp: TimestampType {}
extension DocumentReference: DocumentReferenceType {}



public struct Stream: Decodable{
    let chat: DocumentReference?
    let created_at: Timestamp
    let creator: DocumentReference
    let creator_name: String
    let name: String
    let stream_url: String
}


func fetchStream() {
    
}

class JoinStreamViewController: UIViewController {

  var streamPlaybackURL: URL?
  let db = Firestore.firestore()
  var stream:Stream?
  @IBOutlet weak var streamTitleLabe: UILabel!
  @IBOutlet weak var numberOfViewersLabel: UILabel!
  @IBOutlet weak var thumbnailImage: UIImageView!
  
    override func viewDidLoad() {
        super.viewDidLoad()
        
      streamTitleLabe.text = "My stream"
    }
  
  /// Adds the livestream view on this entire screen and starts the livestream
  fileprivate func playLiveStream() {
    guard let url = streamPlaybackURL else {
      assertionFailure()
      return
    }
    
    let asset = AVAsset(url: url)
    let playerItem = AVPlayerItem(asset: asset)
    let player = AVPlayer(playerItem: playerItem)
    
    let playerLayer = AVPlayerLayer(player: player)
    playerLayer.frame = view.bounds
    playerLayer.videoGravity = .resizeAspectFill
    
    view.layer.addSublayer(playerLayer)
    player.play()
  }

    
    fileprivate func fetchStream(_ name:String) {
        db.collection("streams").whereField("name", isEqualTo: name).getDocuments() { (querySnapshot, err) in
            if let err = err {
                print("ERROR: \(err)")
            } else {
                if querySnapshot?.count == 0 {
                    print("Stream not found")
                } else {
                    self.stream = try! FirestoreDecoder().decode(Stream.self, from:querySnapshot?.documents[0].data() ?? <#default value#>)
                    
                }
            }
        }
    }

}
