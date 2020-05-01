//
//  ChatViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 5/1/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

protocol ChatViewControllerDelegate {
  func didAddMessage(message: String)
}

class ChatViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
  @IBOutlet weak var tableView: UITableView!
  @IBOutlet weak var textField: UITextField!
  
  @IBAction func sendMessageButtonTapped(_ sender: Any) {
    guard let message = textField.text else {
      return
    }
    delegate?.didAddMessage(message: message)
  }
  
  let colors: [UIColor] = [
    UIColor(red: 252.0/255.0, green: 100.0/255.0, blue: 113.0/255.0, alpha: 1.0),
    UIColor(red: 98.0/255.0, green: 98.0/255.0, blue: 98.0/255.0, alpha: 1.0),
    UIColor(red: 47.0/255.0, green: 186.0/255.0, blue: 160.0/255.0, alpha: 1.0),
  ]
  
  var chat = DataProvider.aChat()
  var delegate: ChatViewControllerDelegate?
  
  func numberOfSections(in tableView: UITableView) -> Int {
    1
  }
  
  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    chat.messages.count
  }
  
  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let message = chat.messages[indexPath.row]
    let cell = tableView.dequeueReusableCell(withIdentifier: "message", for: indexPath)
    let color = colors[indexPath.row % 3]
    cell.textLabel?.attributedText = ChatViewController.attributedString(with: color, for: message)
    return cell
  }

    override func viewDidLoad() {
      super.viewDidLoad()
      tableView.reloadData()
  }
  
  class func attributedString(with color: UIColor, for message: Message) -> NSAttributedString {
    let usernameAttributes: [NSAttributedString.Key: Any] = [
      .foregroundColor: color,
      .font: UIFont.boldSystemFont(ofSize: 16)
    ]
    let userNameString = "\(message.user.name): "
    let userNameAttrString = NSMutableAttributedString(string: userNameString,
                                                       attributes: usernameAttributes)
    
    let messageAttributes: [NSAttributedString.Key: Any] = [.font: UIFont.boldSystemFont(ofSize: 16)]
    let messageAttrString = NSAttributedString(string: message.body, attributes: messageAttributes)
    
    userNameAttrString.append(messageAttrString)
    
    return userNameAttrString
  }

}
