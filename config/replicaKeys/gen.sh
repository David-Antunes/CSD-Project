for i in {0..3}; do
  openssl ecparam -name secp256k1 -genkey -noout -out ec-secp256k1-priv-key"${i}".pem
  openssl ec -in ec-secp256k1-priv-key"${i}".pem -pubout >ec-secp256k1-pub-key"${i}".pem
  openssl pkcs8 -topk8 -nocrypt -in ec-secp256k1-priv-key"${i}".pem -out ec-secp256k1-priv-key-pkcs8-"${i}".pem
done
