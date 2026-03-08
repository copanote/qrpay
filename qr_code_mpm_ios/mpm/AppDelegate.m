//
//  AppDelegate.m
//  mpm
//
//  Created by SugjinMac on 2018. 1. 30..
//  Copyright © 2018년 BCCard. All rights reserved.
//

#import "AppDelegate.h"
#import "Constant.h"
#import "ViewController.h"
#import "MainViewController.h"
#import "PushInfoViewController.h"
#import "NSDictionary+Safe.h"

#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
@import UserNotifications;
#endif

// Implement UNUserNotificationCenterDelegate to receive display notification via APNS for devices
// running iOS 10 and above.
#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
@interface AppDelegate ()

@end
#endif

@implementation AppDelegate

NSString *const kGCMMessageIDKey = @"gcm.message_id";
UIImageView *imageView = nil;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    if (@available(iOS 13.0, *)) {
        [self.window setOverrideUserInterfaceStyle:UIUserInterfaceStyleLight];
    }

    self.currentServerURL = SERVER_URL;
    
    [FIRApp configure];
    [FIRMessaging messaging].delegate = self;
    
    // edited by ksmartech, 쿠키 로드
    [self loadCookies];
    
    UNNotificationCategory* notiCategory = [UNNotificationCategory
         categoryWithIdentifier:NOTIFICATION_CATEGORY
         actions:@[]
         intentIdentifiers:@[]
         options:UNNotificationCategoryOptionCustomDismissAction];
     
    // Register the notification categories.
    UNUserNotificationCenter* center = [UNUserNotificationCenter currentNotificationCenter];
    [center setNotificationCategories:[NSSet setWithObjects:notiCategory, nil]];
    
    return YES;
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return UIInterfaceOrientationMaskPortrait;
}

// [START receive_message]
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    // If you are receiving a notification message while your app is in the background,
    // this callback will not be fired till the user taps on the notification launching the application.
    // TODO: Handle data of notification
    
    // With swizzling disabled you must let Messaging know about the message, for Analytics
    // [[FIRMessaging messaging] appDidReceiveMessage:userInfo];
    
    // Print message ID.
    if (userInfo[kGCMMessageIDKey]) {
        NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
    }
    
    // Print full message.
    NSLog(@"%@", userInfo);
}

- (UIViewController *)theParentVC {
    UIViewController* parentController =self.window.rootViewController;
    while (parentController.presentedViewController &&
           parentController != parentController.presentedViewController) {
        parentController = parentController.presentedViewController;
    }
    return parentController;
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    // If you are receiving a notification message while your app is in the background,
    // this callback will not be fired till the user taps on the notification launching the application.
    // TODO: Handle data of notification
    
    // Print message ID.
    if (userInfo[kGCMMessageIDKey]) {
        NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
    }
    
    if (userInfo[@"gcm.notification.seq"]) {
        NSLog(@"Message SEQ: %@", userInfo[@"gcm.notification.seq"]);
    }
    
    if (userInfo[@"VALUE"]) {
        NSLog(@"Message VALUE: %@", userInfo[@"VALUE"]);
    }
    
    // Print full message.
    NSLog(@"push userInfo : %@", userInfo);
    
    NSDictionary *apsDic = [userInfo valueForKey:@"aps"];

    NSDictionary *alertDic = [apsDic valueForKey:@"alert"];
    NSString *MSG = [alertDic stringForKey:@"body"];
    NSString *TITLE = [alertDic stringForKey:@"title"];
    //NSString *VALUE = userInfo[@"gcm.notification.value"];
    NSString *VALUE = userInfo[@"VALUE"];
    //NSString *SEQ = userInfo[@"gcm.notification.seq"];
    NSLog(@"MSG : %@", MSG);
    NSLog(@"TITLE : %@", TITLE);
    NSLog(@"VALUE : %@", VALUE);
    //NSLog(@"SEQ : %@", SEQ);
    
    //url parsing =====================================================
    NSArray *params = [VALUE componentsSeparatedByString:@"?"];

    NSLog(@"query: %@", params[1]);
    
    MainViewController *mainViewController = (MainViewController *)self.window.rootViewController;
    if ([mainViewController getInitView] == 0) {
        [mainViewController setInitView:[mainViewController getInitView]+1];
        ViewController *viewController = [mainViewController getMainView];
        viewController.modalPresentationStyle = UIModalPresentationFullScreen;
        [mainViewController presentViewController:viewController animated:YES completion:nil];
    }
    
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    [userDefault setObject:@"Y" forKey:@"is_push"];
    [userDefault setObject:[self urlParsing:params[1]] forKey:@"pushUserInfo"];
    [userDefault setObject:[VALUE componentsSeparatedByString:@"bcMView://"][1] forKey:@"pushUrl"];
    
    [userDefault synchronize];
    
    if (application.applicationState != UIApplicationStateActive){
        [[mainViewController getMainView] pushReceiveCheck];
    }else {
        //push 오면 native 화면으로 이동
        
        UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
        PushInfoViewController *pushInfoView = (PushInfoViewController *) [storyboard instantiateViewControllerWithIdentifier:@"pushInfoView"];
        [pushInfoView SetParent:mainViewController];
        pushInfoView.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
        pushInfoView.modalPresentationStyle = UIModalPresentationOverCurrentContext;
        
        [[self theParentVC] presentViewController:pushInfoView animated:YES completion:nil];
    }
  
    completionHandler(UIBackgroundFetchResultNewData);
}
// [END receive_message]

