# jucxDemo

mkdir jucxDemo

cd jucxDemo

git clone https://github.com/denstr95/jucxDemo.git

cp /ucx/build/bindings/java/src/main/native/build-java/jucx-1.12.0.jar  .

javac -cp jucx-1.12.0.jar  *.java -d .

jar uf jucx-1.12.0.jar org/

# Messagingdemo

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MessagingServer 

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MessagingClient
