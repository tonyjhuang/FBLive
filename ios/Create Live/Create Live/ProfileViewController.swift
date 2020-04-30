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

extension Timestamp: TimestampType {}
extension DocumentReference: DocumentReferenceType {}

public  struct OrderModel: Decodable{
    let price: Float
    let buyer: DocumentReference
    let seller_name: String
    let product: DocumentReference
//    let itemName: String
//    let item_photo_url: String
    let created_at: Timestamp
    
}

public struct OrderDisplayModel{
    let order: OrderModel
    let item_name: String
    let item_photo_url: String
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
    var itemsBought: [OrderDisplayModel] = []
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
        let userRef = Firestore.firestore().collection("users").document(UserDefaults.standard.string(forKey:"uid")!)
        db.collection("orders").whereField(field, isEqualTo: userRef)
            .getDocuments() { (querySnapshot, err) in
                if let err = err {
                    print("HERE \(err)")
                } else {
                    for order in querySnapshot!.documents {
                        let model:OrderModel = try! FirestoreDecoder().decode(OrderModel.self, from:order.data())
                        model.product.getDocument() { (querySnapshot, err) in
                            if let err = err {
                                
                            } else {
                                let item_name:String = querySnapshot?.get("name") as! String
                                let item_url:String = querySnapshot?.get("photo_url") as! String
                                var orderDisplayModel: OrderDisplayModel = OrderDisplayModel(order:model,item_name:item_name ,item_photo_url:item_url)
                                self.itemsBought.append(orderDisplayModel)
                                print(orderDisplayModel)
                            }
                            
                        }
                        
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
