import jks
import OpenSSL
import base64
import requests
import urllib3
import warnings
import random as rand
import concurrent.futures
import json

warnings.filterwarnings("ignore", category=DeprecationWarning)
urllib3.disable_warnings()


_ASN1 = OpenSSL.crypto.FILETYPE_ASN1
_KEYSTORE_FILE = 'users.jks'
_KEYSTORE_PASS = 'user1'
_BASE_URL = 'https://172.20.0.2:8443'
_USERS = '/users'
_ACCOUNTS = "/accounts"
_TRANSACTIONS = "/transactions"
_LEDGER = "/ledger"

rand.seed(1111)

keystore = jks.KeyStore.load(_KEYSTORE_FILE, _KEYSTORE_PASS)


def generate_users():
    load_user("user1", "user1")
    create_user()
    for val in range(1, 6):
        i = str(val)
        create_account("account1" + i)
        loadMoney("account1" + i, str(rand.randint(1, 1000000)))
        print("created account " + "account1" + i + " with " + str(rand.randint(1, 1000000)) + " money.")
    load_user("user2", "user2")
    create_user()
    for val in range(1, 6):
        i = str(val)
        create_account("account2" + i)
        loadMoney("account2" + i, str(rand.randint(1, 1000000)))
        print("created account " + "account2" + i + " with " + str(rand.randint(1, 1000000)) + " money.")
    load_user("user3", "user3")
    create_user()
    for val in range(1, 6):
        i = str(val)
        create_account("account3" + i)
        loadMoney("account3" + i, str(rand.randint(1, 1000000)))
        print("created account " + "account3" + i + " with " + str(rand.randint(1, 1000000)) + " money.")
    load_user("user4", "user4")
    create_user()
    for val in range(1, 6):
        i = str(val)
        create_account("account4" + i)
        loadMoney("account3" + i, str(rand.randint(1, 1000000)))
        print("created account " + "account4" + i + " with " + str(rand.randint(1, 1000000)) + "money.")


def fill():
    generate_users()
    for val in range(1, 4):
        i = str(val)
        load_user("user" + i, "user" + i)
        for j in range(1, 10):
            print("Sent transaction from account" + i + str(rand.randint(1, 5)) + " to account" + str(
                rand.randint(1, 4)) + str(rand.randint(1, 5)) + "\t" + str(rand.randint(1, 100)))
            send_transaction("account" + i + str(rand.randint(1, 5)),
                             "account" + str(rand.randint(1, 4)) + str(rand.randint(1, 5)), str(rand.randint(1, 100)))

    return False


def thread_function(index):
    global keystore
    _pkey = keystore.private_keys["user" + str(index)]
    _pkey.decrypt("user" + str(index))
    _priv_key = OpenSSL.crypto.load_privatekey(_ASN1, _pkey.pkey)

    i = str(index)
    for j in range(10000):
        accountId = "account" + i + str(rand.randint(1, 5))
        to = "account" + str(rand.randint(1, 4)) + str(rand.randint(1, 5))
        value = str(rand.randint(1, 100))
        print("Thread " + i + ": Sent transaction from " + accountId + " to " + to + "\t" + str(rand.randint(1, 100)))

        json_body = {"from": accountId, "to": to, "value": value}

        signature = base64.b64encode(bytes(OpenSSL.crypto.sign(_priv_key, accountId + to + value, "sha512"))).decode(
            'utf-8')
        r = requests.post(_BASE_URL + _TRANSACTIONS, json=json_body, headers={'signature': str(signature)},
                          verify=False)
        if r.status_code != 200:
            print(str(r.content))


def benchmark():
    generate_users()

    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as executor:
        for i in range(5):
            executor.submit(thread_function, i)

    print("Benchmark done.")


def generatePostRequest(url, sign, json_body):
    response = base64.b64encode(bytes(OpenSSL.crypto.sign(priv_key, sign, "sha512"))).decode('utf-8')
    r = requests.post(url, json=json_body, headers={'signature': str(response)}, verify=False)
    if r.status_code != 200:
        print(str(r.content))


def list_users():
    r = requests.get(_BASE_URL + _USERS, verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))


def load_user(userId, password):
    try:
        global priv_key
        global pub_key
        global current_prompt
        global current_user
        pkey = keystore.private_keys[userId]
        pkey.decrypt(password)
        priv_key = OpenSSL.crypto.load_privatekey(_ASN1, pkey.pkey)
        cert = OpenSSL.crypto.load_certificate(_ASN1, pkey.cert_chain[0][1])
        pub_key = base64.b64encode(OpenSSL.crypto.dump_certificate(OpenSSL.crypto.FILETYPE_PEM, cert)).decode('utf-8')
        current_prompt = userId
        current_user = userId
        return True
    except:
        return False


def create_user():
    json_body = {"userId": {"email": current_user, "base64pk": str(pub_key)}}
    generatePostRequest(_BASE_URL + _USERS, current_user, json_body)


