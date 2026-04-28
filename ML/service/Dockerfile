FROM python:3.13-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY ML/service/requirements.txt ./requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

COPY ML/service/service /app/service

EXPOSE 8000

CMD ["uvicorn", "service.app:app", "--host", "0.0.0.0", "--port", "8000"]
