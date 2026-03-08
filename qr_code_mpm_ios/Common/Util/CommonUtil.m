//
//  CommonUtil.m
//  TWorldGlobal
//
//  Created by SugjinMac on 2017. 8. 4..
//  Copyright © 2017년 SugjinMac. All rights reserved.
//

#import <sys/sysctl.h>
#import "UIDevice+IdentifierAddition.h"
#import "CommonUtil.h"
#import "Reachability.h"

@implementation CommonUtil

/**
 @brief Wifi 켜져있는지 여부 리턴
 @return Wifi 켜져있는지 여부
 */
+ (BOOL)isWifiConnection
{
    Reachability *reach = [Reachability reachabilityForLocalWiFi];
    NetworkStatus status = [reach currentReachabilityStatus];

    if(status == ReachableViaWiFi)
    {
        return YES;
    }
    else
    {
        return NO;
    }
}

/**
 @brief 3G Network가 켜져있는지 여부 리턴
 @return 3G Network가 켜져있는지 여부
 */
+ (BOOL)isNetworkConnection
{
    Reachability *reach = [Reachability reachabilityForInternetConnection];
    NetworkStatus status = [reach currentReachabilityStatus];

    if(status == ReachableViaWWAN)
    {
        return YES;
    }
    else
    {
        return NO;
    }
}

+ (NSString *)getDeviceModel
{
    NSString *platform = nil;
    size_t size;
    
    sysctlbyname("hw.machine", NULL, &size, NULL, 0);
    char *machine = malloc(size);
    sysctlbyname("hw.machine", machine, &size, NULL, 0);
    
#if TARGET_IPHONE_SIMULATOR
    platform = [NSString stringWithFormat:@"%@", @"iPhone3,1"];
#else
    platform = [NSString stringWithCString:machine encoding:NSUTF8StringEncoding];
#endif
    
    free(machine);
    
    NSString *modelName = [self platformType:platform];
    return modelName;
}

