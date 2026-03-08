//
//  PushAllowViewController.h
//  mpm
//
//  Created by SugjinMac on 2018. 3. 16..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>

@class MainViewController;

@interface PushAllowViewController : UIViewController {
    MainViewController *parent;
}
-(void)SetParent:(MainViewController *)_parent;
@end
