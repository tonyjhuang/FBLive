//
//  ChatViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 5/1/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import FirebaseFirestore
import CodableFirebase


protocol ChatViewControllerDelegate: NSObject {
  func didAddMessage(message: String)
}

class ChatViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
  @IBOutlet weak var tableView: UITableView!
  @IBOutlet weak var textField: UITextField!
  
  let db = Firestore.firestore()
  var chatRef: DocumentReference?
  
  @IBAction func sendMessageButtonTapped(_ sender: Any) {
    guard let message = textField.text else {
      return
    }
    addMessage(body: message)
    textField.text = ""
  }
  
  let colors: [UIColor] = [
    UIColor(red: 252.0/255.0, green: 100.0/255.0, blue: 113.0/255.0, alpha: 1.0),
    UIColor(red: 98.0/255.0, green: 98.0/255.0, blue: 98.0/255.0, alpha: 1.0),
    UIColor(red: 47.0/255.0, green: 186.0/255.0, blue: 160.0/255.0, alpha: 1.0),
  ]
  
  var chatId: String?
  var messages = [Message]()
  
  var pollTimer: Timer?
  
  weak var delegate: ChatViewControllerDelegate?
  
  func numberOfSections(in tableView: UITableView) -> Int {
    1
  }
  
  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return messages.count
  }
  
  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {    
    let message = messages[indexPath.row]
    let cell = tableView.dequeueReusableCell(withIdentifier: "message", for: indexPath)
    let color = colors[indexPath.row % 3]
    cell.textLabel?.attributedText = ChatViewController.attributedString(with: color, for: message)
    return cell
  }

    override func viewDidLoad() {
        super.viewDidLoad()
      tableView.delegate = self
      tableView.dataSource = self
      fetchChat(chatId: chatId!)
      
      pollTimer = Timer(timeInterval: 1.0, repeats: true, block: { (timer) in
        self.fetchChat(chatId: self.chatId!)
      })
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    pollTimer?.invalidate()
    pollTimer = nil
  }
  
  class func attributedString(with color: UIColor, for message: Message) -> NSAttributedString {
    let usernameAttributes: [NSAttributedString.Key: Any] = [
      .foregroundColor: color,
      .font: UIFont.boldSystemFont(ofSize: 16)
    ]
    let userNameString = "\(message.author_name): "
    let userNameAttrString = NSMutableAttributedString(string: userNameString,
                                                       attributes: usernameAttributes)
    
    let messageAttributes: [NSAttributedString.Key: Any] = [.font: UIFont.boldSystemFont(ofSize: 16)]
    let messageAttrString = NSAttributedString(string: message.body, attributes: messageAttributes)
    
    userNameAttrString.append(messageAttrString)
    
    return userNameAttrString
  }
  
  fileprivate func fetchChat(chatId:String) {
    db.collection("chats").document(chatId).collection("messages").order(by:"created_at",descending: false).getDocuments() { querySnapShot, err in
         if let err = err {
             print("ERROR: \(err)")
         } else {
          self.messages = [Message]()
          for message in querySnapShot!.documents {
            let message: Message = try! FirestoreDecoder().decode(Message.self, from:(message.data()))
            self.messages.append(message)
            print("HERE "+message.body)
          }
          
          DispatchQueue.main.async {
            self.tableView.reloadData()
            let indexPath = IndexPath(row: self.messages.count-1, section: 0)
            self.tableView.scrollToRow(at: indexPath, at: .bottom, animated: true)
          }
        }
     }
  }
  
  fileprivate func addMessage(body: String) {
    let message = Message(author_name: UserDefaults.standard.string(forKey: "name")!, author_photo_url: "", body: body,created_at: Timestamp.init())
    let docData = try! FirestoreEncoder().encode(message)
    db.collection("chats").document(self.chatId!).collection("messages").addDocument(data: docData) { (error) in
      if let error = error {
          print("ERROR: \(error)")
      } else {
        self.fetchChat(chatId: self.chatId!)
      }
    }
  }
  
  /*
   db.collection("streams").document((stream?.stream_id)!).collection("products").getDocuments() {(querySnapshot, err) in
       if let err = err {
           print("ERROR: \(err)")
       } else {
           self.productRef = querySnapshot?.documents[0].reference
           self.product = try! FirestoreDecoder().decode(Product.self, from:(querySnapshot?.documents[0].data())!)
           self.product?.product_id = querySnapshot?.documents[0].documentID
       }
       
   }
   */
}
