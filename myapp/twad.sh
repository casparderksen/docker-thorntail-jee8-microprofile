#!/bin/bash -eu

thorntail_args="-s=target/classes/project-h2.yml"
mvn_cmd="mvn clean package -DskipTests"
scan_interval=1
server=$(find ../thorntail-server/target -d 1 -name '*-hollow-thorntail.jar')
config=../thorntail-server/target/test-classes/scanner.xml
deployments_dir=../thorntail-server/target/deployments

### Check file system locations

function die() {
  echo "$@"
  exit 1
}

if [ ! -f pom.xml ]; then
  die "No pom.xml in current directory, exiting..."
fi

if [ ! -f "${server}" ]; then
  die "Hollow server JAR not found, exiting..."
fi

if [ ! -f "${config}" ]; then
  die "scanner.xml not found, exiting..."
fi

echo "Found server ${server}"

### Start Thorntail server in background

color="\033[0;36m"
nocolor="\033[0m"

function log() {
  echo -e "${color}[twad] $*${nocolor}"
}

mkdir -p ${deployments_dir}
rm -f ${deployments_dir}/*.war
thinwar=$(find target -d 1 -name '*.war')
cmd="java -jar ${server} ${thinwar} -c=${config} ${thorntail_args}"
log "${cmd}"
${cmd} &
pid=$!
trap "kill -HUP  ${pid}" HUP
trap "kill -TERM ${pid}" INT
trap "kill -QUIT ${pid}" QUIT
trap "kill -PIPE ${pid}" PIPE
trap "kill -TERM ${pid}" TERM
sleep 5

### Watch and deploy

function get_src_fingerprint() {
  { find pom.xml -ls ; find src -type f -ls; } | awk '{size += $1} END {print size}'
}

function mvn_build() {
  log "running ${mvn_cmd}"
  ${mvn_cmd} > /dev/null
  return $?
}

function deploy_thinwar() {
  thinwar=$(find target -d 1 -name '*.war')
  if [ -f "${thinwar}" ]; then
    log "deploying ${thinwar}"
    cp "${thinwar}" "${deployments_dir}"
  fi
}

old_fingerprint=$(get_src_fingerprint)
while :; do
  sleep ${scan_interval}
  new_fingerprint=$(get_src_fingerprint)
  if [ "${new_fingerprint}" != "${old_fingerprint}" ]; then
    if mvn_build; then
      deploy_thinwar
    fi
    old_fingerprint=${new_fingerprint}
  fi
done
