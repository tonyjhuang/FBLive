//
//  ViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/27/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import FirebaseCrashlytics
import GoogleSignIn

class ViewController: UIViewController {

  override func viewDidLoad() {
    super.viewDidLoad()
    // Do any additional setup after loading the view.
    let signInButton = GIDSignInButton()
    signInButton.frame = CGRect(x: 20, y: 50, width: 100, height: 30)
    view.addSubview(signInButton)
    
    GIDSignIn.sharedInstance()?.presentingViewController = self
  }


}

