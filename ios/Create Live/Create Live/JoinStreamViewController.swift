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

/*
  var streamPlaybackURL: URL?
  let db = Firestore.firestore()
  var stream:Stream?
*/
  
  var stream = DataProvider.aStream()
  
  @IBOutlet weak var streamTitleLabe: UILabel!
  @IBOutlet weak var streamUserIDLabel: UILabel!
  @IBOutlet weak var numberOfViewersLabel: UILabel!
  @IBOutlet weak var thumbnailImage: UIImageView!
  
  @IBAction func shareButtonTapped(_ sender: Any) {}
  
  @IBAction func chatSlideUpButtonTapped(_ sender: Any) {
    // TODO: show chat
  }
  
  @IBAction func emojiButtonTapped(_ sender: UIButton) {
    guard let emoji = sender.title(for: .normal) else {
      return
    }
    
    // TODO: Emoji parade
  }
  
  
    override func viewDidLoad() {
        super.viewDidLoad()
        
      streamTitleLabe.text = stream.name
      // TODO: Fill out other labels
    }
  
  /// Adds the livestream view on this entire screen and starts the livestream
  fileprivate func playLiveStream() {
    guard let url = URL(string: stream.url) else {
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

    /*
    fileprivate func fetchStream(_ name:String) {
        db.collection("streams").whereField("name", isEqualTo: name).getDocuments() { (querySnapshot, err) in
            if let err = err {
                print("ERROR: \(err)")
            } else {
                if querySnapshot?.count == 0 {
                    print("Stream not found")
                } else {
                  self.stream = try! FirestoreDecoder().decode(Stream.self, from:querySnapshot?.documents[0].data() ?? [String: Any]())
                    
                }
            }
        }
    }*/

}
