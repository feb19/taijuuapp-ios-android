//
//  InputBodyMassPickerbasedViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/17.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit

class InputBodyMassPickerbasedViewController: UITableViewController {
    
    @IBOutlet weak var pickerView: UIPickerView!
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "InputBodyMassPickerbased", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        if HealthKitManager.shared.bodyMass.count > 0 {
            let bodyMass = (HealthKitManager.shared.bodyMass.first?.bodyMass ?? 60.0)
            let m = "\(bodyMass)".components(separatedBy: ".")[1]
            pickerView.selectRow(Int(bodyMass), inComponent: 0, animated: false)
            pickerView.selectRow(Int(m) ?? 0, inComponent: 1, animated: false)
        } else {
            pickerView.selectRow(60, inComponent: 0, animated: false)
        }
        
    }
    
    @IBAction func cancelButtonWasTapped(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func saveButtonWasTapped(_ sender: Any) {
        let row0 = pickerView.selectedRow(inComponent: 0)
        let row1 = pickerView.selectedRow(inComponent: 1)
        let weight = atof("\(row0).\(row1)")
        
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
}

extension InputBodyMassPickerbasedViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    
    func pickerView(_ pickerView: UIPickerView, widthForComponent component: Int) -> CGFloat {
        return 64.0
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if component == 0 { return 150 }
        else { return 10 }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if component == 0 { return "\(row)" }
        else { return ".\(row)" }
    }
}
