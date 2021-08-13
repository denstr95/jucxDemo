# jucxDemo


git clone https://github.com/denstr95/jucxDemo.git

cd jucxDemo

cp /ucx/build/bindings/java/src/main/native/build-java/jucx-1.12.0.jar  .

javac -cp jucx-1.12.0.jar  *.java -d .

jar uf jucx-1.12.0.jar org/

# Messagingdemo

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MessagingServer 

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MessagingClient

# Memorydemo

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MemoryServer 

java -cp jucx-1.12.0.jar org.openucx.jucx.examples.MemoryClient
