//
//  MainViewController.m
//  mpm
//
//  Created by SugjinMac on 2018. 3. 27..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import "MainViewController.h"
#import "ViewController.h"
#import "PushAllowViewController.h"
#import "PushInfoViewController.h"
#import "NSDictionary+Safe.h"

@interface MainViewController ()

@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    initView = 0;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self check];
}

- (void)check {
    if (initView == 0) {
        initView = initView + 1;
        
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        NSString *pushAllow = [userDefault stringForKey:@"pushAllow"];
        
        if ([pushAllow length] == 0) {
            UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
            PushAllowViewController *pushAllowView = (PushAllowViewController *) [storyboard instantiateViewControllerWithIdentifier:@"PushAllowView"];
            [pushAllowView SetParent:self];
            pushAllowView.modalPresentationStyle = UIModalPresentationFullScreen;
            [self presentViewController:pushAllowView animated:YES completion:nil];
        }else {
            if (mainView == nil) {
                UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
                mainView = (ViewController *) [storyboard instantiateViewControllerWithIdentifier:@"MainView"];
            }
            mainView.modalPresentationStyle = UIModalPresentationFullScreen;
            [self presentViewController:mainView animated:YES completion:nil];
        }
    }
}

- (void)setInitView:(int)value {
    initView = value;
}

- (int)getInitView {
    return initView;
}

- (ViewController *)getMainView {
    if(mainView == nil){
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        mainView = (ViewController *) [storyboard instantiateViewControllerWithIdentifier:@"MainView"];
    }
    
    return mainView;
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

@end
