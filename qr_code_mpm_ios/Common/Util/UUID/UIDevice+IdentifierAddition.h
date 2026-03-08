//
//  UIDevice(Identifier).h
//  
//
//  Created by Goeun Kim on 13. 4. 5..
//  Copyright (c) 2013년 Goeun Kim. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface UIDevice (IdentifierAddition)

- (NSString *)uniqueGlobalDeviceIdentifier;

@end
