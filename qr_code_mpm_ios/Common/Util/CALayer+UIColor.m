//
//  CALayer+UIColor.m
//  mpm
//
//  Created by Bae Hyunjin on 09/07/2019.
//  Copyright © 2019 BCCard. All rights reserved.
//

#import "CALayer+UIColor.h"

@implementation CALayer(UIColor)

- (void)setBorderUIColor:(UIColor*)color {
    self.borderColor = color.CGColor;
}

- (UIColor*)borderUIColor {
    return [UIColor colorWithCGColor:self.borderColor];
}

@end
