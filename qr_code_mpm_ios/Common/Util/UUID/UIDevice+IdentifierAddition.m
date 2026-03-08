//
//  UIDevice(Identifier).m
//  
//
//  Created by Goeun Kim on 13. 4. 5..
//  Copyright (c) 2013년 Goeun Kim. All rights reserved.
//

#import "UIDevice+IdentifierAddition.h"
#import "KeychainItemWrapper.h"

//#define APP_ID_IPAD @"kr.co.kyobobook"
#define APP_ID_IPAD @"NC777HLHAM.com.bccard.mpm" // NC777HLHAM. @"AXP9CRR6GH"
#define APP_ID APP_ID_IPAD

@interface UIDevice(Private)

- (NSString *) getUUID;

@end

@implementation UIDevice (IdentifierAddition)

////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark Private Methods

- (void)saveData:(id)object forKey:(id)key {
    //키체인 그룹 값이 맞지 않음. 맞는 값이 필요함.
    NSString *appKey = [NSString stringWithFormat:@"%@.%@", APP_ID, key];
    
    //addLog(([NSString stringWithFormat:@"appKey : %@",appKey]));
    
    KeychainItemWrapper *wrapper =
    [[KeychainItemWrapper alloc] initWithIdentifier:appKey accessGroup:nil];//appKey];
    //[[KeychainItemWrapper alloc] initWithIdentifier:@"kr.co.kyobobook.iPadB2C.AccountNumber" accessGroup:nil];//appKey];
	//[wrapper setObject:@"secureDataIdentifer" forKey:(__bridge id)(kSecAttrAccount)];
    [wrapper setObject:appKey forKey:(__bridge id)(kSecAttrAccount)];
	[wrapper setObject:object forKey:(__bridge id)(kSecValueData)];
	wrapper = nil;
}

- (id)loadData:(id)key {
    NSString *appKey = [NSString stringWithFormat:@"%@.%@", APP_ID, key];
    KeychainItemWrapper *wrapper =
     [[KeychainItemWrapper alloc] initWithIdentifier:appKey accessGroup:nil];
    //[[KeychainItemWrapper alloc] initWithIdentifier:@"kr.co.kyobobook.iPadB2C.AccountNumber" accessGroup:nil];//[NSString stringWithFormat:@"%@.%@", APP_ID, key]];
   
	NSString *retString = [wrapper objectForKey:(__bridge id)(kSecValueData)];

	wrapper = nil;
	
    return retString;
}

- (NSString *)getUUID
{
    // generate a new uuid and store it in user defaults
    CFUUIDRef uuid = CFUUIDCreate(NULL);
    NSString *appUID = (NSString *) CFBridgingRelease(CFUUIDCreateString(NULL, uuid));
    CFRelease(uuid);
    
    return appUID;
}

- (NSString *)getGlobalUUID
{
    NSString *appKey = @"UUID";
    NSString *uuid = [self loadData:appKey];
	
	if(!uuid || !uuid.length) {
        [self saveData:[self getUUID] forKey:appKey];
        uuid = [self loadData:appKey];
    }
	
    return uuid;
}

////////////////////////////////////////////////////////////////////////////////
#pragma mark -
#pragma mark Public Methods

- (NSString *)uniqueGlobalDeviceIdentifier{
    NSString *globalUUID = [[UIDevice currentDevice] getGlobalUUID];

    return globalUUID;
}

@end
