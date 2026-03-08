//
//  QRKitRegViewController.m
//  mpm
//
//  Created by administrator on 18/12/2018.
//  Copyright © 2018 BCCard. All rights reserved.
//

#import "QRKitRegViewController.h"

@interface QRKitRegViewController ()

@end

@implementation QRKitRegViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.delegate = self;
    
    if ([AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo] == AVAuthorizationStatusDenied) {
        self.selectedIndex = 1;
    }
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    for (UITabBarItem* item in self.tabBar.items) {
        [item setTitlePositionAdjustment:UIOffsetMake(0, -6)];
        [item setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                      [UIFont systemFontOfSize:16], NSFontAttributeName, nil]
                            forState:UIControlStateNormal];
    }
    [self.tabBar setTintColor:[UIColor colorWithRed:(250/255.0) green:(51/255.0) blue:(69/255.0) alpha:1]];
    [self.tabBar setBarTintColor:[UIColor whiteColor]];
    
    UIView *view = [[UIView alloc]initWithFrame:CGRectMake(self.tabBar.frame.origin.x,self.tabBar.frame.origin.y, self.view.frame.size.width/2, 44)];
    
    UIImageView *border = [[UIImageView alloc]initWithFrame:CGRectMake(view.frame.origin.x,view.frame.size.height-2, self.view.frame.size.width/2, 2)];
    border.backgroundColor = [UIColor redColor];
    [view addSubview:border];
    [self.tabBar setSelectionIndicatorImage:[self changeViewToImage:view]];
}

- (UIImage *) changeViewToImage : (UIView *) viewForImage {
    UIGraphicsBeginImageContext(viewForImage.bounds.size);
    [viewForImage.layer   renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    CGRect cgRect = CGRectMake(0, 108, self.tabBar.frame.size.width, 40);
    self.tabBar.frame = cgRect;
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

@end
