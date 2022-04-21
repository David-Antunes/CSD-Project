# CSD Project

To build docker image run:

```bash
docker build . -t bft
```

To run bft without bft-smart run:

```bash
docker run -it -p 8080:8080 bft
```

To run bft with bft-smart you have two options:

* Or you run locally using:

    ```bash
    export BFT_ID=$1; export SPRING_PROFILES_ACTIVE=bftsmart; java -jar build/libs/bft-smart-0.0.1-SNAPSHOT.jar com.csd.bftsmart.BftSmartApplication
    ```

    which is the bftsmart-start.sh.

    Open one terminal and run

    ```bash
    ./bftsmart-start.sh 0
    ```

    for 1 2 3 replicas


* Or you run with docker-compose:

    ```bash
    docker-compose up
    ```

keep in mind that in docker-compose the ips are different for each container. To configure docker-compose to run you have to change the hosts.config from this to

```bash
0 127.0.0.1 11000 11001
1 127.0.0.1 11010 11011
2 127.0.0.1 11020 11021
3 127.0.0.1 11030 11031
```

to this

```bash
0 172.20.0.2 11000 11001
1 172.20.0.3 11010 11011
2 172.20.0.4 11020 11021
3 172.20.0.5 11030 11031
```

and to also delete the currentView file as this contains the previous ips

after that run

```bash
docker build . -t bft
```

if you haven’t change any code, this is going to be very fast, if you have change source code then it will recompile.

To enable HTTPS you must set SPRING_PROFILES_ACTIVE=tls,(other profiles) and KEYSTORE_PASSWORD=password