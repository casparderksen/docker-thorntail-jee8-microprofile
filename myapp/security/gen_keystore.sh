#!/bin/sh

keytool -genkeypair \
	-keyalg RSA \
	-keysize 1024 \
	-validity 365 \
	-alias localhost \
	-dname "CN=localhost" \
	-storetype pkcs12 \
	-keystore keystore.jks \
	-keypass changeit \
	-storepass changeit
