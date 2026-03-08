//
//  ViewController.h
//  mpm
//
//  Created by SugjinMac on 2018. 1. 30..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QRShareViewController.h"

@interface ViewController : UIViewController {
    
}

@property (weak, nonatomic) IBOutlet UIImageView *img_loadingView;

- (void)pushReceiveCheck;
- (void)moveUrl:(NSString *)_url withParam:(NSString *)_param;

@end

