//
//  QRKitReadViewController.h
//  mpm
//
//  Created by Young Yun Na on 03/05/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//



#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface QRKitReadViewController : UIViewController<AVCaptureMetadataOutputObjectsDelegate>

@property (weak, nonatomic) IBOutlet UIView *preview;
@property (weak, nonatomic) IBOutlet UITextView *camDisable;
@property (weak, nonatomic) IBOutlet UIImageView *camEdge;

@end

NS_ASSUME_NONNULL_END
