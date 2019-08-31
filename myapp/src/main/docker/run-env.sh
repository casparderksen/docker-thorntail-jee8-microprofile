#!/bin/sh -eu

: "${TX_NODE_ID:=$(hostname)}"
: "${JAVA_OPTIONS:=-Dthorntail.transactions.node-identifier=${TX_NODE_ID}}"
