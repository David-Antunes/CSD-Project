FROM python:3.9.12 as BUILD
WORKDIR app
COPY requirements.txt .
RUN pip install -r requirements.txt

FROM BUILD
COPY . .
CMD ["python3", "bft-client.py"]