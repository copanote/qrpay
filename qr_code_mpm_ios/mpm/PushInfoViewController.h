//
//  PushInfoViewController.h
//  mpm
//
//  Created by Young Yun Na on 07/05/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import <UIKit/UIKit.h>


@class MainViewController;
@interface PushInfoViewController : UIViewController
{
    MainViewController *parent;
    CGRect pushViewRect;
}
-(void)SetParent:(MainViewController *)_parent;

//상단
@property (strong, nonatomic) IBOutlet UILabel *lb_MerNm; //가맹점명
@property (strong, nonatomic) IBOutlet UILabel *lb_TRNS_ATON_1; //거래일시
@property (strong, nonatomic) IBOutlet UILabel *lb_TRNS_AMT; //거래금액
@property (strong, nonatomic) IBOutlet UIButton *bt_TRNS_STAT;
@property (weak, nonatomic) IBOutlet UIView *v_cancelLine;

//하단
@property (strong, nonatomic) IBOutlet UILabel *lb_SVC_CLSS_NM; //브랜드(거래구분)
@property (strong, nonatomic) IBOutlet UILabel *lb_TRNS_ATON; //거래일시
@property (strong, nonatomic) IBOutlet UILabel *lb_CARD_CO_AUTH_NO; //승인번호
@property (strong, nonatomic) IBOutlet UILabel *lb_MPAN_NO; //카드번호(마스킹)
@property (strong, nonatomic) IBOutlet UILabel *lb_INS_TRM; //할부개월수(2자리)
@property (strong, nonatomic) IBOutlet UILabel *lb_TRNS_UNIQ_NO; //거래고유번호
@property (strong, nonatomic) IBOutlet UILabel *lb_AFFI_CO_TRNS_UNIQ_NO; //은련만 노출
@property (strong, nonatomic) IBOutlet UILabel *lb_DC_BEF_AMT; //할인전금액 할인시만 노출
@property (strong, nonatomic) IBOutlet UILabel *lb_DC_AMT; //할인금액 할인시만 노출
@property (strong, nonatomic) IBOutlet UILabel *lb_DC_AFTR_AMT; //할인후금액 할인시만 노출
@property (strong, nonatomic) IBOutlet UILabel *lb_TRNS_STAT; //거래 상태 15:승인완료

@property (weak, nonatomic) IBOutlet UIView *v_voucher; //바우처 라인

@property (strong, nonatomic) IBOutletCollection(UIView) NSArray *v_discount;//할인정보 view Collection

- (IBAction)exitButton:(UIButton *)sender;
- (IBAction)vochButton:(UIButton *)sender;

@end
