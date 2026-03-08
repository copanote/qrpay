//
//  QRShareViewController.m
//  mpm
//
//  Created by SugjinMac on 2018. 3. 14..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import "QRShareViewController.h"
#import "CommonUtil.h"
#import "AppDelegate.h"
#import "NSDictionary+Safe.h"
#import "ViewController.h"

const NSInteger webViewMargin = 5;

@interface QRShareViewController () <WKNavigationDelegate, WKUIDelegate>
@property (nonatomic, strong) WKWebView *webView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *webviewH;
@property (weak, nonatomic) IBOutlet UIView *webContentView;

@end

@implementation QRShareViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (BOOL)shouldAutorotate {
    return NO;
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    NSLog(@"QRShareView : viewDidAppear ...");
    [super viewDidAppear:animated];
    
    [self loadWebView];
}

- (void)loadWebView {
    WKWebViewConfiguration *configuration = [[WKWebViewConfiguration alloc] init];

    configuration.processPool = processPool;
    
    webViewRect = CGRectMake(0, 0, self.backgroundView.frame.size.width, 0);
//    self.webView = [[WKWebView alloc] initWithFrame:webViewRect];
    self.webView = [[WKWebView alloc] initWithFrame:webViewRect configuration:configuration];
    self.webView.UIDelegate = self;
    self.webView.navigationDelegate = self;
    self.webView.backgroundColor = UIColor.whiteColor;
    self.webView.scrollView.backgroundColor = UIColor.whiteColor;
    self.webView.opaque = false;
    
    self.webView.scrollView.scrollEnabled = true;
    self.webView.scrollView.bounces = false;
    [self.backgroundView addSubview:self.webView];
    [self.view layoutIfNeeded];
    
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
  
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    NSString *saveQRBase64Str = [userDefault stringForKey:@"saveQRBase64Str"];
    NSString *saveMerNm = [userDefault stringForKey:@"merNm"];
    
    NSURL *parsedUrl = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", appDelegate.currentServerURL, qrShareUrl]];
    NSString *originParam = [parsedUrl query];
    NSString *param = nil;
    NSURL *url = nil;
    if (originParam == nil
        || [originParam isKindOfClass:[NSNull class]]
        || ([originParam respondsToSelector:@selector(length)]
            && [(NSData *)originParam length] == 0)
        || ([originParam respondsToSelector:@selector(count)]
            && [(NSArray *)originParam count] == 0)) {
        param = [NSString stringWithFormat:@"qr=%@&merNm=%@", saveQRBase64Str, saveMerNm];
        url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@%@?%@", appDelegate.currentServerURL, qrShareUrl, param] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
        NSLog(@"URL : %@", url.absoluteString );
    }else{
        param = [NSString stringWithFormat:@"qr=%@&merNm=%@&%@", saveQRBase64Str, saveMerNm, originParam];
        NSArray *comp = [qrShareUrl componentsSeparatedByString:@"?"];
        url = [NSURL URLWithString:[[NSString stringWithFormat:@"%@%@?%@", appDelegate.currentServerURL, [comp objectAtIndex:0], param] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLFragmentAllowedCharacterSet]]];
        NSLog(@"URL : %@", url.absoluteString );
    }
    
    [self.webView loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)SetQRShareUrl:(NSString *)_url {
    NSLog(@"SetQRShareUrl");
    qrShareUrl = _url;
}

- (void)setProcessPool:(id)_processPool {
    NSLog(@"setProcessPool");
    processPool = _processPool;
}

- (IBAction)actionClose:(id)sender {
    NSLog(@"actionClose");
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)actionShare:(id)sender {
    NSLog(@"actionShare");
    
    UIImage *image = [self captureWebView:self.webView];
    NSArray *items = @[image];
    
    // build an activity view controller
    UIActivityViewController *controller = [[UIActivityViewController alloc]initWithActivityItems:items applicationActivities:nil];
    
    NSArray *excluded = @[UIActivityTypeAirDrop, UIActivityTypeSaveToCameraRoll, UIActivityTypeCopyToPasteboard
                          , UIActivityTypePrint, UIActivityTypeAssignToContact, UIActivityTypeAddToReadingList, UIActivityTypeOpenInIBooks];
    controller.excludedActivityTypes = excluded;
    
    if ( [controller respondsToSelector:@selector(popoverPresentationController)] ) {
        // iOS8
        controller.popoverPresentationController.sourceView = sender;
    }
    controller.completionWithItemsHandler = ^(NSString *activityType, BOOL completed, NSArray *returnedItems, NSError *activityError) {
        NSLog(@"completed : %@", (completed ? @"YES" : @"NO"));
        NSLog(@"activityError : %@",[activityError localizedDescription]);
        
        [self dismissViewControllerAnimated:NO completion:nil];
    };
    [self presentViewController:controller animated:YES completion:nil];
}

- (UIImage*) captureWebView: (WKWebView*)webView {
    UIGraphicsBeginImageContextWithOptions(webView.scrollView.contentSize, webView.scrollView.opaque, 0.0);
    {
        CGPoint savedContentOffset = webView.scrollView.contentOffset;
        CGRect savedFrame = webView.scrollView.frame;
        
        webView.scrollView.contentOffset = CGPointZero;
        webView.scrollView.frame = CGRectMake(0, 0, webView.scrollView.contentSize.width, webView.scrollView.contentSize.height);
        [webView.scrollView.layer renderInContext: UIGraphicsGetCurrentContext()];
        UIImage* img = UIGraphicsGetImageFromCurrentImageContext();
        
        webView.scrollView.contentOffset = savedContentOffset;
        webView.scrollView.frame = savedFrame;
        
        UIGraphicsEndImageContext();
        UIImageWriteToSavedPhotosAlbum(img, nil, nil, nil);
        
        return img;
    }
}

#pragma mark - WKNavigationDelegate

- (void)callJS:(NSString *)js {
    [self.webView evaluateJavaScript:js completionHandler:^(id result, NSError *error) {
        NSLog(@"JSCall : %@, result : %@, error: %@", js, result, error);
    }];
}

- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(WKNavigation *)navigation {
    NSLog(@"webViewDidStartLoad");
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
    NSLog(@"webViewDidFinishLoad");
    
    if (webView.isLoading == false) {
        if (_webContentView.hidden) {
            [webView evaluateJavaScript:@"document.body.scrollHeight" completionHandler:^(id _Nullable result, NSError * _Nullable error) {
                if ([result isKindOfClass:[NSNumber class]]) {
                    _webviewH.constant = [result floatValue]+webViewMargin;
                }else {
                    [webView sizeThatFits:CGSizeMake(self.backgroundView.frame.size.width, 1)];
                    _webviewH.constant = webView.scrollView.contentSize.height+webViewMargin;
                }
                [self.view layoutIfNeeded];
                webViewRect = CGRectMake(0, 0, self.backgroundView.frame.size.width, self.backgroundView.frame.size.height);
                [self.webView setFrame:webViewRect];
                _webContentView.hidden = NO;
            }];
        }
    }
    
    NSString * jsCallBack = @"window.getSelection().removeAllRanges();";
    
    [self callJS:jsCallBack];
    [self callJS:@"document.documentElement.style.webkitUserSelect='none';"];
    [self callJS:@"document.documentElement.style.webkitTouchCallout='none';"];
}

- (void)webView:(WKWebView *)webView didFailNavigation:(WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"webView didFailLoadWithError failed: %@", error);
}

@end
