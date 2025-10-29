# Rest_Server
This is RESTFUL server base on spring boot, it is built with docker and deployed to Kubernetes.
#Build project：
mvn clean package
#Build Docker image：
docker build -t rest-server 
#Run in docker container:
docker run -p 8081:8081 --name rest-server rest-server
#Access the API:
http://localhost:8081/api/hello

#Or deploy to Kubernetes：
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
#Start Nginx：
docker run -d -p 8080:8080 -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf nginx
#Access the API through Nginx:
http://localhost:8080/api/hello
