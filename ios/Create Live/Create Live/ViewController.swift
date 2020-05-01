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
import FirebaseFirestore
import FirebaseAuth
import Firebase

class ViewController: UIViewController , GIDSignInDelegate{
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        let GREEN = UIColor(red:47/255, green:186/255, blue: 160/255, alpha: 1.0)
        
        let title = UILabel(frame: CGRect(x:10, y:45, width:300, height:100))
        title.center = self.view.center
        title.center.y = 400
        title.text = "Firebase Live"
        title.textColor = GREEN

        title.font = UIFont(name:"NotoSansKannada-Bold", size:CGFloat(35))
        title.textAlignment = .center
//        title.sizeToFit()
        view.addSubview(title)
        
        let signInButton = GIDSignInButton()
        signInButton.center.x = self.view.center.x
        signInButton.center.y = 500
        view.addSubview(signInButton)
        // Set up Google sign in.
        GIDSignIn.sharedInstance().clientID = FirebaseApp.app()?.options.clientID
        GIDSignIn.sharedInstance().delegate = self
        GIDSignIn.sharedInstance()?.presentingViewController = self
    }
    
    
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error?) {
        let db = Firestore.firestore()
        if let error = error {
            print("Sign in failure: ",error)
            return
        }
        // Get credentials from Google
        guard let authentication = user.authentication else { return }
        let credential = GoogleAuthProvider.credential(withIDToken: authentication.idToken,
                                                       accessToken: authentication.accessToken)
        // Use Google credential to authenticate with Firebase
        Auth.auth().signIn(with: credential) { (authResult, error) in
            if let error = error {
                print(error.localizedDescription)
                return
            }
            if let user = authResult?.user {
                UserDefaults.standard.set(user.uid, forKey:"uid")
                UserDefaults.standard.set(user.displayName, forKey:"name")
                UserDefaults.standard.set(user.photoURL?.absoluteString, forKey:"photo_url")
                UserDefaults.standard.synchronize()
                self.lookupAndCreateUser(user,db)
            } else {
                //Error handling.
            }
            
            return
            
        }
        
    }
    
    func sign(_ signIn: GIDSignIn!, didDisconnectWith user: GIDGoogleUser!, withError error: Error!) {
        // Perform any operations when the user disconnects from app here.
        // ...
    }
    
    
    func lookupAndCreateUser(_ user: User, _ db: Firestore) {
         
        db.collection("users").whereField("email", isEqualTo: user.email)
             .getDocuments() { (querySnapshot, err) in
                 if let err = err {
                     print("HERE \(err)")
                 } else {
                     if querySnapshot!.documents.count == 0 {
                        self.createUser(user, db) { () in
                            print("HERE")
                            self.toWelcomePage()
                        }
                     } else {
                        self.toWelcomePage()
                    }
                 }
             
             
         }
     }
     
    func createUser(_ user: User, _ db: Firestore, onSucess closure:()->Void) {
        db.collection("users").document(user.uid).setData([
            "email": user.email ?? "",
            "name": user.displayName ?? "",
            "photo_url": user.photoURL?.absoluteString ?? "",
         ]) { err in
                 if let err = err {
                     print("Error creating user: \(err)")
                 } else {
                     print("Uploaded.")
                 }
         }
     }
     
     func toWelcomePage() {
        let storyboard = UIStoryboard(name:"Main", bundle:nil)
        let welcomeVC = storyboard.instantiateViewController(identifier: "WelcomeViewController")
//        let navigationContonroller = UINavigationController(rootViewController: welcomeVC)
        self.view.window!.rootViewController = welcomeVC
     }
    
    
}

