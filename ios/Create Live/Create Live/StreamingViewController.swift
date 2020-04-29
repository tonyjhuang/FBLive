//
//  StreamingViewController.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/28/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

class StreamingViewController: UIViewController {

  override func viewDidLoad() {
    super.viewDidLoad()
    // createMUXStream()
  }
  
  fileprivate func createMUXStream() {
    MUXNetworkManager.createLiveStream { (error, streamMetadata) in
      if error != nil {
        self.displayError(errorString: error!.localizedDescription) {
          self.createMUXStream()
        }
      } else {
        guard let streamMetadata = streamMetadata else {
          assertionFailure()
          return
        }
        // TODO: Do something with the metadata.
        
      }
    }
  }
  
  fileprivate func displayError(errorString: String, tryAgainAction: (() -> Void)?) {
    let alert = UIAlertController(title: "Error",
                                  message: errorString,
                                  preferredStyle: .alert)
    if tryAgainAction != nil {
      alert.addAction(UIAlertAction(title: "Try again",
                                    style: .default,
                                    handler: { (action) in
        tryAgainAction!()
      }))
    }
    alert.addAction(UIAlertAction(title: "Dismiss", style: .default, handler: nil))
  }
}
