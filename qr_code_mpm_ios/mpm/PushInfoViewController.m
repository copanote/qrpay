//
//  PushInfoViewController.m
//  mpm
//
//  Created by Young Yun Na on 07/05/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import "PushInfoViewController.h"
#import "MainViewController.h"
#import "CommonUtil.h"
#import "NSString+Format.h"
#import "NSDictionary+Safe.h"

@interface PushInfoViewController ()

@end

const NSInteger cellH = 34;

@implementation PushInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    [userDefault setObject:@"N" forKey:@"is_push"];
    [userDefault synchronize];
    
    NSUserDefaults *PushUserDefault = [NSUserDefaults standardUserDefaults];
    NSDictionary *retrievedDictionary = [PushUserDefault dictionaryForKey:@"pushUserInfo"];
    NSLog(@"PushInfoViewController queryStringDictionary %@",retrievedDictionary);
    //가맹점명
    NSString *decoded = [[userDefault stringForKey:@"merNm"] stringByRemovingPercentEncoding];
    NSLog(@"decoded string :\n%@", decoded);
    [_lb_MerNm setText:decoded];
    [_lb_TRNS_UNIQ_NO setText:[retrievedDictionary stringForKey:@"TRNS_UNIQ_NO"]];
    NSString *sDate = [retrievedDictionary stringForKey:@"TRNS_ATON"];
    NSLog(@"newDate : %@",sDate);
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyyMMddHHmmss"];
    NSDate *date  = [dateFormatter dateFromString:sDate];
    // Convert to new Date Format
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *newDate = [dateFormatter stringFromDate:date];
    NSLog(@"newDate : %@",newDate);
    [_lb_TRNS_ATON setText:newDate];
    [_lb_TRNS_ATON_1 setText:newDate]; //거래일시 가맹점
    
    [_lb_TRNS_AMT setText:[NSString priceStr:[retrievedDictionary doubleForKey:@"TRNS_AMT"]]];
    
    //승인완료, 승인실패, 승인요청
    NSString *strUnicodeString = [retrievedDictionary stringForKey:@"TRNS_STAT_NM"];
    NSData *unicodedStringData = [strUnicodeString dataUsingEncoding:NSUTF8StringEncoding];
    NSString *emojiStringValue = [[NSString alloc] initWithData:unicodedStringData encoding:NSUTF8StringEncoding];
    [_lb_TRNS_STAT setText:emojiStringValue];
    [_bt_TRNS_STAT setTitle:emojiStringValue forState:normal];
    
    _bt_TRNS_STAT.selected = [emojiStringValue containsString:@"실패"];
    _bt_TRNS_STAT.layer.cornerRadius = _bt_TRNS_STAT.frame.size.height/2;
    _bt_TRNS_STAT.layer.borderWidth = 1;
    _bt_TRNS_STAT.layer.borderColor = _bt_TRNS_STAT.currentTitleColor.CGColor;
    
    _v_cancelLine.hidden = ![emojiStringValue containsString:@"취소완료"];
    
    [_lb_SVC_CLSS_NM setText:[retrievedDictionary stringForKey:@"SVC_CLSS_NM"]]; //브랜드
    [_lb_CARD_CO_AUTH_NO setText:[retrievedDictionary stringForKey:@"CARD_CO_AUTH_NO"]];
    [_lb_MPAN_NO setText:[retrievedDictionary stringForKey:@"MPAN_NO"]];
    
    if ([retrievedDictionary doubleForKey:@"INS_TRM"] > 0) {
        [_lb_INS_TRM setText:[NSString stringWithFormat:@"%d개월", (int)[retrievedDictionary doubleForKey:@"INS_TRM"]]];
    }else {
        [_lb_INS_TRM setText:@"일시불"];
    }
    
    [_lb_TRNS_UNIQ_NO setText:[retrievedDictionary stringForKey:@"TRNS_UNIQ_NO"]];
    
    //브랜드가 은련일때만 Voucher No
    if ([[retrievedDictionary stringForKey:@"SVC_CLSS_NM"] containsString:@"유니온페이"]) {
        _v_voucher.hidden = NO;
        _v_voucher.constraints[0].constant = cellH;
        [_lb_AFFI_CO_TRNS_UNIQ_NO setText:[retrievedDictionary stringForKey:@"AFFI_CO_TRNS_UNIQ_NO"]];
    }else {
        _v_voucher.hidden = YES;
        _v_voucher.constraints[0].constant = 0;
    }
    
    //할인금액이 0 이상이면 노출 0이면 hidden
    if ([retrievedDictionary doubleForKey:@"DC_AMT"] > 0) {
        for (UIView *view in _v_discount) {
            view.hidden = NO;
            view.constraints[0].constant = cellH;
        }
        
        [_lb_DC_BEF_AMT setText:[NSString stringWithFormat:@"%@원", [NSString priceStr:[retrievedDictionary doubleForKey:@"DC_BEF_AMT"]]]];
        [_lb_DC_AFTR_AMT setText:[NSString stringWithFormat:@"%@원", [NSString priceStr:[retrievedDictionary doubleForKey:@"DC_AFTR_AMT"]]]];
        [_lb_DC_AMT setText:[NSString stringWithFormat:@"%@원", [NSString priceStr:[retrievedDictionary doubleForKey:@"DC_AMT"]]]];
    }else {
        for (UIView *view in _v_discount) {
            view.hidden = YES;
            view.constraints[0].constant = 0;
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)SetParent:(MainViewController *)_parent {
    parent = _parent;
}

- (IBAction)exitButton:(UIButton *)sender {
    [[self presentingViewController] dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)vochButton:(UIButton *)sender {
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:nil
                                  message:nil
                                  preferredStyle:UIAlertControllerStyleAlert];
    
    NSString * MSG = @"Voucher No.(凭证号)은\n중국 고객 App에 표시되는 승인번호입니다.\n결제 취소 요청시 Voucher No.(凭证号)를 확인해주세요.";
    
    NSMutableAttributedString *pushMsg = [[NSMutableAttributedString alloc] initWithString:MSG];
    [pushMsg addAttribute:NSFontAttributeName
                    value:[UIFont systemFontOfSize:13.0]
                    range:NSMakeRange(0, [MSG length])];
    [alert setValue:pushMsg forKey:@"attributedMessage"];
    
    UIAlertAction *btn1 = [UIAlertAction
                           actionWithTitle:@"확인"
                           style:UIAlertActionStyleDefault
                           handler:^(UIAlertAction * action) {
        [alert dismissViewControllerAnimated:YES completion:nil];
    }];
    [alert addAction:btn1];
    
    [self presentViewController:alert animated:YES completion:nil];
}
@end
