from sentence_transformers import SentenceTransformer
from typing import List
import numpy as np

class Embedder:
    def __init__(self, model_name: str = "intfloat/multilingual-e5-base", prefix: str = "query: "):
        self.model = SentenceTransformer(model_name)
        self.prefix = prefix

    def generate_embeddings(self, texts: List[str]) -> np.ndarray:
        """
        텍스트 리스트를 임베딩 벡터로 변환합니다.
        모델 특성상 prefix ("query: " 또는 "passage: ") 필요.
        """
        preprocessed_texts = [f"{self.prefix}{text}" for text in texts]
        return self.model.encode(preprocessed_texts, normalize_embeddings=True)
