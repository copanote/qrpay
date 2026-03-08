/*
 * Copyright (C) Arxan Technologies Inc. 2001-2017.
 * All rights Reserved.
 *
 * This is the GuardSpec program for the ios simple simon target.
 */

#include "EnsureIT.h"

// NATIVEOBJECT should be defined on the command line when compiling the GuardSpec
// using -DNATIVEOBJECT=/path/to/native/object, where "/path/to/native/object" is the
// architecture specific native object file. You may define it in the GuardSpec if it is
// not already defined on the command line when submitting the GuardSpec to the Level 2+
// Protection utility. #ifndef NATIVEOBJECT
#define NATIVEOBJECT /some/default/path
#endif

#define XSTRINGIFY(s) #s
#define STRINGIFY(s) XSTRINGIFY(s)

int main(int argc, char* argv[])
{
  eit::GuardSpec gs;

  // Increase obfuscation of all Guards
  gs.obfuscate(gs.allGuards(), 200);

  // Obfuscate entire source bitcode (not including Guards)
  gs.obfuscate(gs.sourceBitcode(), 300);

  // Obfuscate CheckPass even more
//  gs.obfuscate(gs.function("CheckPass"), 800);
  // Read in the files that have information for Objective-C renaming (iOS specific)-
  gs.rename("appDelegate.rename").setAlgorithm("binary");
    


  // Add a debugger detection guard
  gs.detectDebugger(
    /* name   */ "level_1_debugger_detection_in_function",
    /* invoc  */ gs.function("view", eit::SUBSTRING).entry()
    ).setExecutionProbability(1).setTamperAction("fail");

  // Use the string encryption guard
  gs.encryptStrings("level_1_string_encryption",
    /* ranges */    gs.sourceBitcode());

  
  // Detect if C/C++ function calls from system or user library have been overridden
  gs.detectHooking(
    /* name   */    "level_1_hook_detection",
    /* invoc  */    gs.function("\01-[AppDelegate application:didFinishLaunchingWithOptions:]").entry()
    ).setExecutionProbability(1).setTamperAction("fail").addCommonlyHookedFunctions();

  // Begin iOS specific guards

  // Detect swizzling to external addresses (iOS specific)
  gs.detectSwizzling(
    /* name   */ "level_1_swizzling_detection",
    /* invoc  */ gs.function("\01-[AppDelegate application:didFinishLaunchingWithOptions:]").entry()
    ).setTamperAction("fail").setAlgorithm("strict");

  // Detect if device can be jail broken (iOS specific)
  gs.detectJailbreak(
    /* name   */ "level_1_jailbreak_detection",
    /* invoc  */ gs.function("\01-[ViewController viewDidLoad]").entry()
    ).setTamperAction("fail");

  // End iOS specific guards

  //-----------------------------
  // Start of L2P Guard Network  
  //-----------------------------
    // Checksum all source bitcode
    gs.checksum(
                /* name   */ "level_1_checksum_of_src_bitcode",
                /* invoc  */ gs.function("\01-[ViewController viewDidLoad]").entry(),
                /* ranges */ gs.sourceBitcode()
                ).setTamperAction("fail");
    
    
    // Checksum all source bitcode
    gs.checksum(
                /* name   */ "level_1_checksum_of_src_bitcode_2",
                /* invoc  */ gs.function("\01-[AppDelegate application:didFinishLaunchingWithOptions:]").entry(),
                /* ranges */ gs.sourceBitcode()
                ).setTamperAction("fail");
  // L2 Guards
  gs.checksum(
              "L2_Auto_checksum_2",
              gs.function("\01-[PushAllowViewController viewDidLoad]").entry(),
              gs.guard("level_1_debugger_detection_in_function") +
              gs.guard("level_1_string_encryption")
    ).setExecutionProbability(0.50000f).setTamperAction("fail").setNonTamperAction("do_nothing");
  gs.checksum(
              "L2_Auto_checksum_3",
               gs.function("\01-[QRShareViewController viewDidLoad]").entry(),
              gs.guard("level_1_hook_detection") +
              gs.guard("level_1_swizzling_detection")
    ).setExecutionProbability(0.50000f).setTamperAction("fail").setNonTamperAction("do_nothing");
  gs.checksum(
              "L2_Auto_checksum_4",
               gs.function("\01-[ViewController viewDidLoad]").entry(),
              gs.guard("level_1_jailbreak_detection") +
              gs.guard("level_1_checksum_of_src_bitcode_2") +
              gs.guard("level_1_checksum_of_src_bitcode")
    ).setExecutionProbability(0.50000f).setTamperAction("fail").setNonTamperAction("do_nothing");


  //-----------------------------
  // End of L2P Guard Network    
  //-----------------------------
  
  // set randomization seed for deterministic reproducibility
  gs.seed(180322);

  // install protection, with command-line overrides
  gs.execute(argc, argv);

  return 0;
}

