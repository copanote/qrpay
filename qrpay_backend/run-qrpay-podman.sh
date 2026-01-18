#!/bin/bash
set -e  # 명령어 하나라도 실패하면 즉시 중단

# 1. 입력인자 확인
if [ -z "$1" ]; then
    echo "사용법: ./run-app-local.sh <profile>"
    echo ""
    echo "예제:"
    echo "  ./run-app-local.sh local"
    echo "  ./run-app-local.sh dev"
    echo "  ./run-app-local.sh prod"
    return 1
fi

# 2. 변수 설정
PROFILE="$1"
APP_NAME="qrpay-$PROFILE"
PORT=9090
IMAGE_NAME="qrpay-app:latest"

echo "🚀 [$APP_NAME] 배포 프로세스를 시작합니다..."

# 2. 기존 컨테이너 중지 및 삭제
if [ "$(podman ps -aq -f name=$APP_NAME)" ]; then
    echo "🛑 기존 컨테이너를 중지하고 삭제합니다..."
    podman stop $APP_NAME || true
    podman rm $APP_NAME || true
fi

# 3. Maven 빌드 (실패 탐지 로직 추가)
echo "📦 애플리케이션을 빌드합니다 (Maven)..."
mvn clean package -DskipTests

# [중요] 빌드 성공 여부 체크 ($? 는 직전 명령어의 종료 코드)
if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패! 스크립트를 중단합니다."
    exit 1
fi

# 4. Podman 이미지 빌드 (실패 탐지 로직 추가)
echo "🏗️ Podman 이미지를 빌드합니다..."
podman build -t $IMAGE_NAME .
podman build -t $IMAGE_NAME .

if [ $? -ne 0 ]; then
    echo "❌ 이미지 빌드 실패! 스크립트를 중단합니다."
    exit 1
fi

# 5. 컨테이너 실행
 # -d: 백그라운드 실행 (데몬) # -p: 호스트포트:컨테이너포트 연결 # --name: 컨테이너에 이름 부여
echo "🏃 컨테이너를 $PORT 포트에서 실행합니다 (Profile: $PROFILE)..."
podman run -d \
  -p $PORT:9090 \
  --name $APP_NAME \
  -e "SPRING_PROFILES_ACTIVE=$PROFILE" \
  $IMAGE_NAME

if [ $? -eq 0 ]; then
    echo "✅ 실행 완료! http://localhost:$PORT 에서 확인하세요."
    echo "📝 로그 확인 명령어: podman logs -f $APP_NAME"
else
    echo "❌ 컨테이너 실행 실패!"
    exit 1
fi

#podman stop my-app

# 중지된 컨테이너 다시 시작
#podman start my-app

# 컨테이너 강제 종료 (응답 없을 때)
#podman kill my-app

