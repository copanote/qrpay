#!/bin/bash
set -e  # 명령어 하나라도 실패하면 즉시 중단

# 1. 입력인자 확인
if [ -z "$1" ]; then
    echo "사용법: ./stop-app-local.sh <profile>"
    echo ""
    echo "예제:"
    echo "  ./stop-app-local.sh local"
    echo "  ./stop-app-local.sh dev"
    echo "  ./stop-app-local.sh prod"
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


#podman stop my-app

# 중지된 컨테이너 다시 시작
#podman start my-app

# 컨테이너 강제 종료 (응답 없을 때)
#podman kill my-app

