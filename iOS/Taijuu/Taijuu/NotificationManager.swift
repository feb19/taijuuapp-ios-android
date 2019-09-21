//
//  NotificationManager.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/16.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UserNotifications

class NotificationManager {

    static let shared = NotificationManager()
    
    var granted = false
    
    init() {
        
    }
    
    func requestAuth() {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.badge, .sound, .alert]) { (granted, error) in
            if error != nil {
                return
            }
            
            if granted {
                // 通知許可された
                print("OK")
                self.granted = true
                
            } else {
                // 通知拒否
                print("NG")
            }
        }
    }
    
    func reserveNotification() {
        if !self.granted { return }
        
        cancel()
        
        let content = UNMutableNotificationContent()
        content.title = "体重を記録しましょう！"
        content.subtitle = "from Taijuu"
        content.body = "12 時間後のリマインドです"
        
        let interval = TimeInterval(60*60*12) // 60 sec. * 60 min. * 12 hours.
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: interval,
                                                        repeats: false)
        let request = UNNotificationRequest(identifier: "taijuu12",
                                            content: content,
                                            trigger: trigger)
        
        UNUserNotificationCenter.current().add(request,
                                               withCompletionHandler: nil)
    }
    
    func cancel() {
        let center = UNUserNotificationCenter.current()
        center.removeAllPendingNotificationRequests()
        
    }
}
