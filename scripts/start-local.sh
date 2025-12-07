#!/bin/bash

echo "🏭 제조 NLP SQL 질의 시스템 시작..."
echo ""

# PostgreSQL 시작
echo "📦 PostgreSQL 컨테이너 시작 중..."
docker run --name postgres-mfg \
  -e POSTGRES_DB=manufacturing_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15 2>/dev/null || docker start postgres-mfg

echo "⏳ PostgreSQL 준비 대기 중..."
sleep 5

# 백엔드 시작
echo ""
echo "🚀 백엔드 서버 시작 중..."
cd backend
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ..

echo "⏳ 백엔드 시작 대기 중..."
sleep 15

# 프론트엔드 시작
echo ""
echo "🎨 프론트엔드 서버 시작 중..."
cd frontend
npm install
npm run dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "✅ 모든 서비스가 시작되었습니다!"
echo ""
echo "📱 프론트엔드: http://localhost:3000"
echo "🔧 백엔드 API: http://localhost:8080/api"
echo "🗄️  PostgreSQL: localhost:5432"
echo ""
echo "종료하려면 Ctrl+C를 누르세요"
echo ""

# 종료 핸들러
trap "echo ''; echo '⏹️  서비스 종료 중...'; kill $BACKEND_PID $FRONTEND_PID; docker stop postgres-mfg; exit" INT

# 대기
wait
