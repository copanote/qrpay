//
//  NSDictionary+Safe.h
//  mpm
//
//  Created by Bae Hyunjin on 24/06/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSDictionary (Safe)

-(NSString *)stringForKey:(NSString *)key;
-(double)doubleForKey:(NSString *)key;

@end

