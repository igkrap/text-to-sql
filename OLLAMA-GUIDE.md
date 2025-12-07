# 🤖 Ollama 로컬 LLM 설정 가이드

이 가이드는 Ollama를 사용하여 로컬 LLM을 설정하는 방법을 설명합니다.

## Ollama란?

Ollama는 로컬 환경에서 대형 언어 모델(LLM)을 실행할 수 있게 해주는 도구입니다. 인터넷 연결 없이도 AI 모델을 실행할 수 있으며, 데이터가 외부로 전송되지 않아 프라이버시가 보장됩니다.

## 설치 방법

### macOS

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

### Linux

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

### Windows

1. https://ollama.com/download 방문
2. Windows용 설치 파일 다운로드
3. 설치 실행
4. 설치 후 자동으로 백그라운드 서비스로 실행됨

### Docker

```bash
docker run -d -p 11434:11434 --name ollama ollama/ollama
```

## 모델 다운로드

### 권장 모델

#### llama3.2:3b (기본값, 권장)
- **용량**: ~2GB
- **메모리**: 4GB 이상 권장
- **속도**: 빠름
- **정확도**: 우수
- **사용 예**:
```bash
ollama pull llama3.2:3b
```

#### llama3.2:1b (경량)
- **용량**: ~1GB
- **메모리**: 2GB 이상
- **속도**: 매우 빠름
- **정확도**: 양호
- **사용 예**:
```bash
ollama pull llama3.2:1b
```

#### llama3.1:8b (고성능)
- **용량**: ~4.7GB
- **메모리**: 8GB 이상 권장
- **속도**: 보통
- **정확도**: 매우 우수
- **사용 예**:
```bash
ollama pull llama3.1:8b
```

#### codellama:7b (SQL 특화)
- **용량**: ~3.8GB
- **메모리**: 8GB 이상 권장
- **속도**: 보통
- **정확도**: SQL/코드에 특화
- **사용 예**:
```bash
ollama pull codellama:7b
```

## Ollama 실행 및 테스트

### 서비스 시작

#### macOS / Linux:
```bash
ollama serve
```

#### Windows:
설치 후 자동으로 실행됩니다. 시작 메뉴에서 "Ollama" 검색

#### Docker:
```bash
docker start ollama
```

### 연결 테스트

```bash
# API 확인
curl http://localhost:11434/api/tags

# 모델 목록 확인
ollama list
```

### 간단한 테스트

```bash
ollama run llama3.2:3b "SELECT 쿼리 작성: 모든 제품 조회"
```

## 프로젝트에서 사용하기

### 1. 모델 설정 변경

`backend/src/main/resources/application.yml` 파일에서 모델 변경:

```yaml
ollama:
  base-url: http://localhost:11434
  model: llama3.2:3b  # 여기를 변경
  timeout: 120
```

### 2. Docker Compose 사용 시

`docker-compose.yml` 파일에서 환경변수 변경:

```yaml
backend:
  environment:
    OLLAMA_MODEL: llama3.2:3b  # 여기를 변경
```

### 3. 모델 다운로드 확인

애플리케이션 시작 전에 모델이 다운로드되었는지 확인:

```bash
ollama list
```

출력 예:
```
NAME              ID              SIZE      MODIFIED
llama3.2:3b      abc123def       2.0 GB    2 days ago
```

## 성능 최적화

### GPU 사용

Ollama는 자동으로 GPU를 감지하고 사용합니다:

- **NVIDIA GPU**: CUDA 자동 사용
- **AMD GPU**: ROCm 지원
- **Apple Silicon**: Metal 자동 사용
- **CPU only**: CPU로 실행 (느림)

### 메모리 설정

모델 실행 시 사용할 최대 메모리 설정:

```bash
# 환경변수로 설정
OLLAMA_MAX_LOADED_MODELS=1 ollama serve
```

### 동시 실행 모델 수 제한

```bash
# 하나의 모델만 메모리에 로드
OLLAMA_MAX_LOADED_MODELS=1 ollama serve
```

## 문제 해결

### Ollama 연결 오류

```bash
# 1. Ollama 프로세스 확인
ps aux | grep ollama

# 2. 포트 확인
lsof -i :11434

# 3. Ollama 재시작
killall ollama
ollama serve
```

### 모델 다운로드 느림

```bash
# 다운로드 중단 후 재시도
ollama pull llama3.2:3b
```

### 메모리 부족 오류

더 작은 모델 사용:
```bash
ollama pull llama3.2:1b
```

또는 스왑 메모리 증가:
```bash
# Linux
sudo fallocate -l 8G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Docker 권한 오류

```bash
# Docker 볼륨 권한 수정
docker run -d \
  -p 11434:11434 \
  -v ollama:/root/.ollama \
  --user $(id -u):$(id -g) \
  ollama/ollama
```

## 모델 비교

| 모델 | 크기 | 메모리 | 속도 | SQL 정확도 | 추천 용도 |
|------|------|--------|------|------------|----------|
| llama3.2:1b | ~1GB | 2GB+ | ⚡⚡⚡ | ⭐⭐⭐ | 테스트/학습 |
| llama3.2:3b | ~2GB | 4GB+ | ⚡⚡ | ⭐⭐⭐⭐ | 일반 사용 (권장) |
| llama3.1:8b | ~5GB | 8GB+ | ⚡ | ⭐⭐⭐⭐⭐ | 고정확도 필요 |
| codellama:7b | ~4GB | 8GB+ | ⚡ | ⭐⭐⭐⭐⭐ | SQL/코드 특화 |

## 보안 및 프라이버시

Ollama의 장점:

- ✅ **완전 로컬 실행**: 인터넷 불필요
- ✅ **데이터 프라이버시**: 데이터가 외부로 전송되지 않음
- ✅ **비용 무료**: API 비용 없음
- ✅ **오프라인 작동**: 인터넷 연결 없이 사용 가능

## 추가 리소스

- [Ollama 공식 홈페이지](https://ollama.com/)
- [Ollama GitHub](https://github.com/ollama/ollama)
- [사용 가능한 모델 목록](https://ollama.com/library)
- [Ollama API 문서](https://github.com/ollama/ollama/blob/main/docs/api.md)

## 자주 묻는 질문

### Q: Ollama는 인터넷이 필요한가요?
A: 모델 다운로드 시에만 인터넷이 필요하며, 이후 실행은 완전 오프라인으로 가능합니다.

### Q: 여러 모델을 동시에 사용할 수 있나요?
A: 가능하지만, 메모리 사용량이 증가합니다. 일반적으로 하나의 모델 사용을 권장합니다.

### Q: GPU가 없어도 사용할 수 있나요?
A: 가능하지만, CPU로 실행되어 속도가 느릴 수 있습니다. 작은 모델(1b)을 권장합니다.

### Q: Windows에서 GPU를 사용할 수 있나요?
A: NVIDIA GPU가 있고 CUDA가 설치되어 있으면 자동으로 GPU를 사용합니다.

### Q: 모델을 삭제하려면?
A: `ollama rm llama3.2:3b` 명령어를 사용하세요.
