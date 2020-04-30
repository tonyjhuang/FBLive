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
    var stream_id: String?
}

public struct Product: Decodable{
    let name: String
    let photo_url: String
    let price: Float
    let quantity: Int
    let remaining: Int
    var product_id: String?
}


func fetchStream() {
    
}

class JoinStreamViewController: UIViewController {

  var streamPlaybackURL: URL?
  let db = Firestore.firestore()
  var stream:Stream?
  var product:Product?
  var productRef:DocumentReference?
    
  @IBOutlet weak var streamTitleLabe: UILabel!
  @IBOutlet weak var numberOfViewersLabel: UILabel!
  @IBOutlet weak var thumbnailImage: UIImageView!
  
    override func viewDidLoad() {
        super.viewDidLoad()
        fetchStream("fb live")
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
                    self.stream = try! FirestoreDecoder().decode(Stream.self, from:(querySnapshot?.documents[0].data())!)
                    self.stream?.stream_id = querySnapshot?.documents[0].documentID
                    print(self.stream)
                    self.fetchProduct()
                }
            }
        }
    }
    
    //
    fileprivate func fetchProduct() {
        db.collection("streams").document((stream?.stream_id)!).collection("products").getDocuments() {(querySnapshot, err) in
            if let err = err {
                print("ERROR: \(err)")
            } else {
                self.productRef = querySnapshot?.documents[0].reference
                self.product = try! FirestoreDecoder().decode(Product.self, from:(querySnapshot?.documents[0].data())!)
                self.product?.product_id = querySnapshot?.documents[0].documentID
            }
            
        }
    }
    
    fileprivate func createOrder() {
        let userRef = Firestore.firestore().collection("users").document(UserDefaults.standard.string(forKey:"uid")!)
        let order: OrderModel = OrderModel(price:(product?.price)!, buyer:userRef,seller_name:stream!.creator_name,product:productRef!,created_at: Timestamp.init())
        let docData = try! FirestoreEncoder().encode(order)
        Firestore.firestore().collection("orders").addDocument(data: docData) { err in
            if let err = err {
                print("ERROR: \(err)")
            } else {
                print("Create order success.")
            }
        }

    }
    
    

}
