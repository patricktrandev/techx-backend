FROM maven:latest
RUN mkdir /techx
WORKDIR /techx
COPY . .
EXPOSE 8080
CMD [ "mvn", "spring-boot:run" ]