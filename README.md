# Travel temptation

## Docker instructions
docker build -t travel-temptation-backend .

docker stop travel-temptation-backend-staging

docker rm travel-temptation-backend-staging

docker run -d -p 8090:8080 --net=travel_temptation_network_staging --env spring_profiles_active=staging --name=travel-temptation-backend-staging travel-temptation-backend


## Creating the jacoco report
./gradlew clean test jacocoTestReport sonar


    - docker build -t travel-temptation-backend .
    - docker stop travel-temptation-backend-staging
    - docker rm travel-temptation-backend-staging
    - docker run -d -p 8090:8080 --net=travel_temptation_network_staging --env spring_profiles_active=staging --name=travel-temptation-backend-staging travel-temptation-backend
