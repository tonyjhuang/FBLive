//
//  WelcomeViewController.swift
//  Create Live
//
//  Created by Vivian Wu on 2020-04-29.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import AVFoundation

class WelcomeViewController: UIViewController {
    @IBOutlet weak var greetLabel: UILabel!
    
    @IBAction func startLiveStream(_ sender: UIButton) {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
            case .authorized: // The user has previously granted access to the camera.
                self.getMicrophonePermission()
            
            case .notDetermined: // The user has not yet been asked for camera access.
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        self.getMicrophonePermission()
                    }
                }
            
            case .denied: // The user has previously denied access.
                //Error handling.
                return

            case .restricted: // The user can't grant access due to restrictions.
                return
        }
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        greetLabel.text = UserDefaults.standard.string(forKey: "name")
    }
    
    func getMicrophonePermission() {
        switch AVCaptureDevice.authorizationStatus(for: .audio) {
            case .authorized: // The user has previously granted access to the camera.
                toStartStream()
            
            case .notDetermined: // The user has not yet been asked for camera access.
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        self.toStartStream()
                    }
                }
            
            case .denied: // The user has previously denied access.
                //Error handling.
                return

            case .restricted: // The user can't grant access due to restrictions.
                return
        }
    }
    
    func toStartStream() {
          let storyboard = UIStoryboard(name:"Main", bundle:nil)
          let startStreamVC = storyboard.instantiateViewController(identifier: "StartStreamViewController")
          startStreamVC.modalPresentationStyle = .overCurrentContext
          self.present(startStreamVC, animated: true, completion: nil)
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
