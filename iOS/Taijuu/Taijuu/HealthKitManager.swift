//
//  HealthKitManager.swift
//  Taijuu
//
//  Created by Nobuhiro Takahashi on 2019/08/15.
//  Copyright © 2019 Nobuhiro Takahashi. All rights reserved.
//

import Foundation
import UIKit
import HealthKit

struct BodyMass {
    var date = Date()
    var bodyMass = 0.0
    
    init(date: Date, bodyMass: Double) {
        self.date = date
        self.bodyMass = bodyMass
    }
}

class HealthKitManager {
    static var shared = HealthKitManager()
    private let store = HKHealthStore()
    
    var bodyMass : [BodyMass] = []
    var height: Double = 0.0
    
    func register(completion: ((_ success: Bool?) -> Void)!) {
        if !HKHealthStore.isHealthDataAvailable() {
            completion(false)
            return
        }
        
        let shareType: Set<HKSampleType> = [
            HKSampleType.quantityType(forIdentifier: .bodyMass)!,
            HKSampleType.quantityType(forIdentifier: .bodyMassIndex)!,
            HKSampleType.quantityType(forIdentifier: .height)!
        ]
        let readType: Set<HKObjectType> = [
            HKObjectType.quantityType(forIdentifier: .bodyMass)!,
            HKObjectType.quantityType(forIdentifier: .height)!
        ]
        
        store.requestAuthorization(toShare: shareType, read: readType) { [weak self] (success, error) in
            if error != nil {
                completion(false)
                return
            }
            print("success")
            completion(true)
        }
    }
    
    func getBodyMass(completion: ((_ success: Bool?) -> Void)!) {
        // 今
        let now = Date()
        let endDate = now
        // 一年前の今日
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: now)
        let startDate = calendar.date(byAdding: Calendar.Component.year,
                                      value: -1, to: today,
                                      wrappingComponents: true)!
        
        // 日の区切りは「今日の始まりの0時から」
        let anchorDate = today
        
        print("startDate: \(startDate) - endDate: \(endDate) | anchorDate: \(anchorDate)")
        
        // 間隔は1日ごと
        let intervalComponents = NSDateComponents()
        intervalComponents.day = 1
        
        // クエリを送るときに、サンプルをフィルタリングするために使う述語（Predicate）を作る
        let predicate = HKQuery.predicateForSamples(withStart: startDate,
                                                    end: endDate,
                                                    options: .strictEndDate)
        let quantityType = HKObjectType.quantityType(forIdentifier: .bodyMass)!
        let statsOptions: HKStatisticsOptions = [.separateBySource, .discreteMostRecent]
        let query = HKStatisticsCollectionQuery(
            quantityType: quantityType,
            quantitySamplePredicate: predicate,
            options: statsOptions,
            anchorDate: anchorDate,
            intervalComponents: intervalComponents as DateComponents)
        
        self.bodyMass = []
        
        query.initialResultsHandler = { [weak self] (query, result, error) in
            guard let result = result, error == nil else{
                return
            }
            for item in result.statistics() {
                // kg 表現で取得
                let kgUnit = HKUnit.gramUnit(with: .kilo)
                let kg = item.mostRecentQuantity()!.doubleValue(for: kgUnit)
                let bodyMass = BodyMass(date: item.endDate,
                                        bodyMass: kg)
                self?.bodyMass.append(bodyMass)
            }
            self?.bodyMass.reverse()
            
            // UI を更新する処理は main thread で
            DispatchQueue.main.async {
                completion(true)
            }
        }
        
        store.execute(query)
    }
    
    /*
    func getBIM(completion: ((_ success: Bool?) -> Void)!) {
        let calendar = Calendar.current
        
        let now = Date()
        let today = calendar.startOfDay(for: now)
        
        let endDate = now
        let startDate = calendar.date(byAdding: Calendar.Component.year, value: -1, to: today, wrappingComponents: true)!
        let anchorDate = today
        
        print("startDate: \(startDate) - endDate: \(endDate)")
        print("anchorDate: \(anchorDate)")
        
        let intervalComponents = NSDateComponents()
        intervalComponents.day = 1
        
        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: endDate, options: .strictEndDate)
        let quantityType = HKObjectType.quantityType(forIdentifier: .bodyMassIndex)!
        let statsOptions: HKStatisticsOptions = [HKStatisticsOptions.separateBySource, HKStatisticsOptions.discreteMostRecent]
        
        let query = HKStatisticsCollectionQuery(quantityType: quantityType,
                                                quantitySamplePredicate: predicate,
                                                options: statsOptions,
                                                anchorDate: anchorDate,
                                                intervalComponents: intervalComponents as DateComponents)
        
        
        
        query.initialResultsHandler = { [weak self] (query, result, error) in
            guard let result = result, error == nil else{
                return
            }
            for item in result.statistics() {
                print("\(item.startDate) - \(item.endDate) \(item.mostRecentQuantity()!.doubleValue(for: HKUnit.count()))")
            }
        }
        
        store.execute(query)
    }
 */
    
    func getHeight(completion: ((_ success: Bool?) -> Void)!) {
        let type = HKSampleType.quantityType(forIdentifier: .height)!
        // 最新一件のみ取得
        let sortDescriptor = NSSortDescriptor(key: HKSampleSortIdentifierStartDate, ascending: false)
        let query = HKSampleQuery(sampleType: type,
                                  predicate: nil,
                                  limit: 1,
                                  sortDescriptors: [sortDescriptor]) {
                                    (query, results, error) in
            if let result = results?.first as? HKQuantitySample{
                print("Height => \(result.quantity) \(result.startDate) \(result.endDate)")
                let cmUnit = HKUnit.meterUnit(with: .centi)
                let cm = result.quantity.doubleValue(for: cmUnit)
                self.height = cm
                completion(true)
            } else {
                print(error ?? "no error")
                completion(false)
            }
        }
        store.execute(query)
    }
    
    func setBodyMass(bodyMassKg: Double, completion: ((_ success: Bool) -> Void)!) {
        let type = HKSampleType.quantityType(forIdentifier: .bodyMass)!
        let date = Date()
        let quantity = HKQuantity(unit: HKUnit.gramUnit(with: .kilo), doubleValue: bodyMassKg)
        let sample = HKQuantitySample(type: type,
                                      quantity: quantity,
                                      start: date,
                                      end: date)
        store.save(sample, withCompletion: { (success, error) in
            if error != nil { completion(false) }
            completion(true)
        })
    }
    
    func setBMI(bmi: Double, completion: ((_ success: Bool) -> Void)!) {
        let type = HKSampleType.quantityType(forIdentifier: .bodyMassIndex)!
        let date = Date()
        let quantity = HKQuantity(unit: HKUnit.count(), doubleValue: bmi)
        let sample = HKQuantitySample(type: type,
                                      quantity: quantity,
                                      start: date,
                                      end: date)
        store.save(sample, withCompletion: { (success, error) in
            if error != nil { completion(false) }
            completion(true)
        })
    }
    
    func setHeight(heightCm: Double, completion: ((_ success: Bool) -> Void)!) {
        let type = HKSampleType.quantityType(forIdentifier: .height)!
        let date = Date()
        let quantity = HKQuantity(unit: HKUnit.meterUnit(with: .centi), doubleValue: heightCm)
        let sample = HKQuantitySample(type: type,
                                      quantity: quantity,
                                      start: date,
                                      end: date)
        store.save(sample, withCompletion: { (success, error) in
            if error != nil { completion(false) }
            completion(true)
        })
    }
}
