//
//  QRKitInputViewController.m
//  mpm
//
//  Created by administrator on 18/12/2018.
//  Copyright © 2018 BCCard. All rights reserved.
//

#import "QRKitInputViewController.h"

@interface QRKitInputViewController ()

@end

@implementation QRKitInputViewController
@synthesize serialNo;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIToolbar* numberToolbar = [[UIToolbar alloc]initWithFrame:CGRectMake(0, 0, 320, 50)];
    numberToolbar.barStyle = UIBarStyleDefault;
    numberToolbar.barTintColor = [UIColor colorWithRed:(250/255.0) green:(51/255.0) blue:(69/255.0) alpha:1];
    numberToolbar.tintColor = [UIColor whiteColor];
    numberToolbar.items = @[[[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil],
                            [[UIBarButtonItem alloc]initWithTitle:@"확인" style:UIBarButtonItemStyleDone target:self action:@selector(doneWithNumberPad)],
                            [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil]];
    [numberToolbar sizeToFit];
    serialNo.inputAccessoryView = numberToolbar;
    
    CALayer *border = [CALayer layer];
    CGFloat borderWidth = 1;
    border.borderColor = [UIColor darkGrayColor].CGColor;
    border.frame = CGRectMake(0, serialNo.frame.size.height - borderWidth, serialNo.frame.size.width, serialNo.frame.size.height);
    border.borderWidth = borderWidth;
    [serialNo.layer addSublayer:border];
    serialNo.layer.masksToBounds = YES;
    
    [self.serialNo becomeFirstResponder];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)cancelNumberPad {
    [serialNo resignFirstResponder];
    serialNo.text = @"";
}

- (void)doneWithNumberPad {
    NSString *data = serialNo.text;
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQrkitData" object:data userInfo:nil];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)actionDoneBtn:(id)sender {
    NSString *data = serialNo.text;
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQrkitData" object:data userInfo:nil];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)actionClose:(id)sender {
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQrkitData" object:@"" userInfo:nil];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    NSString *data = serialNo.text;
    
    [[NSNotificationCenter defaultCenter]postNotificationName:@"resQrkitData" object:data userInfo:nil];
    [self dismissViewControllerAnimated:YES completion:nil];
    
    return TRUE;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (string.length > 0) {
        NSCharacterSet *numbersOnly = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
        NSCharacterSet *characterSetFromTextField = [NSCharacterSet characterSetWithCharactersInString:string];
        
        BOOL stringIsValid = [numbersOnly isSupersetOfSet:characterSetFromTextField];
        return stringIsValid;
    }
    return YES;
}

@end
