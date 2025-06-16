import logging
import requests
from typing import List, Dict, Tuple
from services.embedding_service import Embedder

logger = logging.getLogger(__name__)

KAKAO_API_KEY = "KakaoAK YOUR_REST_API_KEY"
KAKAO_BLOG_SEARCH_URL = "https://dapi.kakao.com/v2/search/blog"

def search_kakao_documents(query: str, size: int = 100) -> List[Dict]:
    """
    카카오 API로 문서를 검색하고 유사도 점수를 계산합니다.
    
    Args:
        query: 검색 쿼리
        size: 검색할 문서 수 (기본값: 100)
    """
    headers = {
        "Authorization": f"KakaoAK {KAKAO_API_KEY}"
    }
    params = {
        "query": query,
        "size": size
    }

    response = requests.get(KAKAO_BLOG_SEARCH_URL, headers=headers, params=params)
    response.raise_for_status()
    result = response.json()

    documents = []
    for item in result.get("documents", []):
        documents.append({
            "title": item["title"],
            "contents": item["contents"],
            "url": item["url"],
            "datetime": item.get("datetime")
        })

    # 유사도 점수 계산 (상위 5개만)
    embedder = Embedder()
    query_embedding = embedder.generate_embeddings([query])[0]
    doc_texts = [f"{doc['title']} {doc['contents']}" for doc in documents[:5]]  # 상위 5개만
    doc_embeddings = embedder.generate_embeddings(doc_texts)
    
    # 코사인 유사도 계산
    similarities = []
    for doc_embedding in doc_embeddings:
        similarity = (query_embedding @ doc_embedding) / (
            (query_embedding @ query_embedding) ** 0.5 * 
            (doc_embedding @ doc_embedding) ** 0.5
        )
        similarities.append(float(similarity))
    
    # 결과 로깅 (상위 5개만)
    logger.info("카카오 API 검색 결과 유사도 점수 (상위 5개):")
    for doc, score in zip(documents[:5], similarities):
        logger.info(f"- {doc['title']}: {score:.4f}")
    
    return documents
