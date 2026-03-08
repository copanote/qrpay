//
//  ViewController.m
//  mpm
//
//  Created by SugjinMac on 2018. 1. 30..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import "ViewController.h"
#import "AppDelegate.h"
#import "CommonUtil.h"
#import "Constant.h"
#import "NFilterNum.h"
#import "NFilterChar.h"
#import "QRKitRegViewController.h"
#import "QRKitReadViewController.h"
#import "PushInfoViewController.h"
#import "NSDictionary+Safe.h"

#define TIME_OUT 90

typedef void (^SuccessBlock)(id responseObject);
typedef BOOL (^FailureBlock)(id responseObject, NSError *error);

@interface ViewController () <WKUIDelegate, WKNavigationDelegate, NFilterCharDelegate, NFilterNumDelegate, NFilterToolbar2Delegate, NSURLSessionDelegate, NSURLSessionTaskDelegate, UIGestureRecognizerDelegate, NSURLConnectionDelegate> {
    BOOL mbShowPushAlert;
    BOOL mbPushCheckOnce;
    BOOL mbVersionCheckOnce;
    
    NSString *updateUrl;
    NSString *mainPageUrl;
    NSString *token;
    NSString *custId;
    
    NSString *nFilterPublicKey;
    NSString *nFilterMode;
    NSString *nFilterName;
    NSString *nFilterMaxLen;
    NSString *nFilterDesc;
    // edited by ksmartech, nFilter뒤의 뷰를 들어올릴지 여부
    NSString *nFilterUpYn;
    
    NSString *qrBase64Str;
    NSString *merNm;
    
    QRShareViewController *qrShareView;
    QRKitRegViewController *qrKitRegView;
    QRKitReadViewController *qrKitReadView;
    
    NSString *prevUrl;
}

@property (nonatomic, strong) UIActivityIndicatorView *activityView;
@property NFilterNum *numPad;
@property NFilterChar *charPad;
@property BOOL isCustomKeypad;
@property BOOL isSupportLandscape;
@property BOOL isCloseKeypad;
@property BOOL Authenticated;
@property NSURLRequest *FailedRequest;
@property BOOL fromWebview;
@property SuccessBlock onSuccess;
@property FailureBlock onFailure;
@property NSString *host;
@property UINavigationBar *navBar;
@property (nonatomic) WKWebView *webView;
@property (weak, nonatomic) IBOutlet UIView *webContainer;
@property WKProcessPool *processPool;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    mbPushCheckOnce = FALSE;
    mbVersionCheckOnce = FALSE;
    mbShowPushAlert = FALSE;
    _Authenticated = TRUE;
    _fromWebview = FALSE;
    _onSuccess = nil;
    _onFailure = nil;
    
    NSLog(@"model : %@", [CommonUtil getDeviceModel]);
    
    self.definesPresentationContext = YES;
    
    int indicatorHeight;
    if ([[CommonUtil getDeviceModel] isEqualToString:@"iPhone X"]) {
        indicatorHeight = 44;
    }else {
        indicatorHeight = 20;
    }
    
    UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, indicatorHeight)];
    label.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:label];
    
    // 상단 네비게이션 바
    _navBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 46)];
    _navBar.backgroundColor = [UIColor whiteColor];
    
    UINavigationItem *navItem = [[UINavigationItem alloc] init];
    //navItem.title = @"Navigation Bar title here";
    
    UIBarButtonItem *leftButton = [[UIBarButtonItem alloc] initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(goBack)];
    navItem.leftBarButtonItem = leftButton;
    
    _navBar.items = @[ navItem ];
    
    _isCustomKeypad = NO;
    _isSupportLandscape = NO;
    _isCloseKeypad = YES;
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)goBack {
    NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@", prevUrl] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    self.processPool = [[WKProcessPool alloc] init];
    
    if (_webContainer.subviews.count == 0) {
        CGRect rect = CGRectMake(0, 0, self.webContainer.bounds.size.width, self.webContainer.bounds.size.height);
        
        WKWebViewConfiguration *configuration = [[WKWebViewConfiguration alloc] init];
        configuration.processPool = self.processPool;
//        self.webView = [[WKWebView alloc] initWithFrame:rect];
        self.webView = [[WKWebView alloc] initWithFrame:rect configuration:configuration];
        self.webView.scrollView.bounces = false;
        self.webView.UIDelegate = self;
        self.webView.navigationDelegate = self;
        self.webView.opaque = false;
        self.webView.backgroundColor = UIColor.whiteColor;
        self.webView.scrollView.backgroundColor = UIColor.whiteColor;
        [self.webContainer addSubview:self.webView];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(webviewTouchEvent)];
        [tap setNumberOfTapsRequired:1];
        [tap setDelegate:self];
        
        [self.webView addGestureRecognizer:tap];
    }
    
    if (mbVersionCheckOnce == FALSE) {
        BOOL noConnection = ([CommonUtil isNetworkConnection] || [CommonUtil isWifiConnection]) ? NO : YES;
        if (noConnection) {
            [self showAlertWithTitle:TITLE_INFO message:MSG_NOT_CONNECT_NETWORK type1:ALERT_TYPE_OK];
        }else {
            [self reqAppVersionCheck];  //앱버전 체크
        }
        mbVersionCheckOnce = TRUE;
    }
    
    if (mbPushCheckOnce == FALSE) {
        [self checkPushAllow];
        mbPushCheckOnce = TRUE;
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        [userDefault setObject:@"Y" forKey:@"secondAction"];
        [userDefault synchronize];
    }
}

- (void)webviewTouchEvent {
    if (!self.numPad.view.isHidden) {
        [self hideNumpad];
    }
    
    if (self.charPad != nil) {
        [self.charPad closeKeypad];
    }
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    return YES;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    if ([self.webView isLoading]) {
        [self.webView stopLoading];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)pushReceiveCheck {
    if (self.webView != nil) {
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        NSString *is_push = [userDefault stringForKey:@"is_push"];
        if ((is_push != nil) && [is_push isEqualToString:@"Y"]){
            [userDefault setObject:@"N" forKey:@"is_push"];
            [userDefault synchronize];
            NSString *pushUrl = [userDefault stringForKey:@"pushUrl"];
            NSLog(@"pushUrl %@",pushUrl);
            [self moveUrl:pushUrl withParam:nil];
        }
    }
}

- (void)checkPushAllow {
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *pushAllow = [userDefault stringForKey:@"pushAllow"];
    if ((pushAllow != nil) && ![pushAllow isEqualToString:@""]) {
        //앱이 두번째 실행될 때부터 앱 푸시 설정 체크를 하도록 한다.
        NSString *secondAction = [userDefault stringForKey:@"secondAction"];
        if ((secondAction != nil) && [secondAction isEqualToString:@"Y"]) {
            //앱 설정에서 PUSH 설정 값 읽어오기
            UIUserNotificationType types = [[[UIApplication sharedApplication] currentUserNotificationSettings] types];
            BOOL pushEnabled = (types & UIUserNotificationTypeAlert);
            NSLog(@"push : %d", pushEnabled);
            if (!pushEnabled) {
                [self showAlertWithTitle:TITLE_INFO message:MSG_PUSH_ALLOW type1:ALERT_TYPE_PUSH_SETTING];
            }
        }
    }
}

- (void)moveUrl:(NSString *)_url withParam:(NSString *)_param {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSURL *url;
    if (_param != nil) {
        url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@%@?%@", appDelegate.currentServerURL, _url, _param] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
    }else {
        url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@%@", appDelegate.currentServerURL, _url] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
    }
    
    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:url];
    
    [self.webView loadRequest:request];
}

