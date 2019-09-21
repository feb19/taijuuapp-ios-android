//
//  SettingHeightViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/16.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class SettingHeightViewController: UITableViewController {
    @IBOutlet weak var heightTextField: UITextField!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "SettingHeight", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        if HealthKitManager.shared.height == 0.0 { return }
        heightTextField.text = "\(String(format: "%0.1f", HealthKitManager.shared.height))"
    }
    
    @IBAction func saveButtonWasTapped(_ sender: Any) {
        
        
        let height = Double(heightTextField.text!)!
        HealthKitManager.shared.setHeight(heightCm: height) { (success) in
            if !success { return }
            DispatchQueue.main.async {
                let alert = UIAlertController(title: "成功",
                                              message: "身長を保存しました",
                                              preferredStyle: .alert)
                alert.show(self, sender: nil)
                let action = UIAlertAction(title: "OK", style: .default, handler: nil)
                alert.addAction(action)
                self.present(alert, animated: true, completion: nil)
            }
        }
        
    }
    
    func validate(string: String) {
        let num = atof(string) 
        saveButton.isEnabled = num > 50.0 && num < 300.0
    }
}

extension SettingHeightViewController: UITextFieldDelegate {
    
    func textField(_ textField: UITextField,
                   shouldChangeCharactersIn range: NSRange,
                   replacementString string: String) -> Bool {
        let str = heightTextField.text! + string
        validate(string: str)
        return true
    }
}
