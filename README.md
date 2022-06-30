# CSD

# CSD Project

Author: André Palma Santos 55415

Author: David Antunes 55045

## Build project

To have a full functioning prototype it is required to build the docker image for the application using bft-smart

### Full build

To build the whole project including client and application run:

```bash
./build.sh
```

### BFT_SMART

To Build the system running BFT-SMART run:

```bash
docker-compose -f server/rest/docker-compose.yaml up
```

### PROXY

To use proxy for SGX run:

```bash
docker-compose -f server/sgx-proxy/docker-compose.yaml up
```

### Client

To build the client run:

```bash
docker build -t bft-client -f server/client/Dockerfile .
```

### Run the client

To run the client you have two possible choices:

#### Without Proxy

```bash
docker run -it --network rest_bft bft-client
```

#### With Proxy

```bash
docker run -it --network sgx-proxy_bft -e PROXY=https://172.20.0.6:8443 bft-client
```
This way the sendTransaction request will go to the proxy instead of a replica

```bash
docker-compose up
```

In case you don’t docker-compose go to [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

In the other terminal run the client:

```bash
docker run -it --network csdproject_bft bft-client
```

## Client

```                   
ca <accountId> ---- create account
cu ---- create current user in the system
chu <userId> <password> ---- Change to another user
lm <accountId> <value> ---- Load Money
st <from_accountId> <to_accountId> <value> ---- Send Transaction
gb <accountId> ---- get Balance
ge <accountId> ---- get Extract
ggv ---- get global Value
gtv <accountId_1> <accountId_2> ... <accountId_N> ---- get total value
gl ---- get ledger
gu --- get all users
ga --- get all accounts
gt --- get all transactions
mine --- toggle mining blocks
fill --- fill the system with users,accounts and transactions
help
exit
```


### Benchmark

To Run the benchmark:

#### Docker

```bash
docker run -it --network rest_bft -e BENCHMARK_PATH=config/workloads/workload.properties benchmark
```

or locally:

```bash
java -Djavax.net.ssl.trustStore=config/users.pkcs12 -Djavax.net.ssl.trustStorePassword=users -Dhttps.protocols=TLSv1.3 -Djavax.net.ssl.keyStoreType=pkcs12 -jar server/benchmark/build/libs/benchmark-0.0.1-SNAPSHOT.jar com.csd.blockneat.Main config/workloads/workload.properties
```

There are two possible workloads, mining and api.
Workload API will test the REST API of the system without mining any blocks.
Workload MINING will test the Blockchain performance by writing and mining at the same time.

It will be generated a folder called results that will contain the data of the tests. Keep in mind that if you are running inside of docker it is needed to copy the files from the inside of the docker.

Successive runs will remove the already written files.