- (void)postUrl:(NSString *)_url withParam:(NSString *)_param {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@%@", appDelegate.currentServerURL, _url] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL: url];
    [request setHTTPMethod: @"POST"];
    [request setHTTPBody: [_param dataUsingEncoding: NSUTF8StringEncoding]];
    
    [self.webView loadRequest: request];
}

- (void)showAlertWithTitle:(NSString *)title message:(NSString *)message type1:(NSInteger)type {
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:title
                                  message:message
                                  preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *btn1;
    if (type == ALERT_TYPE_OK) {
        btn1 = [UIAlertAction
                actionWithTitle:BTN_OK
                style:UIAlertActionStyleDefault
                handler:^(UIAlertAction * action) {
            [alert dismissViewControllerAnimated:YES completion:nil];
            exit(0);
        }];
        [alert addAction:btn1];
    }else if (type == ALERT_TYPE_FORCE_UPDATE) {
        btn1 = [UIAlertAction
                actionWithTitle:BTN_MOVE
                style:UIAlertActionStyleDefault
                handler:^(UIAlertAction * action) {
            [alert dismissViewControllerAnimated:YES completion:nil];
            [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:updateUrl]]];
        }];
        [alert addAction:btn1];
    }else if (type == ALERT_TYPE_UPDATE) {
        btn1 = [UIAlertAction
                actionWithTitle:BTN_UPDATE
                style:UIAlertActionStyleDefault
                handler:^(UIAlertAction * action) {
            [alert dismissViewControllerAnimated:YES completion:nil];
            [self.webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:updateUrl]]];
        }];
        UIAlertAction* btn2 = [UIAlertAction
                               actionWithTitle:BTN_CANCEL
                               style:UIAlertActionStyleDefault
                               handler:^(UIAlertAction * action) {
            [alert dismissViewControllerAnimated:YES completion:nil];
            [self moveUrl:LOGIN_URL withParam:nil];
        }];
        
        [alert addAction:btn1];
        [alert addAction:btn2];
    }else if (type == ALERT_TYPE_PUSH_SETTING) {
        mbShowPushAlert = TRUE;
        btn1 = [UIAlertAction
                actionWithTitle:BTN_CLOSE
                style:UIAlertActionStyleDefault
                handler:^(UIAlertAction * action) {
            mbShowPushAlert = FALSE;
            [alert dismissViewControllerAnimated:YES completion:nil];
        }];
        UIAlertAction* btn2 = [UIAlertAction
                               actionWithTitle:BTN_SETTING
                               style:UIAlertActionStyleDefault
                               handler:^(UIAlertAction * action) {
            mbShowPushAlert = FALSE;
            [alert dismissViewControllerAnimated:YES completion:nil];
            if (UIApplicationOpenSettingsURLString != NULL) {
                NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                [[UIApplication sharedApplication] openURL:url];
            }
        }];
        
        [alert addAction:btn1];
        [alert addAction:btn2];
    }
    
    [self presentViewController:alert animated:YES completion:nil];
}

//앱 버전을 체크하고, 업데이트가 없으면 메인페이지 이동
- (void)reqAppVersionCheck {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSString * urlStr = [NSString stringWithFormat:@"%@%@", appDelegate.currentServerURL, IF_SERID_APP_INTRO];
    NSString *appVer = [[[NSBundle mainBundle] infoDictionary] stringForKey:@"CFBundleShortVersionString"];
    NSDictionary* dicParam = @{@"DEVI_TYPE"   : DEVI_TYPE,
                               @"APP_VERSION"    : appVer};
    
    [self getUrl:urlStr
      parameters:dicParam
         success:^(id responseObject) {
        NSDictionary  *mpmDict = [responseObject objectForKey:@"MPM"];
        NSLog(@"mpmDict : %@", mpmDict);
        NSDictionary *msgDic = [mpmDict objectForKey:@"msg"];
        NSDictionary *versionDic = [msgDic objectForKey:@"version"];
        NSString *APP_URL = [versionDic stringForKey:@"APP_URL"];
        NSString *FORCE_UPDATE_YN = [versionDic stringForKey:@"FORCE_UPDATE_YN"];
        NSString *UPDATE_YN = [versionDic stringForKey:@"UPDATE_YN"];
        
        //update check
        if ([UPDATE_YN isEqualToString:@"Y"]) {
            updateUrl = APP_URL;
            //check force update
            if ([FORCE_UPDATE_YN isEqualToString:@"Y"]) {
                [self showAlertWithTitle:TITLE_INFO message:MSG_UPDATE_FORCE type1:ALERT_TYPE_FORCE_UPDATE];
            }else {
                //normal update
                [self showAlertWithTitle:TITLE_INFO message:MSG_UPDATE_FORCE type1:ALERT_TYPE_UPDATE];
            }
            
        }else {
            //move to main page
            NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
            NSString *saveQRBase64Str = [userDefault stringForKey:@"saveQRBase64Str"];
            if (saveQRBase64Str != nil && [saveQRBase64Str length] > 0) {
                NSString *saveMerNm = [userDefault stringForKey:@"merNm"];
                NSString *param = [NSString stringWithFormat:@"qr=%@&merNm=%@", saveQRBase64Str, saveMerNm];
                // edited by ksmartech
                //[self moveUrl:SUBMAIN_URL withParam:param];
                [self postUrl:SUBMAIN_URL withParam:param];
            }else {
                [self moveUrl:MAIN_URL withParam:nil];
            }
            
            [self pushReceiveCheck];
        }
        
    } failure:^BOOL(id responseObject, NSError *error) {
        NSLog(@"error : %@", error);
        [self showAlertWithTitle:TITLE_INFO message:MSG_NOT_CONNECT_NETWORK type1:ALERT_TYPE_OK];
        return NO;
    }];
}

- (void)reqAppDeviceUpdate {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSString * urlStr = [NSString stringWithFormat:@"%@%@", appDelegate.currentServerURL, IF_SERID_APP_DEVICE_UPDATE];
    
    if ((token != nil) && ![token isEqualToString:@""]) {
        NSDictionary* dicParam = @{
            @"DEVI_TYPE"     : token,       //로그인키
            @"DEVI_VAL"      : DEVI_TYPE,   // 앱 타입(A:안드로이드, I:IOS)
            @"DEVI_ID"       : [[UIDevice currentDevice] systemVersion],    // OS버전
            @"AFFI_CO_ID"    : @"BCQRCPAY",  // 가맹점명
        };
        
        [self getUrl:urlStr
          parameters:dicParam
             success:^(id responseObject) {
            NSLog(@"responseObject : %@", responseObject);
        } failure:^BOOL(id responseObject, NSError *error) {
            return NO;
        }];
    }
}

