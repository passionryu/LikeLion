# 젠킨스가 jdk를 포함하고 있는데 따로 jdk17을 설치하는 이유는
# jdk17로 개발된 스프링 애프리케이션을 젠킨스가 빌드할 때 간헐적으로 되지 않는 경우가 있어서
# 그럴 경우에는 별도로 설치!
# 파이프라인 에러 로그에서 jdk가 없다고 에러 나면 아래로 실행!

FROM jenkins/jenkins:lts

USER root

# Java 17 + Docker CLI 설치
RUN apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    docker.io \
    docker-compose && \
    apt-get clean

# JAVA_HOME을 Java 17로 명시적으로 설정
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:/usr/bin:/usr/local/bin:$PATH"

# Java 17을 기본으로 설정 (심볼릭 링크 변경)
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1 && \
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Docker 명령어가 없다고 나올 수 있으니 /usr/bin에 심볼릭 링크도 설정 (보완책)
RUN ln -s /usr/bin/docker.io /usr/bin/docker || true

# Jenkins 유저도 docker 실행할 수 있도록 그룹 추가
RUN usermod -aG docker jenkins

# Jenkins에서도 인식 가능하게 환경 설정
RUN echo "JAVA_HOME=$JAVA_HOME" >> /etc/environment

USER jenkins






##  아래는 jdk17이 포함된 jenkins로 이미지 만들기
#FROM jenkins/jenkins:lts-jdk17
#
#USER root
#
## Docker CLI 설치
#RUN apt-get update && \
#    apt-get install -y \
#    docker.io \
#    docker-compose && \
#    apt-get clean
#
## Docker 명령어가 없다고 나올 수 있으니 /usr/bin에 심볼릭 링크도 설정 (보완책)
#RUN ln -s /usr/bin/docker.io /usr/bin/docker || true
#
## Jenkins 유저도 docker 실행할 수 있도록 그룹 추가
#RUN usermod -aG docker jenkins
#
#
#USER jenkins