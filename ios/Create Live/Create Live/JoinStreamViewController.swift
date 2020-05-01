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

/*
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
*/

func fetchStream() {
    
}

class JoinStreamViewController: UIViewController, ChatViewControllerDelegate, OrderConfirmationViewControllerDelegate {
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
  
  @IBOutlet weak var productNameLabel: UILabel!
  @IBOutlet weak var productPriceLabel: UILabel!
  
  @IBOutlet weak var showChatButton: UIButton!
  @IBOutlet weak var chatView: UIView!
  
  @IBOutlet var streamControls: [UIView]!
  
  @IBOutlet weak var buttonsViewBottomAlignedWithChatWindow: NSLayoutConstraint!
  
  @IBOutlet weak var buttonsViewBottomAlignedWithShowChatButton: NSLayoutConstraint!
  
  @IBAction func shareButtonTapped(_ sender: Any) {}
  
  @IBAction func chatSlideUpButtonTapped(_ sender: Any) {
    showChat()
  }
  
  func updateLayoutWithAnimation() {
    let options: UIView.AnimationOptions = [.curveEaseInOut]
    
    UIView.animate(withDuration: 0.7,
                   delay: 0,
                   options: options,
                   animations: { [weak self] in
                    self?.view.setNeedsLayout()
    }, completion: nil)
  }
  
  fileprivate func showChat() {
    buttonsViewBottomAlignedWithChatWindow.isActive = true
    buttonsViewBottomAlignedWithShowChatButton.isActive = false
    showChatButton.isHidden = true
    chatView.isHidden = false
    updateLayoutWithAnimation()
  }
  
  fileprivate func hideChat() {
    buttonsViewBottomAlignedWithChatWindow.isActive = false
    buttonsViewBottomAlignedWithShowChatButton.isActive = true
    showChatButton.isHidden = false
    chatView.isHidden = true
    updateLayoutWithAnimation()
  }
  
  @IBAction func emojiButtonTapped(_ sender: UIButton) {
    guard let emoji = sender.title(for: .normal) else {
      return
    }
    
    // TODO: Emoji parade
  }
  
  override func viewDidLoad() {
      super.viewDidLoad()
      
      configureStreamControls()
      addGradient()
      playLiveStream()
    }
  
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if segue.identifier == "embedChatView",
      let destination = segue.destination as? ChatViewController {
      destination.chatId = "O2TFvXlolO9ILtoDKJlN"
      destination.delegate = self
    } else if segue.identifier == "showOrderConfirmation", let destination = segue.destination as? OrderConfirmationViewController {
      destination.product = stream.products.first!
      destination.delegate = self
    }
  }
  
  func didAddMessage(message: String) {
    // TODO add message
    hideChat()
  }
  
  func didBuy(_ product: Product) {
    // TODO emoji parade
  }
  
  fileprivate func configureStreamControls() {
    streamTitleLabe.text = stream.name
    streamUserIDLabel.text = stream.creator.name
    thumbnailImage.layer.cornerRadius = 18
    
    if let product = stream.products.first {
      
      let nf = NumberFormatter()
      nf.maximumFractionDigits = 2
      guard let priceString = nf.string(from: product.price) else {
        return
      }
      productNameLabel.text = product.name
      productPriceLabel.text = "$\(priceString + "0")"
    }
  }
  
  fileprivate func addGradient() {
    let topGradient = CAGradientLayer()
    topGradient.frame = CGRect(x: 0, y: 0, width: view.bounds.width, height: 140)
    topGradient.colors = [UIColor.black.withAlphaComponent(0.35).cgColor, UIColor.white.withAlphaComponent(0.35).cgColor]
    view.layer.addSublayer(topGradient)
    
    let bottomGradient = CAGradientLayer()
    bottomGradient.frame = CGRect(x: 0,
                                  y: view.bounds.height - 170,
                                  width: view.bounds.width,
                                  height: 170)
    bottomGradient.colors = [UIColor.white.withAlphaComponent(0.35).cgColor, UIColor.black.withAlphaComponent(0.35).cgColor]
    view.layer.addSublayer(bottomGradient)
    
    bringStreamControlsToFront()
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

    view.layer.insertSublayer(playerLayer, at: 0)

    player.play()
    
    bringStreamControlsToFront()
  }
  
  func bringStreamControlsToFront() {
    for view in streamControls {
      self.view.bringSubviewToFront(view)
    }
  }
/*
    fileprivate func fetchAllStreams() {
        db.collection("streams").getDocuments() { (querySnapshot, err) in
            if let err = err {
                print("ERROR: \(err)")
            } else {
                if querySnapshot?.count == 0 {
                    print("Stream not found")
                } else {
                    for stream in querySnapshot!.documents {
                        var s:Stream = try! FirestoreDecoder().decode(Stream.self, from:(stream.data()))
                        s.stream_id = querySnapshot?.documents[0].documentID
                        print(self.stream)
                        self.allStreams.append(s)
                    }
                }
            }
        }
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
 */
}