- (void)timerMethod {
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    if ([self.webView isLoading]) {
        [self.webView stopLoading];
    }
    [self showAlertWithTitle:TITLE_INFO message:MSG_NOT_CONNECT_NETWORK type1:ALERT_TYPE_OK];
}

// =============================================================================
#pragma mark - WKNavigationDelegate

- (void)callJS:(NSString *)js {
    [self.webView evaluateJavaScript:js completionHandler:^(id result, NSError *error) {
        NSLog(@"JSCall : %@, result : %@, error: %@", js, result, error);
    }];
}

-(void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler {
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"알림" message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"확인" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        completionHandler();
    }];
    [alert addAction:action];
    [self presentViewController:alert animated:YES completion:nil];
}

-(void)webView:(WKWebView *)webView runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(BOOL))completionHandler {
    
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"알림" message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"확인" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        completionHandler(YES);
    }];
    [alert addAction:action];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"취소" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        completionHandler(NO);
    }];
    
    [alert addAction:cancelAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    NSLog(@"webViewDidStartLoad");
    [self showIndicator];
    [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"webViewDidFinishLoad");
    // 외부 url 진입 시 back 버튼이 있는 네비게이션 바 노출
    
    NSURL *url = webView.URL;
    if (/*휴대폰 인증 시에만*/ [url.host isEqualToString:@"nice.checkplus.co.kr"]) {
        [self.webView addSubview:_navBar];
    }else if (![[url absoluteString] isEqualToString:@"about:blank"]) {
        [_navBar removeFromSuperview];
    }
    
    // 롱터치 방지용 코드
    NSString * jsCallBack = @"window.getSelection().removeAllRanges();";
    
    [self callJS:jsCallBack];
    [self callJS:@"document.documentElement.style.webkitUserSelect='none';"];
    [self callJS:@"document.documentElement.style.webkitTouchCallout='none';"];
    
    [self hideIndicator];
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"webView didFailLoadWithError failed: %@", error);
    NSLog(@"webView didFailLoadWithError failed code : %d", [error code]);
    
    if ([error code] == NSURLErrorSecureConnectionFailed) {
        NSLog(@"webView retry to Authenticate...");
        _fromWebview = true;
        
        [NSObject cancelPreviousPerformRequestsWithTarget:self];
        
        _Authenticated = false;
        [self showIndicator];
        [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
        [[NSURLConnection alloc] initWithRequest:_FailedRequest delegate:self];
    }
}

- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction preferences:(WKWebpagePreferences *)preferences decisionHandler:(void (^)(WKNavigationActionPolicy, WKWebpagePreferences * _Nonnull))decisionHandler  API_AVAILABLE(ios(13.0)){
    
    preferences.preferredContentMode = WKContentModeMobile;
    NSLog(@"PRE %@", preferences);
    
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSURL *url = navigationAction.request.URL;
    NSLog(@"url(webView) : %@", url);
    NSLog(@"scheme : %@", [url scheme]);
    NSString *myScheme = @"appto";
    
    //카드인증에서 닫기 누르면 about:blank 내려오는데 마지막 주소로 이동하기 위한 코드
    if ([[url absoluteString] hasPrefix:appDelegate.currentServerURL]) {
        prevUrl = [url absoluteString];
    }else if ([[url absoluteString] isEqualToString:@"about:blank"] && [_host isEqualToString:@"tcard.ok-name.co.kr"]) {
        NSURL *url2 = [NSURL URLWithString:[[NSString stringWithFormat:@"%@", prevUrl] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
        [self.webView loadRequest:[NSURLRequest requestWithURL:url2]];
    }
    
    if ([url.scheme isEqualToString:myScheme]) {
        [self javaInterfaceTo:webView with:url];
        decisionHandler(WKNavigationActionPolicyCancel, preferences);
        return;
        
    }else if ([url.scheme isEqualToString:@"https"]) {
        _host = url.host;
        
        BOOL result = _Authenticated;
        _FailedRequest = [[NSURLRequest alloc] initWithURL:url];
        
        if (!_Authenticated) {
            NSLog(@"Try to authenticate...");
            _fromWebview = YES;
            _onFailure = nil;
            _onSuccess = nil;
            [self showIndicator];
            [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
        }
        
        decisionHandler((result ? WKNavigationActionPolicyAllow : WKNavigationActionPolicyCancel), preferences);
        return;
    }
    
    // tel link 동작 추가
    if ([url.scheme isEqualToString:@"tel"]){
        [UIApplication.sharedApplication openURL:navigationAction.request.URL];
        decisionHandler(WKNavigationActionPolicyCancel, preferences);
        return;
    }
    
    decisionHandler(WKNavigationActionPolicyAllow, preferences);
    return;
}

- (void)javaInterfaceTo:(WKWebView *)webView with:(NSURL *)url {
    NSString *requestString = [url absoluteString];
    NSArray *components = [requestString componentsSeparatedByString:@"://"];
    NSString *functionName = [components objectAtIndex:1];
    
    if ([functionName hasPrefix:@"setAppAuthKey"]) {
        NSArray *subComponents = [functionName componentsSeparatedByString:@"?"];
        NSString *parameters = [subComponents objectAtIndex:1];
        NSArray *params = [parameters componentsSeparatedByString:@"&"];
        mainPageUrl = params[0];
        token = params[1];
        custId = params[2];
        
        [self moveUrl:mainPageUrl withParam:nil];
    }else if ([functionName hasPrefix:@"nfilter"]) {
        NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setTimeZone:[NSTimeZone timeZoneWithName:@"Asia/Seoul"]];
        [dateFormatter setDateFormat:@"yyyy"];
        int year = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        [dateFormatter setDateFormat:@"MM"];
        int month = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        [dateFormatter setDateFormat:@"dd"];
        int day = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        [dateFormatter setDateFormat:@"HH"];
        int hour = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        [dateFormatter setDateFormat:@"mm"];
        int min = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        [dateFormatter setDateFormat:@"ss"];
        
        int sec = [[dateFormatter stringFromDate:[NSDate date]] intValue];
        
        NSString* resultStringSet = nFilterPublicKey;
        NSString *path = [NSTemporaryDirectory()
                          stringByAppendingPathComponent:[NSString stringWithFormat:@"result %d.%d.%d %d:%d:%d.txt",year, month, day,hour,min,sec]];
        [resultStringSet writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil];
        
        
        NSArray *subComponents = [functionName componentsSeparatedByString:@"?"];
        NSString *parameters = [subComponents objectAtIndex:1];
        NSArray *params = [parameters componentsSeparatedByString:@"publicKey="];
        nFilterPublicKey = params[1];
        NSLog(@"nFilter server public key : %@ path : %@", nFilterPublicKey ,path);
    }else if ([functionName hasPrefix:@"showNFilterKeypad"]) {
        NSArray *subComponents = [functionName componentsSeparatedByString:@"?"];
        NSString *parameters = [subComponents objectAtIndex:1];
        NSArray *params = [parameters componentsSeparatedByString:@"&"];
        
        // edited by ksmartech
        nFilterUpYn = @"Y";
        
        for (NSString *param in params) {
            NSArray *paramArr = [param componentsSeparatedByString:@"="];
            
            if ([paramArr[0] isEqualToString:@"mode"]) {
                //eng(영문 자판) or num(숫자 자판)  mode=eng
                nFilterMode = paramArr[1];
            }else if ([paramArr[0] isEqualToString:@"name"]) {
                //콜백함수 파라미터 name=EPASWD_ENCRYPT
                nFilterName = paramArr[1];
            }else if ([paramArr[0] isEqualToString:@"len"]) {
                //max length    len=6
                nFilterMaxLen = paramArr[1];
            }else if ([paramArr[0] isEqualToString:@"desc"]) {
                //타이틀   desc=%EB%B9%84%EB%B0%80%EB%B2%88%ED%98%B8
                nFilterDesc = paramArr[1];
            }else if ([paramArr[0] isEqualToString:@"upYn"]) {
                // edited by ksmartech
                //nfilter 위의 뷰를 들어올릴지 여부
                nFilterUpYn = paramArr[1];
                NSLog(@"upYn : %@", nFilterUpYn );
            }
        }
        
        if ([nFilterMode isEqualToString:@"eng"]) {
            //show nFilter Keypad
            [self showCharKeyForViewMode];
        }else {
            // edited by ksmartech
            [self showNumKeyForViewMode:YES];
        }
        NSString *js = [NSString stringWithFormat:@"showNFilterKeypadCallBack('', '%@', '')", nFilterName];
        [self callJS:js];
        
    }else if ([functionName hasPrefix:@"hideNFilterKeypad2"]) {
        if (self.numPad != nil) {
            [self hideNumpad];
        }
        if (self.charPad != nil) {
            [self.charPad closeKeypad];
        }
        
        [self callJS:@"hideNFilterKeypadCallBack();"];
        
    }else if ([functionName hasPrefix:@"qrShare"]) {
//        NSArray *subComponents = [functionName componentsSeparatedByString:@"?"];
        
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        qrShareView = (QRShareViewController *) [storyboard instantiateViewControllerWithIdentifier:@"QRShareView"];
        qrShareView.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
        qrShareView.modalPresentationStyle = UIModalPresentationOverCurrentContext;
        NSString *urlString = [functionName substringFromIndex:[functionName rangeOfString:@"?"].location + 1];
        NSLog(@"urlString : %@", urlString );
        [qrShareView SetQRShareUrl:urlString];
        [qrShareView setProcessPool:self.processPool];
        [self presentViewController:qrShareView animated:YES completion:nil];
    }else if ([functionName hasPrefix:@"getDevice"]) {
        NSString *deviceType = @"I";
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        NSString *pushToken = [userDefault stringForKey:@"pushToken"];
        UIDevice *device = [UIDevice currentDevice];
        NSString *appVer = [[[NSBundle mainBundle] infoDictionary] stringForKey:@"CFBundleShortVersionString"];
        NSString *deviceId = [CommonUtil checkDeviceKey];
        
        NSMutableDictionary* outterJson   = [NSMutableDictionary new];
        [outterJson setValue:deviceType forKey:@"DEVI_TYPE"];
        [outterJson setValue:pushToken forKey:@"DEVI_VAL"];
        [outterJson setValue:[CommonUtil getDeviceModel] forKey:@"MODL_NM"];
        [outterJson setValue:[device systemName] forKey:@"MOBIL_OS_NM"];
        [outterJson setValue:deviceId forKey:@"DEVI_ID"];
        [outterJson setValue:appVer forKey:@"APP_VER"];
        //결과값 NSData타입
        NSData* nsData = [NSJSONSerialization dataWithJSONObject:outterJson options:NSJSONWritingPrettyPrinted error:nil];
        
        //결과값 NSString타입
        NSString* nsString = [[NSString alloc] initWithData:nsData encoding:NSUTF8StringEncoding];
        
        NSLog(@"nsString :::: %@", nsString);
        
        NSString *js = [NSString stringWithFormat:@"setDevice(%@)", nsString];
        NSLog(@"js :::: %@", js);
        
        [self callJS:js];
        
    }else if ([functionName hasPrefix:@"qrLoad"]) {
        // qrLoad?qr=!;^qrBase64String!;^&merNm=가맹점명
        NSArray *subComponents = [functionName componentsSeparatedByString:@"?"];
        NSString *parameters = [subComponents objectAtIndex:1];
        NSLog(@"parameters : %@", parameters);
        NSArray *params = [parameters componentsSeparatedByString:@"!;%5E"];
        NSLog(@"params : %@", params[1]);
        
        //qr image base64 string
        qrBase64Str = params[1];
        NSLog(@"qrBase64Str : %@", qrBase64Str);
        
        if ([qrBase64Str length] > 0) {
            NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
            [userDefault setObject:qrBase64Str forKey:@"saveQRBase64Str"];
            [userDefault synchronize];
        }
        
        //가맹점명
        NSArray *param2 = [parameters componentsSeparatedByString:@"merNm="];
        merNm = param2[1];
        NSLog(@"merNm : %@", merNm);
        NSString *decoded = [merNm stringByRemovingPercentEncoding];
        NSLog(@"decoded string :\n%@", decoded);
        
        if ([merNm length] > 0) {
            NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
            [userDefault setObject:merNm forKey:@"merNm"];
            [userDefault synchronize];
        }
    }else if ([functionName hasPrefix:@"qrClear"]) {
        NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
        [userDefault removeObjectForKey:@"saveQRBase64Str"];
        [userDefault removeObjectForKey:@"merNm"];
        [userDefault synchronize];
        
    }else if ([functionName hasPrefix:@"goPolicyTreatment"]) {
        NSURL *url = [NSURL URLWithString:PRIVACY_POLICY_TREATMENT];
        if ([[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url];
        }
    }else if ([functionName hasPrefix:@"getDecalCode"]) {
        NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
        [center addObserver:self selector:@selector(resQRKitData:) name:@"resQrkitData" object:nil];
        
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        qrKitRegView = (QRKitRegViewController *) [storyboard instantiateViewControllerWithIdentifier:@"QRKitRegViewController"];
        qrKitRegView.modalPresentationStyle = UIModalPresentationFullScreen;
        
        [self presentViewController:qrKitRegView animated:YES completion:nil];
        
    }else if ([functionName hasPrefix:@"getTrnsData"]) {
        NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
        [center addObserver:self selector:@selector(resQRReadData:) name:@"resQRReadData" object:nil];
        
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        qrKitReadView = (QRKitReadViewController *) [storyboard instantiateViewControllerWithIdentifier:@"QRKitReadViewController"];
        qrKitReadView.modalPresentationStyle = UIModalPresentationFullScreen;
        
        [self presentViewController:qrKitReadView animated:YES completion:nil];
    }else if ([functionName hasPrefix:@"linkExtraBrowser"]) {
        NSArray *params = [requestString componentsSeparatedByString:@"url="];
        NSURL *webUrl = [[NSURL alloc] initWithString:params[1]];
        
        if ([[UIApplication sharedApplication] canOpenURL:webUrl]) {
            [[UIApplication sharedApplication] openURL:webUrl];
        }
    }
}

- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSURL *url = navigationAction.request.URL;
    NSLog(@"url(webView) : %@", url);
    NSLog(@"scheme : %@", [url scheme]);
    NSString *myScheme = @"appto";
    
    //카드인증에서 닫기 누르면 about:blank 내려오는데 마지막 주소로 이동하기 위한 코드
    if ([[url absoluteString] hasPrefix:appDelegate.currentServerURL]) {
        prevUrl = [url absoluteString];
    }else if ([[url absoluteString] isEqualToString:@"about:blank"] && [_host isEqualToString:@"tcard.ok-name.co.kr"]) {
        NSURL *url2 = [NSURL URLWithString:[[NSString stringWithFormat:@"%@", prevUrl] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
        [self.webView loadRequest:[NSURLRequest requestWithURL:url2]];
    }
    
    if ([url.scheme isEqualToString:myScheme]) {
        [self javaInterfaceTo:webView with:url];
        decisionHandler(WKNavigationActionPolicyCancel);
        return;
        
    }else if ([url.scheme isEqualToString:@"https"]) {
        _host = url.host;
        
        BOOL result = _Authenticated;
        _FailedRequest = [[NSURLRequest alloc] initWithURL:url];
        
        if (!_Authenticated) {
            NSLog(@"Try to authenticate...");
            _fromWebview = YES;
            _onFailure = nil;
            _onSuccess = nil;
            [self showIndicator];
            [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
        }
        
        decisionHandler(result ? WKNavigationActionPolicyAllow : WKNavigationActionPolicyCancel);
        return;
    }
    
    // tel link 동작 추가
    if ([url.scheme isEqualToString:@"tel"]){
        [UIApplication.sharedApplication openURL:navigationAction.request.URL];
        decisionHandler(WKNavigationActionPolicyCancel);
        return;
    }

    decisionHandler(WKNavigationActionPolicyAllow);
    return;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    NSLog(@"NSURLConnection didReceiveResponse...");
    
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    if (_Authenticated == NO) {
        NSLog(@"NSURLConnection authentication complete...");
        _Authenticated = YES;
        if (_fromWebview) {
            [connection cancel];
            [self showIndicator];
            [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
            [self.webView loadRequest:_FailedRequest];
        }
        return;
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    NSLog(@"NSURLConnection didReceiveData...");
    
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    
    if (_Authenticated) {
        if (_onSuccess) {
            NSLog(@"NSURLConnection didReceiveData... 1");
            NSDictionary * responseObject = nil;
            
            BOOL isSpace = [data isEqualToData:[NSData dataWithBytes:" " length:1]];
            
            NSError *serializationError = nil;
            if (data.length > 0 && !isSpace) {
                responseObject = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&serializationError];
            } else {
                //return nil;
            }
            
            if (_onSuccess) {
                [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
                    _onSuccess(responseObject);
                    _onSuccess = nil;
                    _onFailure = nil;
                }];
            }
        }
    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    NSLog(@"NSURLConnection connectionDidFinishLoading...");
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    NSLog(@"NSURLConnection Failed with error : %@",error);
    if ([error code] == NSURLErrorSecureConnectionFailed) {
        NSLog(@"NSURLConnection retry to authenticate...");
        
        [NSObject cancelPreviousPerformRequestsWithTarget:self];
        _Authenticated = false;
        [self showIndicator];
        [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
        [[NSURLConnection alloc] initWithRequest:_FailedRequest delegate:self];
        
    }else {
        if (_Authenticated && error)
            NSLog(@"Error at getUrl : %@",[error description]);
        if (_onFailure) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
                _onFailure(nil, error);
                _onFailure = nil;
                _onSuccess = nil;
            }];
        }
    }
}

- (BOOL)connection:(NSURLConnection *)connection
canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace {
    return [protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust];
}

- (void)connection:(NSURLConnection *)connection
didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
    NSLog(@"NSURLConnection didReceiveAuthenticationChallenge...");
    NSLog(@"NSURLConnection try to authenticate...");
    _Authenticated = NO;
    NSURLCredential *credential =
    [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust];
    
    [challenge.sender useCredential:credential forAuthenticationChallenge:challenge];
}

- (void)showNumKeyForViewMode:(BOOL) isSerial {
    // edited by mkpark
    [[UIApplication sharedApplication] sendAction:@selector(resignFirstResponder) to:nil from:nil forEvent:nil];
    ////
    
    if (self.numPad != nil) {
        [self.numPad.view removeFromSuperview];
        self.numPad = nil;
    }
    
    if (self.charPad != nil) {
        [self.charPad.view removeFromSuperview];
        self.charPad = nil;
    }
    
    /////////////////////////////////////////////////////////////
    // NFilter 숫자 키패드 생성
    self.numPad = [[NFilterNum alloc] initWithNibName:@"NFilterNum" bundle:nil];     // 일반넘패드
    
    self.numPad.useInitialVector = YES;
    //[self.numPad setServerPublickey:@"MDIwGhMABBYCBEsAMWHtqFKFE9xK+8OWdHVjeXSQBBTlmbbw1STxAJoZXHDu2Uyj8drXTg=="];   // 더미용 공개키입니다 자사의 공개키로 바
    [self.numPad setServerPublickey:nFilterPublicKey];
    [self.numPad setCallbackMethod:self
                   methodOnConfirm:@selector(onConfirmNFilter:encText:dummyText:tagName:)
                      methodOnPrev:@selector(onPrevNFilter:encText:dummyText:tagName:)
                      methodOnNext:@selector(onNextNFilter:encText:dummyText:tagName:)
                     methodOnPress:@selector(onPressNFilter:encText:dummyText:tagName:)
                 methodOnReArrange:@selector(onReArrangeNFilter)
     ];
    [self.numPad setCloseCallbackMethod:self methodOnClose:@selector(onCloseNFilter:encText:dummyText:tagName:)];
    [self.numPad setLengthWithTagName:@"encdata1" length:[nFilterMaxLen intValue]];
    [self.numPad setFullMode:NO];
    
    [self.numPad setSupportBackgroundEvent:YES];
    [self.numPad setSupportBackGroundClose:YES];
    [self.numPad setIsSerialMode:isSerial];
    [self.numPad setKeyPadBackground:[UIColor lightGrayColor]];
    [self.numPad setUseVoiceOverViaSpreaker:YES];
    self.numPad.supportViewRotatation = _isSupportLandscape;
    [self.numPad setAllowCloseKeypadConfirmPressed:_isCloseKeypad];
    self.numPad.bezelLengthOfKeyPad = 10;
    [self.numPad setNoSound:NO];
    [self.numPad setBottomMaginForIPhoneX:40];
    [self.numPad setMaginForIPhoneX:40];
    
    if (_isCustomKeypad == YES)
        self.numPad.delegate = self;
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7) {
        [self.numPad setVerticalFrame:0];
    }else {
        [self.numPad setVerticalFrame:20];
    }
    
    if (isSerial)
        self.numPad.toolbar2 = [self createNFilterToolbarForNumSerial];
    else
        self.numPad.toolbar2 = [self createNFilterToolbarForNumRandom];
    
    self.numPad.toolbar2.delegate = self;
    self.numPad.emptyImage = [UIImage imageNamed:@"ios_btn_num_empty.png"];
    self.numPad.view.hidden = true;
    // edited by ksmartech
    [self showNumpad];
    ////
    
    NSLog(@"view : %f", self.view.frame.size.width);
    NSLog(@"view : %f", self.view.frame.size.height);
    NSLog(@"keypad : %f", self.numPad.viwNumbPad.frame.origin.x);
    NSLog(@"keypad : %f", self.numPad.viwNumbPad.frame.origin.y);
    NSLog(@"keypad : %f", self.numPad.viwNumbPad.frame.size.width);
    NSLog(@"keypad : %f", self.numPad.viwNumbPad.frame.size.height);
    NSLog(@"keypad : %f", [self.numPad keyPadHeight]);
}

- (void)showCharKeyForViewMode {
    if (self.numPad != nil) {
        [self.numPad.view removeFromSuperview];
        self.numPad = nil;
    }
    
    if (self.charPad != nil) {
        [self.charPad.view removeFromSuperview];
        self.charPad = nil;
    }
    
    self.charPad = [[NFilterChar alloc] initWithNibName:@"NFilterChar" bundle:nil];
    self.charPad.useInitialVector = YES;
    //[self.charPad setServerPublickey:@"MDIwGhMABBYCBEsAMWHtqFKFE9xK+8OWdHVjeXSQBBTlmbbw1STxAJoZXHDu2Uyj8drXTg=="];   // 더미용 공개키입니다 자사의 공개키로 바꿔주세요
    [self.charPad setServerPublickey:nFilterPublicKey];
    [self.charPad setCallbackMethod:self
                    methodOnConfirm:@selector(onConfirmNFilter:encText:dummyText:tagName:)
                       methodOnPrev:@selector(onPrevNFilter:encText:dummyText:tagName:)
                       methodOnNext:@selector(onNextNFilter:encText:dummyText:tagName:)
                      methodOnPress:@selector(onPressNFilter:encText:dummyText:tagName:)
                  methodOnReArrange:@selector(onReArrangeNFilter)
     ];
    [self.charPad setCloseCallbackMethod:self methodOnClose:@selector(onCloseNFilter:encText:dummyText:tagName:)];
    
    [self.charPad setLengthWithTagName:@"encdata2" length:[nFilterMaxLen intValue]];
    [self.charPad setFullMode:NO];
    [self.charPad setNoPadding:NO];
    [self.charPad setSupportBackgroundEvent:YES];
    [self.charPad setSupportBackGroundClose:YES];
    [self.charPad setSupportViewRotatation:_isSupportLandscape];
    [self.charPad setMasking:NFilterMaskingDefault];
    [self.charPad setAttachType:NFilterAttachViewController];
    [self.charPad setShowHanguleText:NO];
    [self.charPad setNFilterHeight:200];
    [self.charPad setDeepSecMode:NO];
    [self.charPad setUseVoiceOverViaSpreaker:YES];
    [self.charPad setAllowCloseKeypadConfirmPressed:_isCloseKeypad];
    [self.charPad setBottomMaginForIPhoneX:40];
    [self.charPad setMaginForIPhoneX:40];
    
    // 아이패드인 경우
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        [self.charPad setShowKeypadBubble:NO];
    }
    
    if (_isCustomKeypad == YES)
        self.charPad.delegate = self;
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7) {
        [self.charPad setVerticalFrame:0];
    }else {
        [self.charPad setVerticalFrame:20];
    }
    self.charPad.toolbar2 = [self createNFilterToolbarForChar];
    self.charPad.toolbar2.delegate = self;
    
    [self.charPad showKeypad:[UIApplication sharedApplication].statusBarOrientation parentViewController:self];
}

