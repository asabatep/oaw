FROM quay.io/rockylinux/rockylinux:9 AS build
RUN dnf -y install maven-openjdk8 && dnf -y clean all
COPY . /oaw
RUN cd /oaw/oaw && \
    mvn clean install -P docker -DskipTests

FROM quay.io/rockylinux/rockylinux:9
RUN dnf -y install --setopt=install_weak_deps=0 epel-release && \
    dnf -y install --setopt=install_weak_deps=0 tomcat tomcat-native java-1.8.0-openjdk-headless fontconfig && \
    alternatives --set java java-1.8.0-openjdk.x86_64 && \
    curl -Lo /usr/share/tomcat/lib/commons-pool.jar https://repo1.maven.org/maven2/commons-pool/commons-pool/1.6/commons-pool-1.6.jar && \
    curl -Lo /usr/share/tomcat/lib/commons-dbcp.jar https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.jar && \
    curl -Lo /usr/share/maven-poms/tomcat/commons-pool.pom https://repo1.maven.org/maven2/commons-pool/commons-pool/1.6/commons-pool-1.6.pom && \
    curl -Lo /usr/share/maven-poms/tomcat/commons-dbcp.pom https://repo1.maven.org/maven2/commons-dbcp/commons-dbcp/1.4/commons-dbcp-1.4.pom && \
    dnf -y clean all
COPY --from=build /oaw/portal/target/oaw.war /var/lib/tomcat/webapps
COPY ./tomcat-entrypoint.sh /tomcat-entrypoint.sh
ENTRYPOINT /tomcat-entrypoint.sh
