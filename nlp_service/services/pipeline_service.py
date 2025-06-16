import logging
from typing import List
from services.kakao_service import search_kakao_documents
from services.db_service import save_documents_to_db
from db.database import SessionLocal
from services.embedding_service import Embedder
from services.fassis_service import add_to_faiss_index, search_faiss_index, init_faiss_index, save_faiss_index, get_faiss_stats

# 로거 설정 (원하는 곳에서 한 번만 설정하세요)
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

def search_and_embed_pipeline(query: str) -> List[int]:
    try:
        logger.info("1. Kakao API로 문서 검색 시작")
        documents = search_kakao_documents(query)
        logger.info(f"1. Kakao API로 {len(documents)}개 문서 검색 완료")

        logger.info("2. DB에 문서 저장 시작")
        with SessionLocal() as session:
            doc_ids = save_documents_to_db(session, documents)
        logger.info(f"2. DB에 {len(doc_ids)}개 문서 저장 완료 (IDs: {doc_ids})")

        logger.info("3. 임베딩 생성 시작")
        embedder = Embedder()
        texts = [f"{doc['title']} {doc['contents']}" for doc in documents]
        vectors = embedder.generate_embeddings(texts)
        logger.info(f"3. 임베딩 {len(vectors)}개 생성 완료")

        logger.info("4. FAISS 인덱스 초기화 및 벡터 추가 시작")
        init_faiss_index()
        # FAISS 초기 상태 로깅
        logger.info("4. FAISS 초기 상태:")
        get_faiss_stats()
        
        add_to_faiss_index(vectors, doc_ids)
        save_faiss_index()
        # FAISS 최종 상태 로깅
        logger.info("4. FAISS 최종 상태:")
        get_faiss_stats()
        logger.info(f"4. FAISS 인덱스에 벡터 추가 및 저장 완료")

        logger.info("5. 쿼리 임베딩 생성 및 유사 문서 검색 시작")
        query_embedding = embedder.generate_embeddings([query])[0]
        similar_doc_ids = search_faiss_index(query_embedding, top_k=5)
        logger.info(f"5. 유사 문서 검색 완료: {similar_doc_ids}")

        return similar_doc_ids

    except Exception as e:
        logger.error(f"파이프라인 처리 중 에러 발생: {e}", exc_info=True)
        raise
