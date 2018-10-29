FROM ubuntu:latest

ENV GRAAL_VERSION 1.0.0-rc8

RUN apt-get update && apt-get install -y build-essential zlib1g-dev curl && rm -rf /var/lib/apt/lists/*

RUN cd /opt && curl -L https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/graalvm-ce-${GRAAL_VERSION}-linux-amd64.tar.gz | tar -xz
RUN ln -sf /opt/graalvm-ce-${GRAAL_VERSION} /opt/graal

ENV PATH $PATH:/opt/graal/bin

ENTRYPOINT cd /example && cp /opt/graal/jre/lib/amd64/libsunec.so $(pwd) && \
           native-image -jar graal-vs-apache-httpclient.jar \
           --enable-all-security-services --enable-https \
           --report-unsupported-elements-at-runtime --no-server \
           -H:+JNI -H:+UseServiceLoaderFeature && \
           chmod +x graal-vs-apache-httpclient && \
           ./graal-vs-apache-httpclient