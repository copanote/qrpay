//
//  CommonUtil.h
//  TWorldGlobal
//
//  Created by SugjinMac on 2017. 8. 4..
//  Copyright © 2017년 SugjinMac. All rights reserved.
//

#import <Foundation/Foundation.h>

#define UIColorFromRGB(rgbValue) \
[UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 \
                green:((float)((rgbValue & 0x00FF00) >>  8))/255.0 \
                blue:((float)((rgbValue & 0x0000FF) >>  0))/255.0 \
                alpha:1.0]

@interface CommonUtil : NSObject

+ (BOOL)isWifiConnection;
+ (BOOL)isNetworkConnection;
+ (NSString *)getDeviceModel;
+ (NSString *) platformType:(NSString *)platform;
+ (NSString *)checkDeviceKey;
//+ (NSString *)getDBString:(NSString *)_curLang key:(NSString *)_key;
//+(void)saveJSessionID;
//+(NSString *)getCurrentJSessionID;
//+(NSString *)getSavedJSessionID;
+ (UIImage *)imageFromColor:(UIColor *)color;
@end
