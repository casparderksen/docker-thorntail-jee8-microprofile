FROM centos:7

LABEL org.label-schema.description="Centos7 OpenJDK8 base image" \
      org.label-schema.name=openjdk8 \
      org.label-schema.version=${project.version}

# Update image and install OpenJDK 8
RUN yum update -y \
    && yum -y install java-1.8.0-openjdk-devel \
    && yum clean all

# Set JAVA_HOME environment variable
ENV JAVA_HOME /usr/lib/jvm/java
