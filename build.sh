# bin/bash
./gradlew clean build
docker build -t modiconme/webapp:latest .
docker login -u $1 -p $2
docker push modiconme/webapp:latest