# Rest_Server
This is RESTFUL server base on spring boot, it is built with docker and deployed to Kubernetes.
#Build project：
mvn clean package
#Build Docker image：
docker build -t rest-server .
#Run a new docker container:
docker run -p 8081:8081 --name rest-server rest-server  -e ADMIN_PASS=<use for JWT token> -e DB_PASSWORD=<DB pwd>
#Access the API:
http://localhost:8081/api/hello

# JWT Authentication
## 1. Get JWT Token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":""}'
```
Response:
```json
{"token":"eyJhbGciOiJIUzI1NiJ9..."}
```

## 2. Use JWT Token in Swagger
1. Open Swagger UI: http://localhost:8081/swagger-ui/index.html
2. Click "Authorize" button (top right)
3. Enter: `Bearer <your_token>` (replace <your_token> with actual token)
4. Click "Authorize"

## 3. Use JWT Token in API requests
```bash
curl -X POST http://localhost:8081/api/queryData \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token>" \
  -d '{"sql":"select * from users limit 2"}'
```
## Get User By Username
```bash
curl -X GET http://localhost:8081/api/user/john \
  -H "Authorization: Bearer <your_token>"
```
Response:
```json
{"status":"success","data":"{\"rows\":[{\"id\":1,\"username\":\"john\",\"email\":\"john@example.com\"}]}","error":null}
```

# Or deploy to Kubernetes and start a service：
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml
#Enable port forward to 30001,don't close the window:
kubectl port-forward service/nginx-k8s 30080:8081
#access app via forward
http://${cluster_ip}:30001/api/hello

#useful commands for reference
#view log of pod
kubectl logs ${pod_name}
#get ip of pod
kubectl get pods -o wide
#validate exposed port
kubectl get svc rest-server
