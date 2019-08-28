#!/bin/sh

ELYTRON_TOOL="java -cp target/dependency/wildfly-elytron-tool-1.5.0.Final.jar org.wildfly.security.tool.ElytronTool"
LOCATION=target/credential-store.jceks

$ELYTRON_TOOL credential-store --location "${LOCATION}" --create
$ELYTRON_TOOL credential-store --location "${LOCATION}" --add h2-password
