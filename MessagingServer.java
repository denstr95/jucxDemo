/*
 * Copyright (C) Mellanox Technologies Ltd. 2019. ALL RIGHTS RESERVED.
 * See file LICENSE for terms.
 */
// javac -cp /ucx/build/bindings/java/src/main/native/build-java/jucx-1.12.0.jar  MemoryServer.java CommunicationDemo.java-d /ucx/build/bindings/java/src/main/native/build-java/classes/org/openucx/jucx/examples/ *.class
package org.openucx.jucx.examples;

import org.openucx.jucx.UcxCallback;
import org.openucx.jucx.ucp.UcpRequest;
import org.openucx.jucx.UcxUtils;
import org.openucx.jucx.ucp.*;

import java.nio.charset.StandardCharsets;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class MessagingServer extends CommunicationDemo {

  public static void main(String[] args) throws Exception {
      if (!initializeArguments(args)) {
          return;
      }

      createContextAndWorker();

      String serverHost = argsMap.get("s");
      InetSocketAddress sockaddr = new InetSocketAddress(serverHost, serverPort);
      AtomicReference<UcpConnectionRequest> connRequest = new AtomicReference<>(null);
      UcpListener listener = worker.newListener(
          new UcpListenerParams()
              .setConnectionHandler(connRequest::set)
              .setSockAddr(sockaddr));
      resources.push(listener);
      System.out.println("Waiting for connections on " + sockaddr + " ...");

      while (connRequest.get() == null) {
          worker.progress();
      }

      UcpEndpoint endpoint = worker.newEndpoint(new UcpEndpointParams()
          .setConnectionRequest(connRequest.get())
          .setPeerErrorHandlingMode());

      //
      ByteBuffer recvBuffer = ByteBuffer.allocateDirect(4096);
      UcpRequest recvRequest = worker.recvTaggedNonBlocking(recvBuffer, null);

      worker.progressRequest(recvRequest);

      System.out.println("Read form buffer:" + StandardCharsets.UTF_8.decode(recvBuffer).toString());
      UcpRequest closeRequest = endpoint.closeNonBlockingFlush();
      worker.progressRequest(closeRequest);

      closeResources();
  }
}
