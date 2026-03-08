//
//  MainViewController.h
//  mpm
//
//  Created by SugjinMac on 2018. 3. 27..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ViewController;
@interface MainViewController : UIViewController {
    int initView;
    ViewController *mainView;
}

- (void)check;
- (void)setInitView:(int)value;
- (int)getInitView;
- (ViewController *)getMainView;
@end
