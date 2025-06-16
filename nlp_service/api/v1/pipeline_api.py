import asyncio
from fastapi import APIRouter, HTTPException
from models.query_model import QueryRequest
from services.pipeline_service import search_and_embed_pipeline

router = APIRouter()

@router.post("/pipeline/search")
async def search_and_embed(req: QueryRequest):
    try:
        # 동기 함수를 백그라운드 스레드로 실행해서 이벤트 루프 블락 방지
        loop = asyncio.get_event_loop()
        ids = await loop.run_in_executor(None, search_and_embed_pipeline, req.query)
        return {"ids": ids}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
