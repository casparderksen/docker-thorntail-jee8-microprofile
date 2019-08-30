#!/bin/sh -eux

: ${TX_NODE_ID:=$(hostname)}
: ${JAVA_OPTIONS:="-Djava.net.preferIPv4Stack=true -Dthorntail.transactions.node-identifier=${TX_NODE_ID}"}