// [START ios_10_message_handling]
// Receive displayed notifications for iOS 10 devices.
#if defined(__IPHONE_10_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_10_0
// Handle incoming notification messages while app is in the foreground.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
    NSDictionary *userInfo = notification.request.content.userInfo;
    if ([notification.request.content.categoryIdentifier isEqualToString:NOTIFICATION_CATEGORY]) {
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // [[FIRMessaging messaging] appDidReceiveMessage:userInfo];
        
        // Print message ID.
        if (userInfo[kGCMMessageIDKey]) {
            NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
        }
        
        // Print full message.
        NSLog(@"%@", userInfo);
        
        // Change this to your preferred presentation option
        completionHandler(UNNotificationPresentationOptionNone);
    }
}

// Handle notification messages after display notification is tapped by the user.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void(^)(void))completionHandler {
    if ([response.notification.request.content.categoryIdentifier isEqualToString:NOTIFICATION_CATEGORY]) {
        
        NSDictionary *userInfo = response.notification.request.content.userInfo;
        if (userInfo[kGCMMessageIDKey]) {
            NSLog(@"Message ID: %@", userInfo[kGCMMessageIDKey]);
        }
        
        // Print full message.
        NSLog(@"%@", userInfo);
        
        completionHandler();
    }
}
#endif
// [END ios_10_message_handling]

// [START refresh_token]
- (void)messaging:(FIRMessaging *)messaging didReceiveRegistrationToken:(NSString *)fcmToken {
    NSLog(@"FCM registration token: %@", fcmToken);
    
    //NSString *token = [[deviceToken description] stringByTrimmingCharactersInSet: [NSCharacterSet characterSetWithCharactersInString:@"<>"]];
    //token = [token stringByReplacingOccurrencesOfString:@" " withString:@""];
    NSLog(@"content---%@", fcmToken);
    
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    [userDefault setObject:fcmToken forKey:@"pushToken"];
    [userDefault synchronize];
    
    // TODO: If necessary send token to application server.
    // Note: This callback is fired at each app startup and whenever a new token is generated.
}
// [END refresh_token]

// [START ios_10_data_message]
// Receive data messages on iOS 10+ directly from FCM (bypassing APNs) when the app is in the foreground.
// To enable direct data messages, you can set [Messaging messaging].shouldEstablishDirectChannel to YES.
- (void)messaging:(FIRMessaging *)messaging didReceiveMessage:(FIRMessagingRemoteMessage *)remoteMessage {
    NSLog(@"Received data message: %@", remoteMessage.appData);
}
// [END ios_10_data_message]

