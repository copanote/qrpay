Proguardmapping 파일 문제 발생시  

-- 공백 2줄 삭제후 재 업로드 처리 

> Axarn 솔루션 라이센스 갱신  
> A4A 경로 : /Applications/A4A_4.11.1/bin/  
> Command : 키 요청파일 생성 : secure-dex —license-offline-setup  
> Command : 키 라이센스 기간 보기 : secure-dex —license-display  
  
> EnsureIT 경로 : /Applications/EnsureIT-13.1.0.a0de9d5-macosx-x64-ios-arm.app/Contents/EnsureIT/bin  
> Command : 키 요청파일 생성 : ensureit —license-offline-setup  
> Command : 키 라이센스 기간 보기 : ensureit —license-display  


> bulid.gradle 에 buildTypes 의 debug , release 로 구분 되어있음
> 안드로이드 스튜디오의 Build Variants 의 app 부분을 설정해서 처리 하고  arxan_build.sh에 비밀번호 확인
> Constant.DROID_X_RUN 도 수정해서 처리해야함.