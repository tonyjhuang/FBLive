//
//  OrderConfirmationViewController.swift
//  Create Live
//
//  Created by Vivian Wu on 2020-05-01.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

protocol OrderConfirmationViewControllerDelegate: NSObject {
  func didBuy(_ product: Product)
}

class OrderConfirmationViewController: UIViewController {
    @IBOutlet weak var buyButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var productNameLabel: UILabel!
    @IBOutlet weak var productPriceLabel: UILabel!
    
  weak var delegate: OrderConfirmationViewControllerDelegate?
  
    @IBAction func buyButtonOnPress(_ sender: Any) {
      self.dismiss(animated: true) {
        self.delegate?.didBuy(self.product)
      }
    }
  
    @IBAction func cancelButtonOnPress(_ sender: Any) {
      self.dismiss(animated: true, completion: nil)
    }
    
  var product = DataProvider.someProducts().first!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        buyButton.layer.cornerRadius = 10
        buyButton.clipsToBounds = true
        cancelButton.layer.cornerRadius = 10
        cancelButton.clipsToBounds = true
      
      productNameLabel.text = product.name
      
      let nf = NumberFormatter()
      nf.maximumFractionDigits = 2
      guard let priceString = nf.string(from: product.price) else {
        return
      }
      productPriceLabel.text = "$\(priceString)0"
    }
    
    
}
