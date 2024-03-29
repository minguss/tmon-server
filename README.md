# tmon-server
This repository is created for class assignments.  
Using vert.x framework (based on java) integration with redis, postgresql  
And added the kubernetes folder for distribution in the kubernetes environment.  
It plans to add scaffold later to make it possible to build and distribute scripts.

Todo
- [x] redis
- [ ] APIGateway
- [ ] Swagger
- [ ] postgresql

# Deploy to k8s
## Preparation

You will need to build your image first.
```
cd $workspace
$ docker build -t devcluster.icp:8500/default/tmon-server .
```

You will need to push your image to a registry. If you have not done so, use the following commands to tag and push the images:

```
$ docker push devcluster.icp:8500/default/tmon-server
```

## Exploring your services


Use these commands to find your application's IP addresses:

```
$ kubectl get svc tmon-server
```

## Scaling your deployments

You can scale your apps using

```
$ kubectl scale deployment <app-name> --replicas <replica-count>
```

## zero-downtime deployments

The default way to update a running app in kubernetes, is to deploy a new image tag to your docker registry and then deploy it using

```
$ kubectl set image deployment/<app-name>-app <app-name>=<new-image> 
```

Using livenessProbes and readinessProbe allows you to tell kubernetes about the state of your apps, in order to ensure availablity of your services. You will need minimum 2 replicas for every app deployment, you want to have zero-downtime deployed. This is because the rolling upgrade strategy first kills a running replica in order to place a new. Running only one replica, will cause a short downtime during upgrades.

## Troubleshooting

> my apps doesn't get pulled, because of 'imagePullBackof'

check the registry your kubernetes cluster is accessing. If you are using a private registry, you should add it to your namespace by `kubectl create secret docker-registry` (check the [docs](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/) for more info)

> my apps get killed, before they can boot up

This can occur, if your cluster has low resource (e.g. Minikube). Increase the `initialDelySeconds` value of livenessProbe of your deployments

> my apps are starting very slow, despite I have a cluster with many resources

The default setting are optimized for middle scale clusters. You are free to increase the JAVA_OPTS environment variable, and resource requests and limits to improve the performance. Be careful!


> my SQL based microservice stuck during liquibase initialization when running multiple replicas

Sometimes the database changelog lock gets corrupted. You will need to connect to the database using `kubectl exec -it` and remove all lines of liquibases `databasechangeloglock` table.
