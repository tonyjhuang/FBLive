//
//  Data.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/30/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit
import CodableFirebase
import FirebaseFirestore
struct FakeStream {
  let id: String
  let createdAt: Date
  let creator: FakeUser
  let creatorName: String
  let endedAt: Date
  let name: String
  let url: String
  let chat: Chat
  let products: [Product]
}

struct FakeUser {
  let id: String
  let name: String
  let imageURL: String
}

struct Chat {
  let id: String
  let messages: [Message]
}

struct Message: Codable {
  let author_name: String
  let author_photo_url: String
  let body: String
  let created_at: Timestamp
}

struct Product {
  let id: String
  let name: String
  let photoURL: String
  let price: NSDecimalNumber
  let quantity: Int
  let remaining: Int
}

class DataProvider {
  class func someProducts() -> [Product] {
    return [
      Product(id: "fake_id",
              name: "CD",
              photoURL: "https://upload.wikimedia.org/wikipedia/commons/1/1d/Yandhi_Cover_Art_%28Free_License%29.jpg", price: 8.50,
              quantity: 5,
              remaining: 5),
      Product(id: "fake_id",
      name: "Bread turnstiles",
      photoURL: "https://upload.wikimedia.org/wikipedia/commons/1/1d/Yandhi_Cover_Art_%28Free_License%29.jpg", price: 12.99,
      quantity: 5,
      remaining: 5)
    ]
  }
  
  class func aChat() -> Chat {
    return Chat(id: "fake_d", messages: [
        Message(author_name: "fake_id", author_photo_url: "fake_url", body: "OK",created_at: Timestamp.init()),
      Message(author_name: "fake_id", author_photo_url: "fake_url", body: "OK",created_at: Timestamp.init()),
      Message(author_name: "fake_id", author_photo_url: "fake_url", body: "OK",created_at: Timestamp.init())
    ])
  }
  
  class func aUser() -> FakeUser {
    return FakeUser(id: "fake_id",
                    name: "Ed Urchin",
                    imageURL: "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg")
  }
  
  class func bUser() -> FakeUser {
    return FakeUser(id: "fake_id",
                    name: "Johnny Purpose",
                    imageURL: "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg")
  }
  
  class func cUser() -> FakeUser {
    return FakeUser(id: "fake_id",
                    name: "Tony Huang",
                    imageURL: "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg")
  }
  
  class func aStream() -> FakeStream {
    return FakeStream(id: "fake_id",
                  createdAt: Date(),
                  creator: cUser(),
                  creatorName: "Tony Huang",
                  endedAt: Date().addingTimeInterval(600),
                  name: "Bad Guy (cover)",
                  url: "https://stream.mux.com/v1TbvVV23L7ygUay3OtSCq02PScCzn7spXyzI94hAx6Q.m3u8",
                  chat: aChat(),
                  products: someProducts())
  }
}
