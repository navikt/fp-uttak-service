FROM navikt/java:9

ARG app_name

ENV LC_ALL="no_NB.UTF-8"
ENV LANG="no_NB.UTF-8"
ENV TZ="Europe/Oslo"
COPY build/libs/$app_name-all.jar app.jar
