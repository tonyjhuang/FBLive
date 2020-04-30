//
//  ProfileViewController.swift
//  Create Live
//
//  Created by Vivian Wu on 2020-04-30.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import FirebaseFirestore
import CodableFirebase

public  struct OrderModel {
    let price: Float
    let user: String
    let itemName: String
    let item_photo_url: String
    let purchaseTime: Date
    
}

public extension UIImageView {
    func downloaded(from url: URL, contentMode mode: UIView.ContentMode = .scaleAspectFit) {  // for swift 4.2 syntax just use ===> mode: UIView.ContentMode
        contentMode = mode
        URLSession.shared.dataTask(with: url) { data, response, error in
            guard
                let httpURLResponse = response as? HTTPURLResponse, httpURLResponse.statusCode == 200,
                let mimeType = response?.mimeType, mimeType.hasPrefix("image"),
                let data = data, error == nil,
                let image = UIImage(data: data)
                else { return }
            DispatchQueue.main.async() {
                self.image = image
            }
        }.resume()
    }
    func downloaded(from link: String, contentMode mode: UIView.ContentMode = .scaleAspectFit) {  // for swift 4.2 syntax just use ===> mode: UIView.ContentMode
        guard let url = URL(string: link) else { return }
        downloaded(from: url, contentMode: mode)
    }
}
class ProfileViewController: UIViewController, UITableViewDelegate, UITableViewDataSource{
    
    
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var itemsBoughtTable: UITableView!
    @IBOutlet weak var itemsSoldTable: UITableView!
    
    let db = Firestore.firestore()
    var itemsBought: [OrderModel] = []
    var itemsSold: [OrderModel] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        profileImage.downloaded(from: UserDefaults.standard.string(forKey: "photo_url")!)
        userNameLabel.text = UserDefaults.standard.string(forKey: "name")
        fetchOrders("buyer")
        // Do any additional setup after loading the view.
    }
    
    // field is either buyer or seller depending on which table to populate
    func fetchOrders(_ field: String) {
        db.collection("orders").whereField(field, isEqualTo: UserDefaults.standard.string(forKey:"uid")!)
            .getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("HERE \(err)")
                } else {
                    
                    for order in querySnapshot!.documents {
                        print(order.data())
                    }
                    
                    
                    
                }
        }
        
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = itemsBoughtTable.dequeueReusableCell(withIdentifier: "ItemBoughtCell")
        cell?.textLabel?.text = "HI"
        return cell!
    }
}
