//
//  SettingRemovePasscodeViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/26.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class SettingRemovePasscodeViewController: UIViewController {
    
    @IBOutlet weak var captionLabel: UILabel!
    @IBOutlet weak var passcodeTextField: UITextField!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "SettingRemovePasscode", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        passcodeTextField.becomeFirstResponder()
        passcodeTextField.defaultTextAttributes.updateValue(36.0,
                                                            forKey: NSAttributedString.Key.kern)
        captionLabel.text = "パスコードを入力してください"
    }
    
    @IBAction func removeButtonWasTapped(_ sender: Any) {
        
        let ud = UserDefaults.standard
        let passcode = ud.string(forKey: "passcode")
        if passcode == passcodeTextField.text {
            ud.set("", forKey: "passcode")
            ud.synchronize()
            
            let alert = UIAlertController(title: "成功",
                                          message: "パスコードを削除しました",
                                          preferredStyle: .alert)
            alert.show(self, sender: nil)
            let action = UIAlertAction(title: "OK", style: UIAlertAction.Style.default) { (alertAction) in
                self.dismiss(animated: true, completion: nil)
            }
            alert.addAction(action)
            self.present(alert, animated: true, completion: nil)
        } else {
            captionLabel.text = "パスコードが間違っています"
        }
        
    }
}

extension SettingRemovePasscodeViewController: UITextFieldDelegate {
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let str = textField.text! + string
        if str.count <= 4 {
            return true
        }
        return false
    }
    
}
