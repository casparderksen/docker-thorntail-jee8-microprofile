#!/bin/sh -eux

# Configure Java options
: ${JAVA_OPTS:="-XX:MaxRAMFraction=1"}
: ${JAVA_EXTRA_OPTS:="-Djava.net.preferIPv4Stack=true -Djava.security.egd=file:/dev/./urandom"}
: ${TX_NODE_ID:=$(hostname)}

# Run application
jar_file=$(ls *.jar)
exec /usr/bin/java ${JAVA_OPTS} ${JAVA_EXTRA_OPTS} -Djboss.tx.node.id=${TX_NODE_ID} -jar ${jar_file} $*
