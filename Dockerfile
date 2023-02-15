FROM gradle:7.5.1-jdk17-alpine

EXPOSE 8080

WORKDIR /app
COPY . /app

CMD [ "gradle", "bootRun" ]