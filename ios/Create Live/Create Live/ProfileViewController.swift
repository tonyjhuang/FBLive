//
//  ProfileViewController.swift
//  Create Live
//
//  Created by Vivian Wu on 2020-04-30.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import FirebaseFirestore

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
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        <#code#>
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        <#code#>
    }
    
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var itemsBoughtTable: UITableView!
    @IBOutlet weak var itemsSoldTable: UITableView!
    let db = Firestore.firestore()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        profileImage.downloaded(from: UserDefaults.standard.string(forKey: "photo_url")!)
        userNameLabel.text = UserDefaults.standard.string(forKey: "name")
        // Do any additional setup after loading the view.
    }
    
    func fetchOrders(_ field: String) {
        db.collection("orders").whereField(field, isEqualTo: UserDefaults.standard.string(forKey:"uid"))
             .getDocuments() { (querySnapshot, err) in
                 if let err = err {
                     print("HERE \(err)")
                 } else {
                     if querySnapshot!.documents.count == 0 {
                        }
                     } else {
                    }
                 }git 
             
             
         }
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

