//
//  QRReaderViewController.h
//  mpm
//
//  Created by administrator on 18/12/2018.
//  Copyright © 2018 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface QRReaderViewController : UIViewController<AVCaptureMetadataOutputObjectsDelegate>

@property (weak, nonatomic) IBOutlet UIView *preview;
@property (weak, nonatomic) IBOutlet UITextView *camDisable;
@property (weak, nonatomic) IBOutlet UIImageView *camEdge;

@end

NS_ASSUME_NONNULL_END
