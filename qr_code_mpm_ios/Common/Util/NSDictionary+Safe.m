//
//  NSDictionary+Safe.m
//  mpm
//
//  Created by Bae Hyunjin on 24/06/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import "NSDictionary+Safe.h"

@implementation NSDictionary (Safe)

- (NSString *)stringForKey:(NSString *)key {
    if ([self objectForKey:key]) {
        return [self objectForKey:key];
    }else {
        return @"";
    }
}

- (double)doubleForKey:(NSString *)key {
    if ([self objectForKey:key]) {
        return [[self objectForKey:key] doubleValue];
    }else {
        return 0;
    }
}

@end
