//
//  JoinStreamViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/29/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

class JoinStreamViewController: UIViewController {

  @IBOutlet weak var streamTitleLabe: UILabel!
  @IBOutlet weak var numberOfViewersLabel: UILabel!
  @IBOutlet weak var thumbnailImage: UIImageView!
  
    override func viewDidLoad() {
        super.viewDidLoad()

      streamTitleLabe.text = "My stream"
    }

}
