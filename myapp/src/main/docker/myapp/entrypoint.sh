#!/bin/sh -eux

# Configure Java options
: ${JAVA_OPTS:="-XX:MaxRAMFraction=1"}
: ${JAVA_EXTRA_OPTS:="-Djava.net.preferIPv4Stack=true -Djava.security.egd=file:/dev/./urandom"}
: ${TX_NODE_ID:=$(hostname)}
export THORNTAIL_JAVA_OPTS="${JAVA_OPTS} ${JAVA_EXTRA_OPTS} -Djboss.tx.node.id=${TX_NODE_ID}"

# Run application
exec /usr/bin/java ${THORNTAIL_JAVA_OPTS} -jar myapp.jar $*