import os
import psycopg2

def connect_postgres():
    env = os.getenv("ENV", "local")
    host = "postgres" if env == "docker" else "localhost"

    try:
        conn = psycopg2.connect(
            host=host,
            database="user_db",
            user="rocket",
            password="qwer123!"
        )
        cursor = conn.cursor()
        print(f"[✅ PostgreSQL] 연결 성공: 환경={env}, host={host}")
        return conn, cursor
    except Exception as e:
        print(f"[❌ PostgreSQL] 연결 실패: {e}")
        return None, None
