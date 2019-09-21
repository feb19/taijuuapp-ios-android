//
//  SettingPasscodeViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/16.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class SettingPasscodeViewController: UIViewController {
    
    @IBOutlet weak var passcodeTextField: UITextField!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "SettingPasscode", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        passcodeTextField.becomeFirstResponder()
        passcodeTextField.defaultTextAttributes.updateValue(36.0,
                                               forKey: NSAttributedString.Key.kern)
    }
    
    @IBAction func saveButtonWasTapped(_ sender: Any) {
        if passcodeTextField.text?.count == 4 {
            let ud = UserDefaults.standard
            ud.set(passcodeTextField.text, forKey: "passcode")
            ud.synchronize()
            
            let alert = UIAlertController(title: "成功",
                                          message: "パスコードを設定しました",
                                          preferredStyle: .alert)
            alert.show(self, sender: nil)
            let action = UIAlertAction(title: "OK", style: UIAlertAction.Style.default) { (alertAction) in
                self.dismiss(animated: true, completion: nil)
            }
            alert.addAction(action)
            self.present(alert, animated: true, completion: nil)
        }
    }
}

extension SettingPasscodeViewController: UITextFieldDelegate {
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let str = textField.text! + string
        if str.count <= 4 {
            return true
        }
        return false
    }
    
}