- (NFilterToolbar2 *)createNFilterToolbarForChar {
    NFilterToolbar2 *toolbar = [[NFilterToolbar2 alloc] initWithFrame:CGRectMake(0, 100, self.view.frame.size.width, 44)];
    toolbar.backgroundColor = UIColorFromRGB(0xebebeb);
    
    // 확인
    NFilterButton2 *toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    UIButton *btn = toolbarButton.button;
    [btn setTitle:@"확인" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:UIColorFromRGB(0xe73d44)] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeOK;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(4, 4, 4, 4);
    toolbarButton.dock = NFDockTypeRight;
    
    [toolbar addToolbarButton:toolbarButton];
    
    toolbar.align = NFilterToolbarAlignTop;
    return toolbar;
}

- (NFilterToolbar2 *)createNFilterToolbarForNumSerial {
    NFilterToolbar2 *toolbar = [[NFilterToolbar2 alloc] initWithFrame:CGRectMake(0, 100, self.view.frame.size.width, 56)];
    toolbar.backgroundColor = UIColorFromRGB(0xebebeb);
    
    // 재배열
    NFilterButton2 *toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    UIButton *btn = toolbarButton.button;
    [btn setTitle:@"재배열" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:[UIColor whiteColor]] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeReplace;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(7, 7, 0, 7);
    toolbarButton.dock = NFDockTypeLeft;
    
    [toolbar addToolbarButton:toolbarButton];
    
    // Delete
    toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    btn = toolbarButton.button;
    [btn setTitle:@"삭제" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:[UIColor whiteColor]] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeDelete;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(7, 7, 0, 7);
    toolbarButton.dock = NFDockTypeLeft;
    
    [toolbar addToolbarButton:toolbarButton];
    
    // 확인
    toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    btn = toolbarButton.button;
    [btn setTitle:@"확인" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:UIColorFromRGB(0xe73d44)] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeOK;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(7, 7, 7, 7);
    toolbarButton.dock = NFDockTypeRight;
    
    [toolbar addToolbarButton:toolbarButton];
    
    toolbar.align = NFilterToolbarAlignBottom;
    return toolbar;
}