- (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSLog(@"APNs device token retrieved: %@", deviceToken);
    NSString *apnsToken = [NSString stringWithUTF8String:[deviceToken bytes]];
    NSLog(@"token : %@", apnsToken);
}

- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    NSLog(@"Unable to register for remote notifications: %@", error);
}

#pragma mark - notification

- (void)applicationWillResignActive:(UIApplication *)application {
    // edited by ksmartech, 화면 보안
    if(imageView != nil) {
        [imageView removeFromSuperview];
        imageView = nil;
    }
    
    imageView = [[UIImageView alloc]initWithFrame:[self.window frame]];
    UIImage* image = [UIImage imageNamed:@"privacy.png"];
    if ( image.size.width > image.size.height ) {
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        //since the width > height we may fit it and we'll have bands on top/bottom
    } else {
        imageView.contentMode = UIViewContentModeScaleAspectFill;
        //width < height we fill it until width is taken up and clipped on top/bottom
    }
    [imageView setBackgroundColor:UIColor.redColor];
    [imageView setImage:image];
    [self.window addSubview:imageView];
    [self.window bringSubviewToFront:imageView];
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // edited by ksmartech, 쿠키 저장
    [self saveCookies];
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // edited by ksmartech, 쿠키 로드
    [self loadCookies];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // edited by ksmartech, 화면 보안 해제
    if (imageView != nil) {
        [imageView removeFromSuperview];
        imageView = nil;
    }
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // edited by ksmartech, 쿠키 저장
    [self saveCookies];
}

// edtied by ksmartech, 쿠키 관련
- (void)saveCookies {
    NSLog(@"Save cookies...");
    NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
    NSData *cookieData = [NSKeyedArchiver archivedDataWithRootObject:cookies];
    [[NSUserDefaults standardUserDefaults] setObject:cookieData forKey:@"Cookies"];
    
    for (NSHTTPCookie *cookie in cookies) {
        //[[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
        NSLog(@"    Cookie Name: %@ : Value: %@, sessionOnly:%i", cookie.name, cookie.value, cookie.sessionOnly);
        NSLog(@"          Cookie URL: %@ ", cookie.domain);
    }
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (void)loadCookies {
    NSLog(@"Load cookies...");
    NSData *cookiesData = [[NSUserDefaults standardUserDefaults] objectForKey:@"Cookies"];
    if ([cookiesData length]) {
        NSArray *cookies = [NSKeyedUnarchiver unarchiveObjectWithData:cookiesData];
        for (NSHTTPCookie *cookie in cookies) {
            //if (cookie.sessionOnly == NO) {
            NSLog(@"    Cookie Name: %@ : Value: %@, sessionOnly:%i", cookie.name, cookie.value, cookie.sessionOnly);
            NSLog(@"          Cookie URL: %@ ", cookie.domain);
            [[NSHTTPCookieStorage sharedHTTPCookieStorage] setCookie:cookie];
            //            }
        }
    }
}

- (NSString *)valueForKey:(NSString *)key fromQueryItems:(NSArray *)queryItems {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"name=%@", key];
    NSURLQueryItem *queryItem = [[queryItems
                                  filteredArrayUsingPredicate:predicate]
                                 firstObject];
    return queryItem.value;
}

- (NSMutableDictionary *)urlParsing:(NSString*)VALUE {
    NSMutableDictionary *queryStringDictionary = [NSMutableDictionary dictionary];
    NSArray *urlComponents = [VALUE componentsSeparatedByString:@"&"];
    
    for (NSString *keyValuePair in urlComponents) {
        
        NSArray *pairComponents = [keyValuePair componentsSeparatedByString:@"="];
        NSString *key = [[pairComponents firstObject] stringByRemovingPercentEncoding];
        NSString *value = [[pairComponents lastObject] stringByRemovingPercentEncoding];
        
        [queryStringDictionary setObject:value forKey:key];
        
    }
    NSLog(@"%@", queryStringDictionary);
    return queryStringDictionary;
}

@end
