# 베이스 이미지: Python 3.10 slim
FROM python:3.10

# 시스템 패키지 업데이트 및 curl 설치 (필요 시)
RUN apt-get update && apt-get install -y curl net-tools && apt-get clean

# 작업 디렉터리 설정
WORKDIR /app

# 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . .

# FastAPI 기본 포트 8000 노출
EXPOSE 8000

# 컨테이너 시작 시 FastAPI 서버 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