- (NFilterToolbar2 *)createNFilterToolbarForNumRandom {
    NFilterToolbar2 *toolbar = [[NFilterToolbar2 alloc] initWithFrame:CGRectMake(0, 100, self.view.frame.size.width, 44)];
    toolbar.backgroundColor = UIColorFromRGB(0xebebeb);
    
    // 이전
    NFilterButton2 *toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    UIButton *btn = toolbarButton.button;
    [btn setTitle:@"이전" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:[UIColor whiteColor]] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypePrev;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(4, 4, 0, 4);
    toolbarButton.dock = NFDockTypeLeft;
    
    [toolbar addToolbarButton:toolbarButton];
    
    // 다음
    toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    btn = toolbarButton.button;
    [btn setTitle:@"다음" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor grayColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:[UIColor whiteColor]] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeNext;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(4, 4, 0, 4);
    toolbarButton.dock = NFDockTypeLeft;
    
    [toolbar addToolbarButton:toolbarButton];
    
    // 확인
    toolbarButton = [[NFilterButton2 alloc] initWithFrame:CGRectMake(0, 0, 80, 42)];
    btn = toolbarButton.button;
    [btn setTitle:@"확인" forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[CommonUtil imageFromColor:UIColorFromRGB(0xe73d44)] forState:UIControlStateNormal];
    toolbarButton.nFilterbuttonType = NFilterButtonTypeOK;
    toolbarButton.alignWithMargins = YES;
    toolbarButton.margins = NFMarginsMake(4, 4, 4, 4);
    toolbarButton.dock = NFDockTypeRight;
    
    [toolbar addToolbarButton:toolbarButton];
    
    toolbar.align = NFilterToolbarAlignTop;
    return toolbar;
}

