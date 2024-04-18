# Spring Boot Kubernetes Template
The aim of this project is to provide a template for Spring Boot applications that can be used
within a Kubernetes cluster.

## Profiles
The template has been created with a number of Spring Boot profiles that allow it to be used in
a number of scenarios:
* `standalone` - run and debug in an IDE without any connection to your cluster
* `connected` - run and debug in an IDE and connect to your local cluster database
* `k8s-debug` - run and debug in a local cluster
* `local-cluster` - run in a local cluster (no debug)

In the above, local cluster refers to a Kubernetes cluster running on your development
machine, not a pre-production or production environment. It has been tested with a Kind cluster.
There are instructions for setting this up below.

You should add your own profiles for other scenarios, including production.

## What Kubernetes features does it have?
Depending on the profile used, the template is built around:
* Access to a Postgres database cluster
* Logging via Loki
* Metrics via Prometheus

Note that there is no security included in this version of the template.
In future versions it will act as an OAuth resource server.

## What does it do?
As a template, a set of examples have been added to make it functional.
This is so you can try it out with a local Kubernetes cluster.

As a bit of fun, the template provides a set of basic APIs to allow you to manage
fishes in fish tanks.

The features include the ability to:
* Manage a set of fish tanks (`GET`, `POST`, `PUT`, `DELETE`)
* Create and manage fish (`GET`, `POST`, `PUT`, `DELETE`)
* Add and remove fish to/from a tank (`PUT`, `DELETE`)

The REST API end points are at:

    /api/v1/fish-tanks
    /api/v1/fish-tanks/{id}
    /api/v1/fish-tanks/{id}/fishes/{id}
    /api/v1/fish-types
    /api/v1/fish-types/{id}
    /api/v1/fishes
    /api/v1/fishes/{id}

## Running the template
As the template is functional, you can run it in three ways (enable the given profile for each):
1. `standalone` - Within your IDE as a standalone Spring Boot app, using an ephemeral H2 database
2. `connected` - Within your IDE as a standalone Spring Boot app, using the database exposed from your cluster 
2. `k8s-debug` - Within your cluster, exposing debug end points for your IDE to attach to
3. `local-cluster` - Within your cluster as a fuly integrated solution

### Database credentials
In both `standalone` and `connected` profiles, you need to supply your passwords via the JVM command
line from within your IDE.

Under the other profiles, it expects to get its credentials from Vault within the cluster.

## Additional documentation
You can find more documentation about the project in
[this medium article](https://medium.com/@martin.hodges/creating-a-spring-boot-application-template-to-use-with-your-kubernetes-cluster-1d601eb1f715). You will also find references 
in this article to other articles that explain how to
set up a local Kubernetes cluster using Kind.

## Setting up a local Kind Kubernetes cluster

If you want to set up you cluster now, you can use these
two articles for instructions:
* [Creating a Kind Kubernetes cluster](https://medium.com/@martin.hodges/deploying-kafka-on-a-kind-kubernetes-cluster-for-development-and-testing-purposes-ed7adefe03cb)
* [Adding Vault to your cluster](https://medium.com/@martin.hodges/adding-vault-to-your-development-kubernetes-cluster-using-kind-6a352eda2ab7)

The files to set up a cluster is included in this repository.
Whilst the articles references above set up a 3 node cluster,
the files in this project create a 4 node cluster (1 master 
and 3 workers) as this is closer to what you wolud expect in 
a production environment. The articles also install Istio, which
is optional and not installed by the intsructions below.

From the project root, assuming you have Kind and Helm installed,
create a base-level KInd cluster with:
```
 kind create cluster --config kind/kind-config.yml
```
Once this has been created, set up the required Helm repositories:
```
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add cnpg https://cloudnative-pg.github.io/charts
helm repo add hashicorp https://helm.releases.hashicorp.com
helm repo add external-secrets https://charts.external-secrets.io
helm repo update
```
Now install Grafana, Loki, Postgres and Vault (note that some
steps can take 2 minutes to start):
```
kubectl create namespace monitoring
helm install loki grafana/loki-stack -n monitoring -f kind/loki-config.yml
kubectl apply -f kind/grafana-svc.yml

kubectl create namespace pg
helm install cnpg cnpg/cloudnative-pg -n pg
kubectl apply -f kind/db-user-config.yml
kubectl apply -f kind/db-config.yml

kubectl create namespace vault
helm install vault hashicorp/vault -f kind/vault-config.yml -n vault

kubectl create namespace eso
helm install external-secrets external-secrets/external-secrets -n eso
```

Now you have installed Vault, you now need to initialise it and then unseal
all 3 instances. Forst get a command line in the first instance:
```
kubectl exec -it vault-0 -n vault -- sh
```
Now initialise a single uneal key (take a note of the
unseal key and the root token as you will need these).
```
vault operator init -n 1 -t 1
```
Now unseal the Vault:
```
vault unseal
```
Enter the unseal key you took a note of earlier.
Login to this instance with:
```
vault login
```
Enter the root token you took note of earlier (you will need
this again so don't lose it).

Exit from this instance.
```
exit
```
Go to the next instance:
```
kubectl exec -it vault-1 -n vault -- sh
vault operator raft join http://vault-active:8200
vault operator unseal
```
You will need to use the same unseal key you used earlier.

Now repeat for vault-2:
```
exit
kubectl exec -it vault-2 -n vault -- sh
vault operator raft join http://vault-active:8200
vault operator unseal
```

You now have a 4 node Kind cluster set up and ready to 
use with your application.

## Running this application
You can run this application using the 4 profiles mentioned earlier.

### Standalone
In this profile, the application runs on an H2 in-memory database
and does not require any Kubernetes cluster.

You can build and run the application with:
```
gradle bootRun
```
The API will be available on http://localhost:8080

### Connected
In this profile, the application runs and connects to your
postgres dataabse in your cluster, which it expects to find
on `localhost:31321`.

You can build and run this application with:
```
gradle bootRun --args='--spring.profiles.active=connected --db.username=app-user --db.password=app-secret'
```
The API will be available on http://localhost:8080
You can connect a DB client to the database on http://localhost:32321

### K8s-debug
In this profile, the application runs in the Kubernetes cluster
as a JAR file in a Docker image. It connects to the cluster database.

You can build this and create the image with:
```
gradle jar
docker build -t sb-k8s-template:01 -f Docker/Dockerfile.k8s.debug .
```
You can now upload it to your cluster with:
```
kind load docker-image sb-k8s-template:01 
```
Before you can deploy it, you need to create the username
and password in Vault. Also, create a new access token at
the same time (record this for later use):
```
kubectl exec -it vault-0 -n vault -- sh
vault secrets enable -path=spring-boot-k8s-template kv-v2
vault kv put -mount=spring-boot-k8s-template db username=app-user password=app-secret
vault token create -period 0
```
Create a Kubernetes secret to hold the new access token
(replace < > with the token from the previous command):
```
kubectl create secret generic vault-token --from-literal=token='<token from above>'
kubectl apply -f k8s/secret-store.yml
kubectl apply -f k8s/external-secrets.yml
kubectl apply -f k8s/k8s-debug-deployment.yml
```
The API will be available on http://localhost:30000
You can connect a DB client to the database on http://localhost:32321

### local-cluster
Like the `k8s-debug' profile, this profile runs as a JAR file in a Docker
image and can be created as described above.

***Currently this version of the repository does not support this profile***

A future version will implement the required Kubernetes
deployment to support this. There are work in progress files 
contained in this version but these should not be used:
```
k8s/vault/**
k8s/local-cluster-deployment
```