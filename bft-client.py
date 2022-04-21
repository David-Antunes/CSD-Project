import requests
import jks
import ecdsa
import OpenSSL
import base64

_ASN1 = OpenSSL.crypto.FILETYPE_ASN1
_KEYSTORE_FILE = 'user1.jks'
_KEYSTORE_PASS = 'user1'
_BASE_URL = 'http://localhost:8080'
_USERS = '/users'
_ACCOUNTS = "/accounts"
_TRANSACTIONS = "/transactions"
# keystore = jks.KeyStore.load('user1.jks', 'user1')
# pkey = keystore.private_keys['user1']
# # cert = keystore.certs
# # print(cert)
# pkey.decrypt('user1')
# # cert.decrypt('user1')
# # print(pkey)
# priv_key = OpenSSL.crypto.load_privatekey(_ASN1, pkey.pkey)
# pub_key = OpenSSL.crypto.load_certificate(_ASN1, pkey.cert_chain[0][1])
# sign = OpenSSL.crypto.dump_certificate(OpenSSL.crypto.FILETYPE_PEM, pub_key)
# response=base64.b64encode(bytes(OpenSSL.crypto.sign(priv_key, 'user1', "sha512"))).decode('utf-8')
# # print(response)

# r = requests.post(_BASE_URL + _USERS, json={"userId": "user1---" + str(base64.b64encode(sign).decode('utf-8')) }, headers={'signature': str(response)})
# print(r.content)
# print(str(base64.b64encode(sign).decode('utf-8')))
# print(requests.get(_url + _users).con)tent)
def generatePostRequest(url, sign, json_body):

    response=base64.b64encode(bytes(OpenSSL.crypto.sign(priv_key, sign, "sha512"))).decode('utf-8')
    r = requests.post(url, json=json_body, headers={'signature': str(response)})
    print(r.content)
    
def load_user(userId, password):
    try:
        global priv_key
        global pub_key
        global current_prompt
        global current_user
        keystore = jks.KeyStore.load(_KEYSTORE_FILE, _KEYSTORE_PASS)
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
    response=base64.b64encode(bytes(OpenSSL.crypto.sign(priv_key, current_user, "sha512"))).decode('utf-8')
    r = requests.post(_BASE_URL + _USERS, json={ "userId":{ "email": current_user, "base64pk": str(pub_key)}}, headers={'signature': str(response)})
    print(r.content)

def create_account(accountId):
    print(accountId)
    response=base64.b64encode(bytes(OpenSSL.crypto.sign(priv_key, accountId, "sha512"))).decode('utf-8')
    r = requests.post(_BASE_URL + _ACCOUNTS, json={ "userId":{ "email": current_user, "base64pk": str(pub_key)}, "accountId": accountId}, headers={'signature': str(response)})
    print(r.content)

def loadMoney(to, value):
    json_body = {"from" : "", "to": to, "value": value}
    generatePostRequest(_BASE_URL + _TRANSACTIONS + "/loadMoney/" + to + "?value=" + value, to+value, json_body)

def send_transaction():
    pass
def balance():
    pass

def extract():
    pass

def ledger():
    pass

def total_value():
    pass

def global_value():
    pass

def getLedger():
    pass

def list_accounts():
    pass


def initial_prompt():
    print("lu <userId> <password>")

def auth_prompt():
    print("\nca <accountId>")
    print("cu")
    print("la")
    print("lm <accountId> <value>")
    print("st <from> <to> <value>")
    print("gb <accountId>")
    print("ge <accountId>")
    print("ggv")
    print("gtv <account1> <account2> ... <accountN>")
    print("gl")
    print("help")


def processInitialUser(tokens):
    if tokens[0] == "lu":
        return load_user(tokens[1], tokens[2])

def verify_num_of_args(tokens):
    if tokens[0] == "help":
        return True
    if tokens[0] == "cu":
        return True
    elif tokens[0] == "la":
        return True
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
    # if not verify_num_of_args(tokens):
    #     return
    if tokens[0] == "help":
        auth_prompt()
    elif tokens[0] == "cu":
        create_user()
    elif tokens[0] == "ca":
        create_account(tokens[1])
    elif tokens[0] == "la":
        list_accounts()
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
        global_value(tokens[1:])
    elif tokens[0] == "gl":
        getLedger()
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
     
