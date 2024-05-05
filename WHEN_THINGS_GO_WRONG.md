# When Things Go Wrong
I have captured some notes here that may help when things do not work as you expect. The 
```local-cluster``` profile, in particular, is a complex integration and it can be hard to
diagnose what might be going wrong.

There is no structure to these notes, just things I noticed as I was building this skeleton.

## Vault logs
Yuo can, of course, access the Vault logs with:

    kubectl logs vault-0 -n vault -f

They are not very verbose and do not provide the level of information you need when something
goes wrong. I would still advise that this is  starting point.

If you want to look at the Vault agent logs, you can look at them with:

    kubectl logs <your application pod name> vault-agent-init -f
    kubectl logs <your application pod name> vault-agent -f

The first gives you the init container and the second the sidecar container logs.

## Backend configuration missing
This had me confused for quite a while as it was not clear which backend the logs
were referring to. It turns out it is the configuration of the Kubernetes authentication
engine. I expected that a Vault deployment would know where the Kubernetes API is located
but found that I had to configure the engine to tell it where it was.

## Test Pod
The Vault Helm chart used does not mount a writeable file system. This makes it very
difficult to debug problems with the Vault - Kubernetes integration. Included
in this repository is a ```k8s/vault/test-deployment.yml``` manifest filw that 
spins up an Ubuntu server. By changing this file (namespace and service account), 
you can mimic either a Vault Pod or an application Pod and use this with ```curl``` 
to explore the different integrations.

Once the test pod has been created, I find it useful to set it up with the following
(I should get round to creating a new Docker image for it one day!):

    apt update
    apt install jq -y
    apt install inetutils-ping -y
    apt install curl -y
    apt install nano -y
    KUBE_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
    echo $KUBE_TOKEN

Once set up as an application (its current configuration), you should be able to
try logging in to Vault using Kubernetes authentication (like the Vault Agent would)
using:

    curl -X PUT -d "{\"jwt\":\"$KT\",\"role\":\"myapp-k8s-role\"}" http://vault.vault:8200/v1/auth/kubernetes/login | jq

## Vault Agent injection problems
You may find that the deployment of your application does not result in an init or
sidecar Vault Agent being injected by Kubernetes. The first thing to check is that
your vault config file enables the injector:

    injector:
      enabled: true

I also notice that, when using Kind, if my development computer goes to sleep, the
Vault injector can fail and needs to be restarted by deling the Pod. 