#pragma mark -
#pragma mark NFilter UI 커스텀 함수
// 숫자키
- (void)onCustomizeButton:(UIButton *)button buttonIndex:(int)index reloadButton:(UIButton *)reloadButton deleteButton:(UIButton *)deleteButton {
    UIImage *image = [CommonUtil imageFromColor:[UIColor whiteColor]];
    [button setBackgroundImage:image forState:UIControlStateNormal];
    
    image = [CommonUtil imageFromColor:UIColorFromRGB(0xf9f9f9)];
    [button setBackgroundImage:image forState:UIControlStateHighlighted];
    
    [button setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
}

- (void)onCustomizeEmptyButton:(UIButton *)button {
    UIImage *image = [CommonUtil imageFromColor:[UIColor grayColor]];
    [button setBackgroundImage:image forState:UIControlStateNormal];
}

// 문자키
- (void)onCustomizeCharKeypadButton:(UIButton *)button {
    if (button.tag == 91) {     // shift 키
        UIImage *image = [CommonUtil imageFromColor:UIColorFromRGB(0xc7eeed)];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0x5DBCD2)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0xcaeced)];
        [button setBackgroundImage:image forState:UIControlStateSelected];
    }else if (button.tag == 38) {     // 삭제 키
        UIImage *image = [CommonUtil imageFromColor:UIColorFromRGB(0xe4f3f1)];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0x5DBCD2)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
    }else if (button.tag == 92) {     // 특수 문자 영문
        UIImage *image = [CommonUtil imageFromColor:UIColorFromRGB(0xe4f3f1)];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0x5DBCD2)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
    }else if (button.tag == 300) {   // 스페이스바
        UIImage *image = [CommonUtil imageFromColor:UIColorFromRGB(0xffecbb)];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0xe28a46)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
    }else if (button.tag == 301) {           // 재배열 버튼
        UIImage *image = [CommonUtil imageFromColor:UIColorFromRGB(0xe4f3f1)];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0x5DBCD2)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
    }else {      // 일반 버튼
        UIImage *image = [CommonUtil imageFromColor:[UIColor whiteColor]];
        [button setBackgroundImage:image forState:UIControlStateNormal];
        
        image = [CommonUtil imageFromColor:UIColorFromRGB(0xf9f9f9)];
        [button setBackgroundImage:image forState:UIControlStateHighlighted];
        
        [button setTitleColor:[UIColor lightGrayColor] forState:UIControlStateNormal];
    }
}

#pragma mark -
#pragma mark NFilter toolbar callback 함수

- (void)NFilterToolbarButtonClick:(NFilterButtonType)buttonType withButton:(UIButton *)button {
    if (self.numPad != nil) {
        if (buttonType == NFilterButtonTypeReplace)
            [self.numPad pressKeypadReload];
        else if (buttonType == NFilterButtonTypeOK)
            [self.numPad pressConfirm];
        else if (buttonType == NFilterButtonTypeDelete)
            [self.numPad pressBack];
        else if (buttonType == NFilterButtonTypeNext)
            NSLog(@"이전 작업 처리를 하세요.");
        else if (buttonType == NFilterButtonTypePrev)
            NSLog(@"다음 작업 처리를 하세요.");
    }else if (self.charPad != nil) {
        if (buttonType == NFilterButtonTypeNext)
            NSLog(@"이전 작업 처리를 하세요.");
        else if (buttonType == NFilterButtonTypeOK)
            [self.charPad pressConfirm];
        else if (buttonType == NFilterButtonTypePrev)
            NSLog(@"다음 작업 처리를 하세요.");
    }
}

#pragma mark -
#pragma mark NFilter 키패드 callback 함수
- (void)onReArrangeNFilter {
    
}

/*--------------------------------------------------------------------------------------
 엔필터 '이전' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onPrevNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"이전버튼 눌림");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
}

/*--------------------------------------------------------------------------------------
 엔필터 '다음' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onNextNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"다음버튼 눌림");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
}

/*--------------------------------------------------------------------------------------
 엔필터 '키' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onPressNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"엔필터 키눌림");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
    
    NSString *js = [NSString stringWithFormat:@"showNFilterKeypadCallBack('%@', '%@', '%@')", encText, nFilterName, dummyText];
    
    [self callJS:js];
}

/*--------------------------------------------------------------------------------------
 엔필터 '확인' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onConfirmNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"엔필터 닫힘");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
    
    NSString *js = [NSString stringWithFormat:@"closeNFilterKeypadCallBack()"];
    
    [self callJS:js];
    
    // allowConfirmPressed 속성이 NO여서 키패드가 안닫힐때 내려가게하고 싶으면 아래와 같이 closeKeypad를 호출하면 키패드가 내려갑니다.
    if (self.charPad != nil) {
        [self.charPad closeKeypad];
    }
    
    // edited by ksmartech
    [self hideNumpad];
    ////
}

/*--------------------------------------------------------------------------------------
 엔필터 '취소' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onCancelNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"엔필터 닫힘 : onCancelNFilter");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
    
    // edited by ksmartech
    [self hideNumpad];
}


/*--------------------------------------------------------------------------------------
 엔필터 '취소' 버튼 눌렀을 때 발생하는 콜백함수
 ---------------------------------------------------------------------------------------*/
- (void)onCancel {
    NSLog(@"엔필터 닫힘");
    // edited by ksmartech
    [self hideNumpad];
}

/*--------------------------------------------------------------------------------------
 엔필터 'Background Close'동작할때 발생하는 콜백 함수
 ---------------------------------------------------------------------------------------*/
