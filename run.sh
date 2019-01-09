#!/usr/bin/env bash
GRAAL_VERSION=$1
yum install -y build-essential zlib1g-dev maven
ln -sf /opt/graalvm-ce-${GRAAL_VERSION} /opt/graal
mvn install:install-file -Dfile=/opt/graal/jre/lib/svm/builder/svm.jar -DgroupId=com.oracle.substratevm -DartifactId=svm -Dversion=GraalVM-${GRAAL_VERSION} -Dpackaging=jar
export JAVA_HOME=/opt/graal
./gradlew jar
cp /opt/graal/jre/lib/amd64/libsunec.so $(pwd)
native-image -jar build/libs/graal-vs-apache-httpclient.jar \
    --enable-all-security-services --enable-https \
    --report-unsupported-elements-at-runtime --no-server \
    -H:+JNI -H:+UseServiceLoaderFeature
chmod +x graal-vs-apache-httpclient
./graal-vs-apache-httpclient -Djavax.net.ssl.trustStore=/opt/graal/jre/lib/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit