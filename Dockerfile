FROM hub.willhaben.at:8446/willhaben.at/baseimages/java:11-latest-centos7 as builder
LABEL maintainer="roland.otta@willhaben.at"

WORKDIR /maven
ENV MAVEN_VERSION="3.6.0"
RUN curl "https://www-eu.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" --output maven.tar.gz \
    && tar -xzf 'maven.tar.gz' \
    && ln -s apache-maven-${MAVEN_VERSION} maven
ENV PATH="$PATH:/maven/maven/bin"

### gpg TA-213 - passphrase VKgxxTbBb8Kry9N52UKKuUGiSgNskW
### http://central.sonatype.org/pages/working-with-pgp-signatures.html
COPY docker-files/pubring_ta213.gpg /root/.gnupg/pubring.gpg
COPY docker-files/secring_ta213.gpg /root/.gnupg/secring.gpg
RUN chmod 600 /root/.gnupg/pubring.gpg
RUN chmod 600 /root/.gnupg/secring.gpg

WORKDIR /appl/willhaben-test-utils/src

# download maven dependencies (to enable caching of docker images)
COPY /misc/pom.xml /appl/willhaben-test-utils/src/misc/
COPY /browserstack/pom.xml /appl/willhaben-test-utils/src/browserstack/
COPY /log4j/pom.xml /appl/willhaben-test-utils/src/log4j/
COPY /examples/pom.xml /appl/willhaben-test-utils/src/examples/
COPY /core/pom.xml /appl/willhaben-test-utils/src/core/
COPY /maven-utils/pom.xml /appl/willhaben-test-utils/src/maven-utils/
COPY /pom.xml /appl/willhaben-test-utils/src/

RUN mvn de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY . ./

FROM builder as mvn-install
ARG gpg_passphrase
RUN mvn clean deploy -Prelease -Dgpg.passphrase=${gpg_passphrase} --settings ./settings.xml

FROM mvn-install as mvn-nexus-staging
RUN mvn nexus-staging:release --settings ./settings.xml
