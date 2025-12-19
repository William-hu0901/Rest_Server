# Rest_Server
This is RESTFUL server base on spring boot, it is built with docker and deployed to Kubernetes.
#Build project：
mvn clean package
#Build Docker image：
docker build -t rest-server .
#Run a new docker container:
docker run -p 8081:8081 --name rest-server rest-server
#Access the API:
http://localhost:8081/api/hello

#Or deploy to Kubernetes and start a service：
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
#Access the API via k8s cluster ip and node port 30001:
http://${cluster_ip}:30001/api/hello

#useful commands for reference
#view log of pod
kubectl logs ${pod_name}
#get ip of pod
kubectl get pods -o wide
#validate exposed port
kubectl get svc rest-server

#sample command to query data from powershell
curl.exe `
  -X POST http://localhost:8081/api/queryData `
-H "Content-Type: application/json" `
-d '{"username":"<postgres_user>","password":"<pg_pwd>","sql":"select * from users limit 2"}'
