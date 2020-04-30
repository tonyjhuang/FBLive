//
//  WatchingViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/30/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import AVKit

class WatchingViewController: UIViewController {

  let url = URL(string: "https://stream.mux.com/MMXs7IYPiffKIxiWHG02Un00aIo29UYtYQW6itPW58mvI.m3u8")
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    guard let url = url else {
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
