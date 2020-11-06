#import "FastPhoneLoginAliyunPlugin.h"
#import <ATAuthSDK/ATAuthSDK.h>
#import "utils/PNSBuildModelUtils.h"

@implementation FastPhoneLoginAliyunPlugin{
    FlutterResult _result;
    NSDictionary *_arguments;
    UIViewController *_viewController;
}
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"fast_phone_login_aliyun"
            binaryMessenger:[registrar messenger]];
    UIViewController *viewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    FastPhoneLoginAliyunPlugin* instance = [[FastPhoneLoginAliyunPlugin alloc] initWithViewController:viewController];
//  FastPhoneLoginAliyunPlugin* instance = [[FastPhoneLoginAliyunPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithViewController:(UIViewController *)viewController {
    self = [super init];
    if (self) {
        _viewController = viewController;
    }
    return self;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"init" isEqualToString:call.method]) {
      NSString *secret = call.arguments[@"iOSsecret"];
      [[TXCommonHandler sharedInstance] setAuthSDKInfo:secret
                                              complete:^(NSDictionary * _Nonnull resultDic) {
          NSLog(@"设置秘钥结果：%@", resultDic);
      }];
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  }else  if ([@"checkEnvAvailable" isEqualToString:call.method]) {
    [[TXCommonHandler sharedInstance] checkEnvAvailableWithAuthType:PNSAuthTypeLoginToken complete:^(NSDictionary * _Nonnull resultDic) {
        NSLog(@"检查认证环境结果：%@", resultDic);
        
    }];
  } else  if ([@"getLoginToken" isEqualToString:call.method]) {
      [[[TXCommonHandler sharedInstance] getReporter]setConsolePrintLoggerEnable:true];
      TXCustomModel *model = [PNSBuildModelUtils buildModelWithStyle:PNSBuildModelStyleSheetPortrait
                                                              button1Title:@"短信登录（使用系统导航栏）"
                                                                   target1:self
                                                                 selector1:@selector(gotoSmsControllerAndShowNavBar)
                                                              button2Title:@"短信登录（隐藏系统导航栏）"
                                                                   target2:self
                                                                 selector2:@selector(gotoSmsControllerAndHiddenNavBar)];
//      [[TXCommonHandler sharedInstance] accelerateLoginPageWithTimeout:3.0 complete:^(NSDictionary * _Nonnull resultDic) {
//          NSLog(@"获取LoginToken为后面授权页拉起加个速，加速结果：%@", resultDic);
//      }];
      [[TXCommonHandler sharedInstance] getLoginTokenWithTimeout:3.0
                                                      controller:_viewController
                                                           model:model
                                                        complete:^(NSDictionary * _Nonnull resultDic) {
          NSLog(@"获取LoginToken或拉起授权页失败回调：%@", resultDic);
          NSString *resultCode = [resultDic objectForKey:@"resultCode"];
          if ([PNSCodeLoginControllerPresentSuccess isEqualToString:resultCode]) {
              NSLog(@"授权页拉起成功回调：%@", resultDic);
          } else if ([PNSCodeLoginControllerClickCancel isEqualToString:resultCode] ||
                     [PNSCodeLoginControllerClickChangeBtn isEqualToString:resultCode] ||
                     [PNSCodeLoginControllerClickLoginBtn isEqualToString:resultCode] ||
                     [PNSCodeLoginControllerClickCheckBoxBtn isEqualToString:resultCode] ||
                     [PNSCodeLoginControllerClickProtocol isEqualToString:resultCode]) {
              NSLog(@"页面点击事件回调：%@", resultDic);
          } else if ([PNSCodeSuccess isEqualToString:resultCode]) {
              NSLog(@"获取LoginToken成功回调：%@", resultDic);
              NSString *token = [resultDic objectForKey:@"token"];
              NSLog(@"接下来可以拿着Token去服务端换取手机号，有了手机号就可以登录，SDK提供服务到此结束");
              //[weakSelf dismissViewControllerAnimated:YES completion:nil];
              [[TXCommonHandler sharedInstance] cancelLoginVCAnimated:YES complete:nil];
              result(token);
          } else {
              NSLog(@"获取LoginToken或拉起授权页失败回调：%@", resultDic);
          }
      }];
//      result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)gotoSmsControllerAndShowNavBar {
    NSLog(@"拉起授权页面gotoSmsControllerAndShowNavBar");
//    PNSSmsLoginController *controller = [[PNSSmsLoginController alloc] init];
//    UIViewController *controller = [UIApplication sharedApplication].delegate.window.rootViewController;
//    controller.isHiddenNavgationBar = NO;
//    if (self.presentedViewController) {
//        //找到授权页的导航控制器
//        [(UINavigationController *)self.presentedViewController pushViewController:controller animated:YES];
//    }
}

- (void)gotoSmsControllerAndHiddenNavBar {
    NSLog(@"拉起授权页面gotoSmsControllerAndHiddenNavBar");
//    PNSSmsLoginController *controller = [[PNSSmsLoginController alloc] init];
//    controller.isHiddenNavgationBar = YES;
//    if (self.presentedViewController) {
//        //找到授权页的导航控制器
//        [(UINavigationController *)self.presentedViewController pushViewController:controller animated:YES];
//    }
    
    [[TXCommonHandler sharedInstance] cancelLoginVCAnimated:YES complete:nil];
}

@end
