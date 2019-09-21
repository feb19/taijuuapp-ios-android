//
//  SettingsViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/16.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class SettingsViewController: UITableViewController {
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "Settings", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    func goSettingHeight() {
        let vc = SettingHeightViewController.create()
        navigationController?.pushViewController(vc, animated: true)
    }
    
    func goSettingPasscode() {
        let ud = UserDefaults.standard
        let passcode = ud.string(forKey: "passcode")
        if passcode == nil || passcode!.isEmpty {
            let vc = SettingPasscodeViewController.create()
            navigationController?.pushViewController(vc, animated: true)
        } else {
            let vc = SettingRemovePasscodeViewController.create()
            navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    @IBAction func doneButtonWasTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if (indexPath.row == 0) { goSettingHeight() }
        if (indexPath.row == 1) { goSettingPasscode() }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "SettingsViewCell")
        
        if indexPath.row == 1 {
            let ud = UserDefaults.standard
            let passcode = ud.string(forKey: "passcode")
            if passcode == nil || passcode!.isEmpty {
                cell?.textLabel?.textColor = .black
                cell?.textLabel?.text = "パスコードの設定"
            } else {
                cell?.textLabel?.textColor = .red
                cell?.textLabel?.text = "パスコードを削除する"
            }
        } else {
            cell?.textLabel?.text = "身長の設定"
        }
        
        return cell!
    }
}