+ (NSString *) platformType:(NSString *)platform
{
    if ([platform isEqualToString:@"iPhone1,1"])    return @"iPhone 2G";
    if ([platform isEqualToString:@"iPhone1,2"])    return @"iPhone 3G";
    if ([platform isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([platform isEqualToString:@"iPhone3,1"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone3,2"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone3,3"])    return @"iPhone 4";
    if ([platform isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([platform isEqualToString:@"iPhone5,1"])    return @"iPhone 5";
    if ([platform isEqualToString:@"iPhone5,2"])    return @"iPhone 5 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPhone5,3"])    return @"iPhone 5c (GSM)";
    if ([platform isEqualToString:@"iPhone5,4"])    return @"iPhone 5c (GSM+CDMA)";
    if ([platform isEqualToString:@"iPhone6,1"])    return @"iPhone 5s (GSM)";
    if ([platform isEqualToString:@"iPhone6,2"])    return @"iPhone 5s (GSM+CDMA)";
    if ([platform isEqualToString:@"iPhone8,4"])    return @"iPhone SE";
    if ([platform isEqualToString:@"iPhone7,1"])    return @"iPhone 6 Plus";
    if ([platform isEqualToString:@"iPhone7,2"])    return @"iPhone 6";
    if ([platform isEqualToString:@"iPhone8,2"])    return @"iPhone 6s Plus";
    if ([platform isEqualToString:@"iPhone8,1"])    return @"iPhone 6s";
    
    if ([platform isEqualToString:@"iPhone9,1"])    return @"iPhone 7";
    if ([platform isEqualToString:@"iPhone9,3"])    return @"iPhone 7";
    if ([platform isEqualToString:@"iPhone9,2"])    return @"iPhone 7 Plus";
    if ([platform isEqualToString:@"iPhone9,4"])    return @"iPhone 7 Plus";
    
    if ([platform isEqualToString:@"iPhone10,1"])    return @"iPhone 8";
    if ([platform isEqualToString:@"iPhone10,4"])    return @"iPhone 8";
    if ([platform isEqualToString:@"iPhone10,2"])    return @"iPhone 8 Plus";
    if ([platform isEqualToString:@"iPhone10,5"])    return @"iPhone 8 Plus";
    
    if ([platform isEqualToString:@"iPhone10,3"])    return @"iPhone X";
    if ([platform isEqualToString:@"iPhone10,6"])    return @"iPhone X";
    
    if ([platform isEqualToString:@"iPod1,1"])      return @"iPod Touch (1 Gen)";
    if ([platform isEqualToString:@"iPod2,1"])      return @"iPod Touch (2 Gen)";
    if ([platform isEqualToString:@"iPod3,1"])      return @"iPod Touch (3 Gen)";
    if ([platform isEqualToString:@"iPod4,1"])      return @"iPod Touch (4 Gen)";
    if ([platform isEqualToString:@"iPod5,1"])      return @"iPod Touch (5 Gen)";
    if ([platform isEqualToString:@"iPod7,1"])      return @"iPod Touch (6 Gen)";
    
    
    if ([platform isEqualToString:@"iPad1,1"])      return @"iPad";
    if ([platform isEqualToString:@"iPad1,2"])      return @"iPad 3G";
    if ([platform isEqualToString:@"iPad2,1"])      return @"iPad 2 (WiFi)";
    if ([platform isEqualToString:@"iPad2,2"])      return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,3"])      return @"iPad 2 (CDMA)";
    if ([platform isEqualToString:@"iPad2,4"])      return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,5"])      return @"iPad Mini (WiFi)";
    if ([platform isEqualToString:@"iPad2,6"])      return @"iPad Mini";
    if ([platform isEqualToString:@"iPad2,7"])      return @"iPad Mini (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,1"])      return @"iPad 3 (WiFi)";
    if ([platform isEqualToString:@"iPad3,2"])      return @"iPad 3 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad3,3"])      return @"iPad 3";
    if ([platform isEqualToString:@"iPad3,4"])      return @"iPad 4 (WiFi)";
    if ([platform isEqualToString:@"iPad3,5"])      return @"iPad 4";
    if ([platform isEqualToString:@"iPad3,6"])      return @"iPad 4 (GSM+CDMA)";
    if ([platform isEqualToString:@"iPad4,1"])      return @"iPad Air (WiFi)";
    if ([platform isEqualToString:@"iPad4,2"])      return @"iPad Air (Cellular)";
    if ([platform isEqualToString:@"iPad4,4"])      return @"iPad Mini 2 (WiFi)";
    if ([platform isEqualToString:@"iPad4,5"])      return @"iPad Mini 2 (Cellular)";
    if ([platform isEqualToString:@"iPad4,6"])      return @"iPad Mini 2";
    if ([platform isEqualToString:@"iPad4,7"])      return @"iPad Mini 3";
    if ([platform isEqualToString:@"iPad4,8"])      return @"iPad Mini 3";
    if ([platform isEqualToString:@"iPad4,9"])      return @"iPad Mini 3";
    if ([platform isEqualToString:@"iPad5,1"])      return @"iPad Mini 4 (WiFi)";
    if ([platform isEqualToString:@"iPad5,2"])      return @"iPad Mini 4 (LTE)";
    if ([platform isEqualToString:@"iPad5,3"])      return @"iPad Air 2";
    if ([platform isEqualToString:@"iPad5,4"])      return @"iPad Air 2";
    if ([platform isEqualToString:@"iPad6,3"])      return @"iPad Pro 9.7";
    if ([platform isEqualToString:@"iPad6,4"])      return @"iPad Pro 9.7";
    if ([platform isEqualToString:@"iPad6,7"])      return @"iPad Pro 12.9";
    if ([platform isEqualToString:@"iPad6,8"])      return @"iPad Pro 12.9";
    
    if ([platform isEqualToString:@"iPad6,11"])      return @"iPad (5th Gen)";
    if ([platform isEqualToString:@"iPad6,12"])      return @"iPad (5th Gen)";
    if ([platform isEqualToString:@"iPad7,1"])      return @"iPad Pro 12.9 (2nd Gen)";
    if ([platform isEqualToString:@"iPad7,2"])      return @"iPad Pro 12.9 (2nd Gen)";
    if ([platform isEqualToString:@"iPad7,3"])      return @"iPad Pro 10.5";
    if ([platform isEqualToString:@"iPad7,4"])      return @"iPad Pro 10.5";
    
    if ([platform isEqualToString:@"AppleTV2,1"])   return @"Apple TV 2G";
    if ([platform isEqualToString:@"AppleTV3,1"])   return @"Apple TV 3";
    if ([platform isEqualToString:@"AppleTV3,2"])   return @"Apple TV 3 (2013)";
    
    if ([platform isEqualToString:@"i386"])         return @"Simulator";
    if ([platform isEqualToString:@"x86_64"])       return @"Simulator";
    
    return platform;
}

