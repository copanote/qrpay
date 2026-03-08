//
//  NSString+Format.m
//  mpm
//
//  Created by Bae Hyunjin on 24/06/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import "NSString+Format.h"

@implementation NSString(Format)

+(NSString *)priceStr:(double)price {
    NSNumberFormatter *priceFormatter = [[NSNumberFormatter alloc]init];
    [priceFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
    
    return [NSString stringWithFormat:@"%@", [priceFormatter stringFromNumber:[NSNumber numberWithDouble:price]]];
}
@end
