//
//  Data.swift
//  Create Live
//
//  Created by Chris Tibbs on 4/30/20.
//  Copyright © 2020 FB Live. All rights reserved.
//

import UIKit

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

struct Message {
  let id: String
  let user: FakeUser
  let body: String
  let createdAt: Date
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
              name: "Fake pearls",
              photoURL: "https://upload.wikimedia.org/wikipedia/commons/1/1d/Yandhi_Cover_Art_%28Free_License%29.jpg", price: 19.99,
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
      Message(id: "fake_id",
              user: aUser(),
              body: "Hey there I'm chris",
              createdAt: Date()),
      Message(id: "fake_is",
              user: bUser(),
              body: "Hey there I'm alexis",
              createdAt: Date()),
      Message(id: "fake_it",
              user: aUser(),
              body: "Hey there I'm tired",
              createdAt: Date()),
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
                    name: "Tina Streamsy Urchin",
                    imageURL: "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg")
  }
  
  class func aStream() -> FakeStream {
    return FakeStream(id: "fake_id",
                  createdAt: Date(),
                  creator: cUser(),
                  creatorName: "Jay-Z",
                  endedAt: Date().addingTimeInterval(600),
                  name: "Jay-Z stream",
                  url: "https://file-examples.com/wp-content/uploads/2017/04/file_example_MP4_480_1_5MG.mp4",
                  chat: aChat(),
                  products: someProducts())
  }
}
