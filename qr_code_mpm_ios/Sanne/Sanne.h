//
//  Sanne.h
//  LibraryForSanne
//
//  Created by bhchae on 2014. 7. 14..
//
//

#import <Foundation/Foundation.h>

#define sanneInstance __ns_h0
#define updatePatternFromServerWithError __ns_h2
#define sysCheckStart __ns_h3
#define getSanneVersion __ns_h4
#define getPatternVersionWithError __ns_h5
#define getPatternFileDateWithError __ns_h6
#define sysCheckStartWithUpdateInfo __ns_h7
#define getDecodeStr __ns_h7

@interface Sanne : NSObject {
}


// 시스템 위변조 검사
+ (Sanne *) sanneInstance;
- (BOOL)updatePatternFromServerWithError:(NSError **)error;
- (void)sysCheckStart;
- (void)sysCheckStartWithUpdateInfo:(NSString *)updateInfo;
- (NSString *) getSanneVersion;
- (NSString *) getPatternVersionWithError:(NSError **)error;
+ (NSString *) getDecodeStr:(NSString *)encodeStr;

@property (nonatomic, strong) NSDictionary *updateResult;
@property (nonatomic, strong) NSDictionary *sanneResult;
@property (nonatomic, assign) NSInteger timeOut;
@property (nonatomic, assign) int retryCountForPatternUpdate;
@property (nonatomic, assign) BOOL enableDetectDebugger;
@property (nonatomic, assign) BOOL enableDetectDebuggerWithQuit;
@property (nonatomic, assign) BOOL enableDisplayWarningAlert;

@end
