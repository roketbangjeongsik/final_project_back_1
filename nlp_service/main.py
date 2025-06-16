from fastapi import FastAPI, HTTPException

#DB
from db.postgreSQL import connect_postgres

# api
# from api.v1 import embedding_api
from api.v1 import pipeline_api
app = FastAPI()


@app.get("/")
async def root():
    return {"message": "fastapi"}

# app.include_router(embedding_api.router)

app.include_router(pipeline_api.router)



#DB 테스트
@app.get("/postgres-test")
def postgres_test():
    conn, cursor = connect_postgres()
    if not conn or not cursor:
        raise HTTPException(status_code=500, detail="PostgreSQL 연결 실패")

    try:
        cursor.execute("SELECT NOW();")
        result = cursor.fetchone()
        return {"postgres_time": result[0].isoformat()}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        cursor.close()
        conn.close()


