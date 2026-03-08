//
//  QRKitReadViewController.m
//  mpm
//
//  Created by Young Yun Na on 03/05/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import "QRKitReadViewController.h"
#import "AppDelegate.h"
#import "ViewController.h"

@interface QRKitReadViewController ()
@property (nonatomic, strong) AVCaptureSession *captureSession;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *videoPreviewLayer;
@property (nonatomic, assign) BOOL isDrawEdge;
@end


@implementation QRKitReadViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _captureSession = nil;
    
    [_camDisable setHidden:TRUE];
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (BOOL)startReading {
    NSError *error;
    
    AVCaptureDevice *captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:captureDevice error:&error];
    if (!input) {
        NSLog(@"%@", [error localizedDescription]);
        [_camDisable setHidden:FALSE];
        
        AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
        
        switch(status) {
            case AVAuthorizationStatusAuthorized: {
                [_camDisable setHidden:TRUE];
                [self startReading];
            };
                break;
            case AVAuthorizationStatusNotDetermined: {
                [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
                    if (granted) {
                        dispatch_async (dispatch_get_main_queue (), ^{
                            [_camDisable setHidden:TRUE];
                        });
                    }
                    else {
                        [_camDisable setHidden:FALSE];
                    }
                }];
            };
                break;
            case AVAuthorizationStatusDenied: {
                
                UIAlertController * alert=   [UIAlertController
                                              alertControllerWithTitle:@"BC QR FOR SHOP의 다음 작업을 허용하시겠습니까? 사진 및 동영상 촬영"
                                              message:@"(허용하지 않으면 스캔 기능을 이용할 수 없습니다.)"
                                              preferredStyle:UIAlertControllerStyleAlert];
                UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"취소" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action){
                    [_camDisable setHidden:FALSE];
                }];
                UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"허용" style:UIAlertActionStyleDefault handler:^(UIAlertAction *action)
                                           {
                    if (([[[UIDevice currentDevice] systemVersion] compare:@"10.0" options:NSNumericSearch] == NSOrderedDescending) == YES) {
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]
                                                           options:@{}
                                                 completionHandler:nil];
                    }else {
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
                    }
                }];
                
                [alert addAction:cancelAction];
                [alert addAction:okAction];
                
                UIViewController *rootView = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
                
                while (rootView.presentedViewController) {
                    rootView = rootView.presentedViewController;
                }
                [rootView presentViewController:alert animated:YES completion:nil];
            }
                break;
            case AVAuthorizationStatusRestricted: {
            };
                break;
        }
        return NO;
    }
    
    _captureSession = [[AVCaptureSession alloc] init];
    [_captureSession addInput:input];
    
    AVCaptureMetadataOutput *captureMetadataOutput = [[AVCaptureMetadataOutput alloc] init];
    [_captureSession addOutput:captureMetadataOutput];
    
    int edgeSize = self.preview.frame.size.width/3*2;
    CGRect edgeRect = CGRectMake(0, 0, 0, 0);
    if (_isDrawEdge == FALSE) {
        _camEdge.image = [UIImage imageNamed:@"cam_edge.png"];
        _camEdge.frame = CGRectMake(0, 0, edgeSize, edgeSize);
        _camEdge.center = _camEdge.superview.center;
        
        edgeRect = [self setImageToCenter:_camEdge];
        UIBezierPath *overlayPath = [UIBezierPath bezierPathWithRect:self.preview.bounds];
        UIBezierPath *transparentPath = [UIBezierPath bezierPathWithRect:edgeRect];
        [overlayPath appendPath:transparentPath];
        [overlayPath setUsesEvenOddFillRule:YES];
        
        CAShapeLayer *fillLayer = [CAShapeLayer layer];
        fillLayer.path = overlayPath.CGPath;
        fillLayer.fillRule = kCAFillRuleEvenOdd;
        fillLayer.fillColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.5].CGColor;
        
        [self.preview.layer addSublayer:fillLayer];
        [self.preview.layer addSublayer:_camEdge.layer];
        
        _isDrawEdge = TRUE;
        
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        CGFloat screenWidth = screenRect.size.width;
        
        UIFont * customFont = [UIFont fontWithName:@"HelveticaNeue" size:15]; //custom font
        NSString * text = @"페이북 QR 스캔만 가능합니다.";
        
        CGSize labelSize = [text sizeWithFont:customFont constrainedToSize:CGSizeMake(380, 20) lineBreakMode:NSLineBreakByTruncatingTail];
        
        UILabel *fromLabel = [[UILabel alloc]initWithFrame:CGRectMake(0 , edgeRect.origin.y+edgeRect.size.height+45, screenWidth - 40, labelSize.height)];
        fromLabel.text = text;
        fromLabel.font = customFont;
        fromLabel.numberOfLines = 1;
        fromLabel.baselineAdjustment = UIBaselineAdjustmentAlignBaselines;
        fromLabel.adjustsFontSizeToFitWidth = YES;
        fromLabel.adjustsLetterSpacingToFitWidth = YES;
        fromLabel.minimumScaleFactor = 10.0f/12.0f;
        fromLabel.clipsToBounds = YES;
        fromLabel.backgroundColor = [UIColor clearColor];
        fromLabel.textColor = [UIColor whiteColor];
        fromLabel.textAlignment = NSTextAlignmentCenter;
        [self.preview addSubview:fromLabel];
    }
    
    dispatch_queue_t dispatchQueue;
    dispatchQueue = dispatch_queue_create("myQueue", NULL);
    [captureMetadataOutput setMetadataObjectsDelegate:self queue:dispatchQueue];
    [captureMetadataOutput setMetadataObjectTypes:[NSArray arrayWithObject:AVMetadataObjectTypeQRCode]];
    
    _videoPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:_captureSession];
    [_videoPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    [_videoPreviewLayer setFrame:_preview.layer.bounds];
    [_preview.layer insertSublayer:_videoPreviewLayer atIndex:0];
    [_captureSession startRunning];
    [captureMetadataOutput setRectOfInterest:[_videoPreviewLayer metadataOutputRectOfInterestForRect:edgeRect]];
    
    return YES;
}

