from sqlalchemy import Column, Integer, String, Text, DateTime, Index
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Identity

Base = declarative_base()

class ResearchDocument(Base):
    __tablename__ = "documents"

    id = Column(Integer, Identity(start=1, cycle=True), primary_key=True)
    title = Column(String(1000))
    contents = Column(Text)
    url = Column(String(1000), unique=True, index=True)
    datetime = Column(DateTime)

    # URL 인덱스 명시적 생성
    __table_args__ = (
        Index('idx_documents_url', 'url'),
    )