- (void)onCloseNFilter:(NSString *)secureText encText:(NSString *)encText dummyText:(NSString *)dummyText tagName:(NSString *)tagName {
    NSLog(@"엔필터 닫힘 : onCloseNFilter");
    NSLog(@"태그: %@", tagName);
    NSLog(@"암호문 : %@", secureText);
    NSLog(@"더미: %@", dummyText);
    NSLog(@"서버에 보낼 암호문: %@", encText);
    
    // edited by ksmartech
    [self hideNumpad];
}

// edited by ksmartech
- (void)showNumpad {
    CGRect frame;
    
    BOOL upYn = YES;
    if ([nFilterUpYn containsString:@"N"] || [nFilterUpYn containsString:@"n"])
        upYn = NO;
    
    if (upYn) {
        if (self.numPad.view.isHidden) {
            [self.numPad showKeypad:[UIApplication sharedApplication].statusBarOrientation parentViewController:self];
            frame = self.numPad.view.frame;
            frame.origin.y += self.numPad.view.frame.size.height;
            self.numPad.view.frame = frame;
            self.numPad.view.hidden  = false;
            
            [UIView beginAnimations:nil context:NULL];
            [UIView setAnimationDuration:0.25];
            frame = self.view.frame;
            frame.origin.y -= self.numPad.view.frame.size.height/2;
            frame.size.height += self.numPad.view.frame.size.height;
            self.view.frame = frame;
            frame = self.numPad.view.frame;
            frame.origin.y -= self.numPad.view.frame.size.height/2;
            self.numPad.view.frame = frame;
            [UIView commitAnimations];
        }
    }else {
        [self.numPad showKeypad:[UIApplication sharedApplication].statusBarOrientation parentViewController:self];
        self.numPad.view.hidden  = false;
    }
}

- (void)hideNumpad {
    if (self.numPad != nil) {
        CGRect frame;
        BOOL upYn = YES;
        if ([nFilterUpYn containsString:@"N"] || [nFilterUpYn containsString:@"n"])
            upYn = NO;
        
        if (upYn) {
            if (!self.numPad.view.isHidden) {
                [UIView beginAnimations:nil context:NULL];
                [UIView setAnimationDuration:0.25];
                frame = self.view.frame;
                frame.origin.y += self.numPad.view.frame.size.height/2;
                frame.size.height -= self.numPad.view.frame.size.height;
                self.view.frame = frame;
                frame = self.numPad.view.frame;
                frame.origin.y += self.numPad.view.frame.size.height/2;
                self.numPad.view.frame = frame;
                [UIView commitAnimations];
                self.numPad.view.hidden = true;
            }
        }else {
            self.numPad.view.hidden = true;
            [self.numPad closeKeypad];
        }
    }
}
// edited by ksmartech, get url request

- (void)getUrl:(NSString *)URLString
    parameters:(id)parameters
       success:(SuccessBlock)success
       failure:(FailureBlock)failure {
    NSURLComponents *components = [NSURLComponents componentsWithString:URLString];
    NSMutableArray *queryItems = [NSMutableArray array];
    for (NSString *key in parameters) {
        [queryItems addObject:[NSURLQueryItem queryItemWithName:key value:parameters[key]]];
    }
    components.queryItems = queryItems;
    NSURL *url = components.URL;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]initWithURL: url];
    [request setHTTPMethod: @"GET"];
    
    NSLog(@"url(getUrl) : %@",url);
    
    _fromWebview = NO;
    _FailedRequest = request;
    _onSuccess = success;
    _onFailure = failure;
    [self showIndicator];
    [self performSelector:@selector(timerMethod) withObject:@"1" afterDelay:TIME_OUT];
    [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

// edited by ksmartech, 인디케이터
- (void)showIndicator {
    NSLog(@"==================== BC showindicator ==========================");
    _img_loadingView.animationImages = [NSArray arrayWithObjects:
                                        [UIImage imageNamed:@"loding_0.png"],
                                        [UIImage imageNamed:@"loding_1.png"],
                                        [UIImage imageNamed:@"loding_2.png"],
                                        [UIImage imageNamed:@"loding_3.png"],
                                        [UIImage imageNamed:@"loding_4.png"],
                                        [UIImage imageNamed:@"loding_5.png"],
                                        [UIImage imageNamed:@"loding_6.png"],
                                        [UIImage imageNamed:@"loding_7.png"],
                                        [UIImage imageNamed:@"loding_8.png"],
                                        [UIImage imageNamed:@"loding_9.png"],
                                        [UIImage imageNamed:@"loding_10.png"],
                                        [UIImage imageNamed:@"loding_11.png"],
                                        nil];
    [_img_loadingView setImage:[UIImage imageNamed:@"loding_0.png"]];
    _img_loadingView.animationRepeatCount = 0;
    _img_loadingView.animationDuration = 1;
    
    [_img_loadingView setHidden:NO];
    
    [_img_loadingView startAnimating];
}

- (void)hideIndicator {
    NSLog(@"==================== BC hideIndicator ==================");
    [_img_loadingView stopAnimating];
    [_img_loadingView setHidden:YES];
}

- (void)updateMethod:(NSTimer *)incomingTimer {
    NSLog(@"Inside update method");
    if ([incomingTimer userInfo] != nil)
        NSLog(@"userInfo: %@", [incomingTimer userInfo]);
    [self hideApproval];
}

- (void)hideApproval {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)resQRKitData:(NSNotification *)noti {
    NSString *data = [noti object];
    NSLog(@"resQRKitData data : %@",data);
    
    NSMutableDictionary* outterJson   = [NSMutableDictionary new];
    [outterJson setValue:data forKey:@"DECAL_CODE"];
    
    NSData* nsData = [NSJSONSerialization dataWithJSONObject:outterJson options:NSJSONWritingPrettyPrinted error:nil];
    NSString* nsString = [[NSString alloc] initWithData:nsData encoding:NSUTF8StringEncoding];
    NSString *js = [NSString stringWithFormat:@"setDecalCode(%@)", nsString];
    
    [self callJS:js];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"resQrkitData" object:nil];
}


- (void)resQRReadData:(NSNotification *)noti {
    NSString *data = [noti object];
    NSLog(@"resQRKitData data : %@",data);
    
    
    if ((data == nil) || [data isEqualToString:@""]) {
        return;
    }else {
        NSMutableDictionary* outterJson   = [NSMutableDictionary new];
        [outterJson setValue:data forKey:@"TRNS_DATA"];
        
        NSData* nsData = [NSJSONSerialization dataWithJSONObject:outterJson options:NSJSONWritingPrettyPrinted error:nil];
        NSString* nsString = [[NSString alloc] initWithData:nsData encoding:NSUTF8StringEncoding];
        NSString *js = [NSString stringWithFormat:@"setTrnsData(%@)", nsString];
        
        [self callJS:js];
        
        [[NSNotificationCenter defaultCenter] removeObserver:self name:@"resQRReadData" object:nil];
    }
}
@end
