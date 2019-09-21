//
//  InputBodyMassTextbasedViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/17.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class InputBodyMassTextbasedViewController: UITableViewController {
    @IBOutlet weak var weightTextField: UITextField!
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "InputBodyMassTextbased", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    @IBAction func cancelButtonWasTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func saveButtonWasTapped(_ sender: Any) {
        let weight = atof(weightTextField.text)
        
        var bmi = 0.0
        if (HealthKitManager.shared.height != 0.0) {
            let height = HealthKitManager.shared.height/100
            bmi = weight / (height * height)
            print("height \(height), bmi \(bmi)")
        }
        
        HealthKitManager.shared.setBodyMass(bodyMassKg: weight) { (success) in
            if (success) {
                if (bmi > 0.0) {
                    HealthKitManager.shared.setBMI(bmi: bmi, completion: { (success) in
                        if (success) {
                            DispatchQueue.main.async {
                                self.dismiss(animated: true, completion: nil)
                            }
                        }
                    })
                } else {
                    DispatchQueue.main.async {
                        self.dismiss(animated: true, completion: nil)
                    }
                }
                
            } else {
                
            }
        }
    }
    
    func validate(string: String) {
        let num = atof(string)
        saveButton.isEnabled = num > 10.0 && num < 200.0
    }
}

extension InputBodyMassTextbasedViewController: UITextFieldDelegate {
    
    func textField(_ textField: UITextField,
                   shouldChangeCharactersIn range: NSRange,
                   replacementString string: String) -> Bool {
        let str = weightTextField.text! + string
        validate(string: str)
        return true
    }
}

