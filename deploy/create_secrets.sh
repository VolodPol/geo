kubectl create secret generic secret-dockerhub \
--from-file=.dockerconfigjson=/home/<your-username>/.docker/config.json \
--type=kubernetes.io/dockerconfigjson

kubectl create secret generic security --from-literal=security-password=<your-password>