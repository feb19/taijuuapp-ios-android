//
//  ViewController.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/15.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import UIKit

class HomeViewController: UITableViewController {
    
    static func create() -> UIViewController {
        let storyboard = UIStoryboard(name: "HomeViewController", bundle: Bundle.main)
        let vc = storyboard.instantiateInitialViewController()!
        return vc
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        tableView.refreshControl = UIRefreshControl(frame: CGRect.zero)
        tableView.refreshControl?.addTarget(self, action: #selector(HomeViewController.refresh(sender:)), for: .valueChanged)
        
        NotificationManager.shared.requestAuth()
        HealthKitManager.shared.register { (success) in
            self.getHistory()
        }
        
        let delegate = UIApplication.shared.delegate as! AppDelegate
        delegate.showPasscodeIFNeed()
    }
    
    @objc func refresh(sender: UIRefreshControl) {
        tableView.refreshControl?.endRefreshing()
        getHistory()
    }
    
    func getHistory() {
        HealthKitManager.shared.getBodyMass { (sucess) in
            self.tableView.reloadData()
        }
        HealthKitManager.shared.getHeight { (success) in
            
        }
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 { return 280 }
        else { return 44 }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 { return 1 }
        return HealthKitManager.shared.bodyMass.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let cell = tableView.dequeueReusableCell(withIdentifier: "BodyMassGraphCell") as! BodyMassGraphCell
            cell.configure(bodyMasses: HealthKitManager.shared.bodyMass)
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "BodyMassCell") as! BodyMassCell
            if HealthKitManager.shared.bodyMass.count == 0 {
                return cell
            }
            
            let bodyMass = HealthKitManager.shared.bodyMass[indexPath.row]
            cell.configure(bodyMass: bodyMass)
            return cell
        }
    }

    @IBAction func settingsButtonWasTapped(_ sender: Any) {
        let vc = SettingsViewController.create()
        self.present(vc, animated: true, completion: nil)
    }
    
    @IBAction func addButtonWasTapped(_ sender: Any) {
        let vc = InputBodyMassPickerbasedViewController.create()
        self.present(vc, animated: true, completion: nil)
    }
}

class BodyMassGraphView: UIView {
    
    var bodyMasses: [BodyMass] = []
    var min: Double = 0
    var max: Double = 0
    var avg: Double = 0
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.backgroundColor = .white
    }
    
    public required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    public override func draw(_ rect: CGRect) {
        if bodyMasses.count == 0 { return }
        guard let context = UIGraphicsGetCurrentContext() else { return }
        
        let width = Double(bounds.size.width)
        let height = Double(bounds.size.height)
        
        context.setLineWidth(1.0)
        context.setStrokeColor(UIColor.red.cgColor)
        context.move(to: CGPoint(x: 0, y: height-(avg-min)/(max-min) * height))
        context.addLine(to: CGPoint(x: width, y: height-(avg-min)/(max-min) * height))
        context.strokePath()
        
        context.setLineWidth(1.0)
        context.setStrokeColor(self.tintColor.cgColor)
        context.move(to: CGPoint(x: width,
                                 y: height-(bodyMasses[0].bodyMass - min) / (max-min) * height))
        let count = 7 < bodyMasses.count ? 7 : bodyMasses.count
        for i in 1 ..< count {
            print(bodyMasses[i].bodyMass)
            context.addLine(to: CGPoint(x: ((6.0 - Double(i)) / 6.0) * width,
                                        y: height-(bodyMasses[i].bodyMass - min) / (max-min) * height))
        }
        context.strokePath()
        
        // memo
        // 移動平均とか出したら面白いかも
    }
}

class BodyMassGraphCell: UITableViewCell {
    
    @IBOutlet weak var maxLabel: UILabel!
    @IBOutlet weak var avgLabel: UILabel!
    @IBOutlet weak var minLabel: UILabel!
    @IBOutlet weak var dateLabel0: UILabel!
    @IBOutlet weak var dateLabel1: UILabel!
    @IBOutlet weak var dateLabel2: UILabel!
    @IBOutlet weak var dateLabel3: UILabel!
    @IBOutlet weak var dateLabel4: UILabel!
    @IBOutlet weak var dateLabel5: UILabel!
    @IBOutlet weak var dateLabel6: UILabel!
    @IBOutlet weak var graphView: BodyMassGraphView!
    
    func configure(bodyMasses: [BodyMass]) {
        var labels = [
            dateLabel6,
            dateLabel5,
            dateLabel4,
            dateLabel3,
            dateLabel2,
            dateLabel1,
            dateLabel0
        ]
        let f = DateFormatter()
        f.locale = Locale(identifier: "ja_JP")
        f.setLocalizedDateFormatFromTemplate("M/dd")
        
        var max = 0.0
        var min = 200.0
        var avg = 0.0
        var count = 0.0
        for i in 0..<7 {
            let label = labels[i]!
            if bodyMasses.count > 0, i < bodyMasses.count - 1 {
                label.text = f.string(from: bodyMasses[i].date)
                
                let b = bodyMasses[i].bodyMass
                if min > b {
                    min = b
                }
                if max < b {
                    max = b
                }
                avg += b
                count += 1.0
            } else {
                label.text = ""
            }
        }
        maxLabel.text = String("\(String(format: "%0.1f", max))kg")
        minLabel.text = String("\(String(format: "%0.1f", min))kg")
        if count > 0.0 {
            avgLabel.text = String("\(String(format: "%0.1f", (max-min)/2+min))kg")
        }
        graphView.min = min
        graphView.max = max
        graphView.avg = avg/count
        graphView.bodyMasses = bodyMasses
        
        graphView.setNeedsDisplay()
    }
}

class BodyMassCell: UITableViewCell {
    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var gramLabel: UILabel!
    
    func configure(bodyMass: BodyMass) {
        let f = DateFormatter()
        f.dateStyle = .short
        f.locale = Locale(identifier: "ja_JP")
        dateLabel.text = f.string(from: bodyMass.date)
        
        gramLabel.text = String("\(String(format: "%0.1f", bodyMass.bodyMass))kg")
    }
}
