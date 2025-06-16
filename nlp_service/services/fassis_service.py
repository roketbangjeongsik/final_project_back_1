import os
import faiss
import numpy as np
import logging
from typing import Dict, List, Optional

# 로거 설정
logger = logging.getLogger(__name__)

# 설정
EMBEDDING_DIM = 768  # E5 기준
INDEX_PATH = "faiss.index"
ID_MAP_PATH = "faiss_id_map.npy"  # ID 매핑 정보 저장 경로

# 전역 변수
index = None
id_to_index: Dict[int, int] = {}  # 문서 ID -> FAISS 인덱스 매핑

def get_faiss_stats():
    """FAISS 인덱스의 현재 상태를 반환합니다."""
    if index is None:
        return "FAISS 인덱스가 초기화되지 않았습니다."
    
    stats = {
        "total_vectors": index.ntotal,
        "dimension": index.d,
        "is_trained": index.is_trained,
        "metric_type": index.metric_type,
        "id_mapping_count": len(id_to_index)
    }
    
    logger.info(f"FAISS 인덱스 상태: {stats}")
    return stats

def load_id_mapping():
    """ID 매핑 정보를 로드합니다."""
    global id_to_index
    if os.path.exists(ID_MAP_PATH):
        try:
            id_to_index = np.load(ID_MAP_PATH, allow_pickle=True).item()
            logger.info(f"ID 매핑 정보 로드 완료: {len(id_to_index)}개")
        except Exception as e:
            logger.error(f"ID 매핑 정보 로드 실패: {e}")
            id_to_index = {}

def save_id_mapping():
    """ID 매핑 정보를 저장합니다."""
    try:
        np.save(ID_MAP_PATH, id_to_index)
        logger.info(f"ID 매핑 정보 저장 완료: {len(id_to_index)}개")
    except Exception as e:
        logger.error(f"ID 매핑 정보 저장 실패: {e}")

def init_faiss_index():
    """FAISS 인덱스를 초기화하고 ID 매핑 정보를 로드합니다."""
    global index
    
    # ID 매핑 정보 로드
    load_id_mapping()
    
    if os.path.exists(INDEX_PATH):
        logger.info("🔄 FAISS 인덱스 로드 중...")
        try:
            index = faiss.read_index(INDEX_PATH)
            logger.info(f"✅ FAISS 인덱스 로드 완료. 현재 벡터 수: {index.ntotal}")
            get_faiss_stats()
        except Exception as e:
            logger.error(f"❌ FAISS 인덱스 로드 실패: {e}")
            # 로드 실패시 새로 생성
            index = faiss.IndexFlatIP(EMBEDDING_DIM)
            id_to_index.clear()
            logger.info("🆕 FAISS 인덱스 새로 생성")
    else:
        logger.info("🆕 FAISS 인덱스 새로 생성")
        index = faiss.IndexFlatIP(EMBEDDING_DIM)
        id_to_index.clear()

def save_faiss_index():
    """FAISS 인덱스와 ID 매핑 정보를 저장합니다."""
    if index is not None:
        try:
            faiss.write_index(index, INDEX_PATH)
            save_id_mapping()
            logger.info(f"💾 FAISS 인덱스 저장 완료. 현재 벡터 수: {index.ntotal}")
            get_faiss_stats()
        except Exception as e:
            logger.error(f"❌ FAISS 인덱스 저장 실패: {e}")
            raise

def add_to_faiss_index(vectors: List[List[float]], doc_ids: List[int]):
    """벡터를 FAISS 인덱스에 추가하거나 업데이트합니다."""
    if index is None:
        raise RuntimeError("FAISS 인덱스가 초기화되지 않았습니다. init_faiss_index() 먼저 호출하세요.")

    try:
        np_vectors = np.array(vectors).astype("float32")
        
        # 각 문서 ID에 대해 처리
        for i, doc_id in enumerate(doc_ids):
            if doc_id in id_to_index:
                # 기존 벡터 업데이트
                idx = id_to_index[doc_id]
                index.reconstruct(idx, np_vectors[i])  # 벡터 업데이트
                logger.info(f"문서 ID {doc_id}의 벡터 업데이트 (인덱스: {idx})")
            else:
                # 새 벡터 추가
                idx = index.ntotal
                index.add(np_vectors[i:i+1])  # 한 개의 벡터만 추가
                id_to_index[doc_id] = idx
                logger.info(f"문서 ID {doc_id}의 벡터 추가 (인덱스: {idx})")
        
        logger.info(f"벡터 처리 완료. 현재 총 벡터 수: {index.ntotal}, ID 매핑 수: {len(id_to_index)}")
        get_faiss_stats()
    except Exception as e:
        logger.error(f"❌ 벡터 추가/업데이트 실패: {e}")
        raise

def search_faiss_index(query_vector: List[float], top_k: int = 5) -> List[int]:
    """쿼리 벡터와 유사한 문서 ID들을 반환합니다."""
    if index is None:
        raise RuntimeError("FAISS 인덱스가 초기화되지 않았습니다. init_faiss_index() 먼저 호출하세요.")

    try:
        query = np.array([query_vector]).astype("float32")
        scores, retrieved_indices = index.search(query, top_k)
        
        # 인덱스를 문서 ID로 변환
        result_ids = []
        for idx in retrieved_indices[0]:
            if idx != -1:  # -1은 검색 실패
                # 인덱스에 해당하는 문서 ID 찾기
                for doc_id, faiss_idx in id_to_index.items():
                    if faiss_idx == idx:
                        result_ids.append(doc_id)
                        break
        
        logger.info(f"검색된 문서 IDs: {result_ids}, 점수: {scores[0][:len(result_ids)]}")
        return result_ids
    except Exception as e:
        logger.error(f"❌ 검색 실패: {e}")
        raise
