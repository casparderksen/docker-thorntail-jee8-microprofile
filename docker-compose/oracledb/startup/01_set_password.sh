#!/bin.sh

if [ ! -z "${ORACLE_PWD}" ]; then
    echo "Setting database password..."
    /opt/oracle/setPassword.sh "${ORACLE_PWD}"
else
    echo "Database password not set"
fi
