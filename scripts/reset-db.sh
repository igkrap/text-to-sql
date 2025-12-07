#!/bin/bash

echo "🗄️  데이터베이스 초기화..."
echo ""

read -p "모든 데이터가 삭제됩니다. 계속하시겠습니까? (y/n) " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "데이터베이스 컨테이너 제거 중..."
    docker-compose down -v

    echo "데이터베이스 재시작 중..."
    docker-compose up -d postgres

    echo ""
    echo "✅ 데이터베이스가 초기화되었습니다."
    echo "백엔드를 재시작하면 샘플 데이터가 자동으로 생성됩니다."
else
    echo "취소되었습니다."
fi
