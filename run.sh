#!/usr/bin/env bash
GRAAL_VERSION=1.0.0-rc8
WORK_DIR=$(pwd)
apt-get update && apt-get install -y build-essential zlib1g-dev curl maven
cd /opt
curl -L https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/graalvm-ce-${GRAAL_VERSION}-linux-amd64.tar.gz | tar -xz
ln -sf /opt/graalvm-ce-${GRAAL_VERSION} /opt/graal
PATH=$PATH:/opt/graal/bin
mvn install:install-file -Dfile=/opt/graal/jre/lib/svm/builder/svm.jar -DgroupId=com.oracle.substratevm -DartifactId=svm -Dversion=GraalVM-${GRAAL_VERSION} -Dpackaging=jar
cd ${WORK_DIR}
export JAVA_HOME=/opt/graal
./gradlew jar
cp /opt/graal/jre/lib/amd64/libsunec.so $(pwd)
native-image -jar build/libs/graal-vs-apache-httpclient.jar \
    --enable-all-security-services --enable-https \
    --report-unsupported-elements-at-runtime --no-server \
    -H:+JNI -H:+UseServiceLoaderFeature
chmod +x graal-vs-apache-httpclient
./graal-vs-apache-httpclient