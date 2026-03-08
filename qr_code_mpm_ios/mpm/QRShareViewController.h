//
//  QRShareViewController.h
//  mpm
//
//  Created by SugjinMac on 2018. 3. 14..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>

@import WebKit;

@interface QRShareViewController : UIViewController {
    NSString *qrShareUrl;
    CGRect webViewRect;
    BOOL viewInit;
    WKProcessPool *processPool;

    UIButton *button1, *button2;
}

@property (nonatomic, weak) IBOutlet UIView *backgroundView;
@property (weak, nonatomic) IBOutlet UIButton *btnShare;
@property (weak, nonatomic) IBOutlet UIButton *btnCancel;

-(void)SetQRShareUrl:(NSString *)_url;
-(void)loadWebView;
-(void)setProcessPool : (WKProcessPool *)_processPool;

@end