def create_account(accountId):
    print(accountId)
    json_body = {"userId": {"email": current_user, "base64pk": str(pub_key)}, "accountId": accountId}
    generatePostRequest(_BASE_URL + _ACCOUNTS, accountId, json_body)


def loadMoney(to, value):
    json_body = {"from": "", "to": to, "value": value}
    generatePostRequest(_BASE_URL + _TRANSACTIONS + "/loadMoney/" + to + "?value=" + value, to + value, json_body)


def send_transaction(accountId, to, value):
    json_body = {"from": accountId, "to": to, "value": value}
    generatePostRequest(_BASE_URL + _TRANSACTIONS, accountId + to + value, json_body)


def balance(accountId):
    r = requests.get(_BASE_URL + _ACCOUNTS + "/balance/" + accountId, verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))


def extract(accountId):
    r = requests.get(_BASE_URL + _TRANSACTIONS + "/extract/" + accountId, verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))


def ledger():
    r = requests.get(_BASE_URL + _LEDGER, verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))

def total_value(tokens):
    json_body = tokens
    r = requests.get(_BASE_URL + _TRANSACTIONS + "/total", json=json_body, verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))


def global_value():
    r = requests.get(_BASE_URL + _TRANSACTIONS + "/global", verify=False)
    parsed = json.loads(r.content)
    print(json.dumps(parsed, indent=4))


def initial_prompt():
    print()
    print("lu <userId> <password> ---- Start user session")
    print("fill ---- Fills the ledger with data")
    print("benchmark ---- Starts a pull of threads and generates transactions.")
    print("help")
    print("exit")


def auth_prompt():
    print("\nca <accountId> ---- create account")
    print("cu ---- create current user in the system")
    print("chu <userId> <password> ---- Change to another user")
    print("lu ---- list all users")
    print("lm <accountId> <value> ---- Load Money")
    print("st <from> <to> <value> ---- Send Transaction")
    print("gb <accountId> ---- get Balance")
    print("ge <accountId> ---- get Extract")
    print("ggv ---- get global Value")
    print("gtv <account1> <account2> ... <accountN> ---- get total value")
    print("gl ---- get ledger")
    print("help")
    print("exit")


def processInitialUser(tokens):
    if tokens[0] == "lu":
        if len(tokens[1:]) > 1 or len(tokens[1:]) < 2:
            return load_user(tokens[1], tokens[2])
        else:
            print("Invalid Argument.")
    if tokens[0] == "benchmark":
        return benchmark()
    if tokens[0] == "fill":
        return fill()
    if tokens[0] == "help":
        initial_prompt()
        return False
    if tokens[0] == "exit":
        exit(0)
    return False


def verify_num_of_args(tokens):
    if tokens[0] == "help":
        return True
    if tokens[0] == "cu":
        return True
    elif tokens[0] == "lu":
        return True
    elif tokens[0] == "cha":
        if len(tokens[1:]) < 2:
            print("Not enough args.")
            return False
    elif tokens[0] == "ca":
        if len(tokens[1:]) < 1:
            print("Not enough args.")
            return False
    elif tokens[0] == "lm":
        if len(tokens[1:]) == 2:
            print("Not enough args.")
            return False
    elif tokens[0] == "st":
        if len(tokens[1:]) == 3:
            print("Not enough args.")
            return False
    elif tokens[0] == "gb":
        if len(tokens[1:]) == 1:
            print("Not enough args.")
            return False
    elif tokens[0] == "ge":
        if len(tokens[1:]) == 1:
            print("Not enough args.")
            return False
    elif tokens[0] == "ggv":
        return True
    elif tokens[0] == "gtv":
        if len(tokens[1:]) == 0:
            print("Not enough args.")
            return False
    elif tokens[0] == "gl":
        return True
    else:
        print("Unknown command.")
        return False


def processToken(tokens):
    if tokens[0] == "help":
        auth_prompt()
    elif tokens[0] == "cu":
        create_user()
    elif tokens[0] == "lu":
        list_users()
    elif tokens[0] == "ca":
        create_account(tokens[1])
    elif tokens[0] == "chu":
        load_user(tokens[1], tokens[2])
    elif tokens[0] == "lm":
        loadMoney(tokens[1], tokens[2])
    elif tokens[0] == "st":
        send_transaction(tokens[1], tokens[2], tokens[3])
    elif tokens[0] == "gb":
        balance(tokens[1])
    elif tokens[0] == "ge":
        extract(tokens[1])
    elif tokens[0] == "ggv":
        global_value()
    elif tokens[0] == "gtv":
        total_value(tokens[1:])
    elif tokens[0] == "gl":
        ledger()
    elif tokens[0] == "exit":
        exit(0)
    else:
        print("Unknown command.")
        return
    return


current_user = ""
current_prompt = ""
priv_key = ""
pub_key = ""

initial_prompt()
while True:
    command = input("> ")
    tokens = command.split(" ")
    if processInitialUser(tokens):
        break
auth_prompt()
while True:
    command = input(current_prompt + "> ")
    tokens = command.split(" ")
    processToken(tokens)
