//
//  JoinStreamViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/29/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import AVKit

class JoinStreamViewController: UIViewController {

  var streamPlaybackURL: URL?
  
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

}
