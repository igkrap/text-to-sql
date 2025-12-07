#!/bin/bash

echo "🏭 제조 NLP SQL 질의 시스템 (Docker Compose)"
echo ""

# Docker Compose 실행
echo "🚀 Docker Compose로 모든 서비스 시작 중..."
docker-compose up -d

echo ""
echo "⏳ 서비스 준비 대기 중..."
sleep 10

echo ""
echo "✅ 모든 서비스가 시작되었습니다!"
echo ""
echo "📱 프론트엔드: http://localhost:3000"
echo "🔧 백엔드 API: http://localhost:8080/api"
echo "🗄️  PostgreSQL: localhost:5432"
echo ""
echo "로그 보기: docker-compose logs -f"
echo "종료하기: docker-compose down"
echo ""
