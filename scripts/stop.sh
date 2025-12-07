#!/bin/bash

echo "⏹️  서비스 종료 중..."
echo ""

# Docker Compose 종료
echo "🐳 Docker 컨테이너 종료 중..."
docker-compose down

# 로컬 PostgreSQL도 종료
docker stop postgres-mfg 2>/dev/null

echo ""
echo "✅ 모든 서비스가 종료되었습니다."
echo ""
