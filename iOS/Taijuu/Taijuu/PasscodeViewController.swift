//
//  PasscodeViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/16.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class PasscodeViewController: UIViewController {
    @IBOutlet weak var captionLabel: UILabel!
    @IBOutlet weak var passcodeTextField: UITextField!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "Passcode", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        captionLabel.text = "パスコードを入力してください"
        
        passcodeTextField.becomeFirstResponder()
        passcodeTextField.defaultTextAttributes.updateValue(36.0,
                                                            forKey: NSAttributedString.Key.kern)
        
        
        let ud = UserDefaults.standard
        let passcode = ud.string(forKey: "passcode")
        print("passcode: \(passcode ?? "-")")
    }
    
    func checkPasscode(text: String) {
        
        let ud = UserDefaults.standard
        let passcode = ud.string(forKey: "passcode")
        if passcode == text {
            self.dismiss(animated: true, completion: nil)
        } else {
            captionLabel.text = "パスコードが間違っています"
        }
    }
}

extension PasscodeViewController: UITextFieldDelegate {
    
    // テキスト編集中に呼ばれる
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let maxLength: Int = 4
        let str = textField.text! + string
        
        if str.count == 4 {
            checkPasscode(text: str)
        }
        
        if str.count <= maxLength {
            return true
        }
        return false
    }
}