- (CGRect)setImageToCenter:(UIImageView *)imageView {
    CGRect rect;
    CGSize imageSize = imageView.image.size;
    [imageView sizeThatFits:imageSize];
    CGPoint imageViewCenter = imageView.center;
    
    imageViewCenter.x = CGRectGetMidX(self.preview.frame) - 20;
    imageViewCenter.y = CGRectGetMidY(self.preview.frame)-(self.preview.frame.origin.y);
    [imageView setCenter:imageViewCenter];
    
    rect.origin.x = imageView.frame.origin.x+5;
    rect.origin.y = imageView.frame.origin.y+5;
    rect.size.width = imageView.frame.size.width - 10;
    rect.size.height = imageView.frame.size.height - 10;
    
    return rect;
}

- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection {
    if (metadataObjects != nil && [metadataObjects count] > 0) {
        AVMetadataMachineReadableCodeObject *metadataObj = [metadataObjects objectAtIndex:0];
        if ([[metadataObj type] isEqualToString:AVMetadataObjectTypeQRCode]) {
            NSLog(@"QRReaderViewController Code : %@", [metadataObj stringValue]);
            NSString *data = [metadataObj stringValue];
            
            [self performSelectorOnMainThread:@selector(stopReading:) withObject:data waitUntilDone:NO];
        }
    }
}

- (void)stopReading:(NSString *)data {
    [_captureSession stopRunning];
    _captureSession = nil;
    
    [_videoPreviewLayer removeFromSuperlayer];
    
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQRReadData" object:data userInfo:nil];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (IBAction)actionScanClose:(id)sender {
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQRReadData" object:@"" userInfo:nil];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    if (_captureSession == nil)
        [self startReading];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    
    [_captureSession stopRunning];
    _captureSession = nil;
    
    [_videoPreviewLayer removeFromSuperlayer];
}

@end
