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

### Application

To build the docker image for the application run:

```bash
docker build . -t bft
```

### Client

To build the client run:

```bash
docker build client/ bft-client
```

## Run

To run the project it is required two terminals.

In one terminal run:

```bash
docker-compose up
```

In case you don’t docker-compose go to [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/)

In the other terminal run the client:

```bash
docker run -it --network csdproject_bft bft-client
```

## Client

The client contains two states. The initial state is to decide if you want to login to a internalUser, fill the application with some data or do a benchmark.

The benchmark command will generate the users, accounts and generate 10000 transactions on 4 threads

```
lu <userId> <password> ---- Start internalUser session
fill ---- Fills the ledger with data
benchmark ---- Starts a pull of threads and generates transactions.
help
exit
```

On the second state you have access to 4 users:

internalUser: user1 password: user1

internalUser: user2 password: user2

internalUser: user3 password: user3

internalUser: user4 password: user4

to login a internalUser you have to run:

```
lu user1 user1
```

then the prompt will change to:

```
user1>
```

In this state the program is loaded with the key pair of the user1 and every write call function will be signed.

In this state you will have access to the following commands:

```
ca <accountId> ---- create account
cu ---- create current internalUser in the system
chu <userId> <password> ---- Change to another internalUser
lu ---- list all users
lm <accountId> <value> ---- Load Money
st <from_accountId> <to_accountId> <value> ---- Send Transaction
gb <accountId> ---- get Balance
ge <accountId> ---- get Extract
ggv ---- get global Value
gtv <account1> <account2> ... <accountN> ---- get total value
gl ---- get ledger
help
exit
user1>
```

cu — registers the current logged in internalUser in the system

ca — creates a new account on user1

chu — changes to a new internalUser

st — sends a transaction from the accountId to another accountId registered in the system with the given value