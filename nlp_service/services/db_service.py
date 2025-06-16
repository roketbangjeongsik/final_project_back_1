from typing import List
from db.database import get_db  # 세션 연결 함수 필요
from models.research_document import ResearchDocument  # SQLAlchemy 모델
from sqlalchemy import update


def save_documents_to_db(session, documents: List[dict]) -> List[int]:
    doc_ids = []
    try:
        for doc in documents:
            # URL로 기존 문서 조회
            existing_doc = session.query(ResearchDocument).filter(ResearchDocument.url == doc["url"]).first()
            
            if existing_doc:
                # 기존 문서가 있으면 업데이트
                existing_doc.title = doc["title"]
                existing_doc.contents = doc["contents"]
                existing_doc.datetime = doc["datetime"]
                doc_ids.append(existing_doc.id)
            else:
                # 새 문서면 추가
                new_doc = ResearchDocument(
                    title=doc["title"],
                    contents=doc["contents"],
                    url=doc["url"],
                    datetime=doc["datetime"]
                )
                session.add(new_doc)
                session.flush()  # id 받기 위해
                doc_ids.append(new_doc.id)
                
        session.commit()
    except Exception:
        session.rollback()
        raise
    return doc_ids

