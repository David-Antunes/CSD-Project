FROM python:3.9.12 as build
WORKDIR app
COPY requirements.txt .
RUN pip install -r requirements.txt

FROM build
COPY . .
CMD ["python3", "bft-client.py"]
