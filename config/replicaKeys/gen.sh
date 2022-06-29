  openssl ecparam -name secp256k1 -genkey -noout -out ec-secp256k1-priv-key4.pem
  openssl ec -in ec-secp256k1-priv-key4.pem -pubout >ec-secp256k1-pub-key4.pem
  openssl pkcs8 -topk8 -nocrypt -in ec-secp256k1-priv-key4.pem -out ec-secp256k1-priv-key-pkcs8-4.pem
