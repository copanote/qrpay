//
//  QRKitInputViewController.h
//  mpm
//
//  Created by administrator on 18/12/2018.
//  Copyright © 2018 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface QRKitInputViewController : UIViewController<UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *serialNo;

@end

NS_ASSUME_NONNULL_END