+ (NSString *)checkDeviceKey
{
    //    return @"2";
    // 기존에 저장된 UDID가 있다면 그 값을 꺼내오고
    NSString *deviceKey = [[NSUserDefaults standardUserDefaults] stringForKey:@"UUID"];
    
    if(deviceKey == nil){
        // 아니면 UUID를 생성후 키체인에 등록한 후 사용하면 된다.
        NSString *rDeviceKey = [[UIDevice currentDevice] uniqueGlobalDeviceIdentifier];
        NSString *tmpDeviceKeyHead = @"0284850380382940-ghijklmnopqrstuvwxyz123345678901234-FLKDeviceKeyHead-abcdefghijklmno-qrstuvwxyzabcde";
        
        deviceKey = [[rDeviceKey stringByAppendingString:tmpDeviceKeyHead] substringWithRange:NSMakeRange(0, 120)];
        
        // 키체인에 등록된 UUID를 임시로 저장한다.
        NSString *uuidKey = [[NSUserDefaults standardUserDefaults] stringForKey:@"UUID"];
        if(uuidKey == nil) {
            [[NSUserDefaults standardUserDefaults] setValue:deviceKey forKey:@"UUID"];
            [[NSUserDefaults standardUserDefaults] synchronize];
        }
    }
    
    return deviceKey;
}


//
//+(void)saveJSessionID
//{
//    NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
//    NSData *cookieData = [NSKeyedArchiver archivedDataWithRootObject:cookies];
//    if ( [cookieData length] )
//    {
//        NSArray *cookies = [NSKeyedUnarchiver unarchiveObjectWithData:cookieData];
//        for ( NSHTTPCookie *cookie in cookies )
//        {
//            if([cookie.name isEqualToString:@"JSESSIONID"]){
//                NSString *jssionId = cookie.value;
//                NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
//                [userDefault setObject:jssionId forKey:@"jssionId"];
//                [userDefault synchronize];
//            }
//        }
//    }
//}
//
//+(NSString *)getCurrentJSessionID
//{
//    NSString *jssionId;
//    NSArray *cookies = [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies];
//    NSData *cookieData = [NSKeyedArchiver archivedDataWithRootObject:cookies];
//    if ( [cookieData length] )
//    {
//        NSArray *cookies = [NSKeyedUnarchiver unarchiveObjectWithData:cookieData];
//        for ( NSHTTPCookie *cookie in cookies )
//        {
//            if([cookie.name isEqualToString:@"JSESSIONID"]){
//                jssionId = cookie.value;
//            }
//        }
//    }
//    return jssionId;
//}
//
//+(NSString *)getSavedJSessionID
//{
//    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
//    NSString *jssionId = [userDefault objectForKey:@"jssionId"];
//    return jssionId;
//}

+ (UIImage *)imageFromColor:(UIColor *)color {
    CGRect rect = CGRectMake(0, 0, 10, 10);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}


@end
