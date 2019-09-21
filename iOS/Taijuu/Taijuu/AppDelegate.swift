//
//  AppDelegate.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/15.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import UIKit
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
//        showPasscodeIFNeed()
        return true
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        
        showPasscodeIFNeed()
        
        if NotificationManager.shared.granted {
            NotificationManager.shared.reserveNotification()
        }
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }

    func showPasscodeIFNeed() {
        let ud = UserDefaults.standard
        let passcode = ud.string(forKey: "passcode")
        if passcode == nil || passcode!.isEmpty {
            return
        }
        
        if let rootViewController = UIApplication.shared.keyWindow?.rootViewController {
            
            var topViewController: UIViewController = rootViewController
            while let presentedViewController = topViewController.presentedViewController {
                topViewController = presentedViewController
            }
            
            let isDisplayedPasscodeLock: Bool = topViewController.children.map{
                return $0 is PasscodeViewController
            }.contains(true)
            
            if !isDisplayedPasscodeLock {
                let vc = PasscodeViewController.create()
                let nav = UINavigationController(rootViewController: vc)
                nav.modalPresentationStyle = .overFullScreen
                nav.modalTransitionStyle   = .crossDissolve
                nav.isNavigationBarHidden = true
                topViewController.present(nav, animated: true, completion: nil)
            }
        }
    }
}
