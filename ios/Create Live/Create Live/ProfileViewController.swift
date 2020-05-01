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

public  struct OrderModel: Codable{
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
    @IBOutlet weak var itemNameLabel: UILabel!
    
    let green = UIColor(red:47/255, green:186/255, blue: 160/255, alpha: 1.0)
    let grey = UIColor(red:98/255, green:98/255, blue: 98/255, alpha: 1.0)
    let light_grey = UIColor(red:221/255, green:221/255, blue: 221/255, alpha: 1.0)
    
    let db = Firestore.firestore()
    var itemsBought: [OrderDisplayModel] = []
    var itemsSold: [OrderModel] = []
    
    override func viewDidLoad() {
        fetchOrders("buyer")
        super.viewDidLoad()
        itemsBoughtTable.separatorStyle = .none
        itemsSoldTable.separatorStyle = .none
        profileImage.layer.borderWidth = 4
        profileImage.layer.masksToBounds = false
        profileImage.layer.borderColor = green.cgColor
        profileImage.layer.cornerRadius = profileImage.frame.height/2
        profileImage.clipsToBounds = true
        profileImage.downloaded(from: UserDefaults.standard.string(forKey: "photo_url")!)
        userNameLabel.text = UserDefaults.standard.string(forKey: "name")
        userNameLabel.textColor = grey
        userNameLabel.font = UIFont(name:"NotoSansKannada-Bold", size:CGFloat(25))
        
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
                                self.itemsBoughtTable.reloadData()
                            }
                            
                        }
                        
                    }
                    
                    
                    
                }
        }
        
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return itemsBought.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 150;//Choose your custom row height
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        let cell = itemsBoughtTable.dequeueReusableCell(withIdentifier: "ItemBoughtCell")
        
        
        let label = UILabel(frame: CGRect(x:10, y:10, width:200, height:40))
        label.text = itemsBought[indexPath.row].item_name
        label.textColor = green
        label.font = UIFont(name:"NotoSansKannada-Bold", size:CGFloat(20))
        
        let image = UIImageView(frame:CGRect(x:250, y:0, width:100, height:100))
        image.downloaded(from: itemsBought[indexPath.row].item_photo_url)
        
        let price = UILabel(frame: CGRect(x:10, y:45, width:100, height:20))
        price.text = "Price: $" + String(itemsBought[indexPath.row].order.price) + "0"
        price.textColor = grey
        price.font = UIFont(name:"NotoSansKannada-Light", size:CGFloat(16))
        
        let seller = UILabel(frame: CGRect(x:10, y:80, width:200, height:20))
        seller.text = "From: " + String(itemsBought[indexPath.row].order.seller_name)
        seller.textColor = grey
        seller.font = UIFont(name:"NotoSansKannada-Light", size:CGFloat(14))
        
        let purchase_time = UILabel(frame: CGRect(x:10, y:100, width:200, height:20))
        purchase_time.text = formatter.string(from: itemsBought[indexPath.row].order.created_at.dateValue())
        purchase_time.textColor = light_grey
        purchase_time.font = UIFont(name:"NotoSansKannada-Light", size:CGFloat(14))
        
        cell?.addSubview(label)
        cell?.addSubview(image)
        cell?.addSubview(price)
        cell?.addSubview(seller)
        cell?.addSubview(purchase_time)
        return cell!
    }
}
