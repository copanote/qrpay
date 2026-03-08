//
//  AppDelegate.h
//  mpm
//
//  Created by SugjinMac on 2018. 1. 30..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>
@import Firebase;

@interface AppDelegate : UIResponder <UIApplicationDelegate, FIRMessagingDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) NSString *currentServerURL;

@end

