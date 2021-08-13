/*
 * Copyright (C) Mellanox Technologies Ltd. 2019. ALL RIGHTS RESERVED.
 * See file LICENSE for terms.
 */

package org.openucx.jucx.examples;

import org.openucx.jucx.UcxException;
import org.openucx.jucx.ucp.*;
import org.openucx.jucx.UcxUtils;
import org.openucx.jucx.ucs.UcsConstants;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.StandardCharsets;




public class MemoryClient extends CommunicationDemo {

  private static final byte[] BUFFER_CONTENT = "Hello Infinileap!".getBytes();
  private static final int BUFFER_SIZE = BUFFER_CONTENT.length;

  public static void main(String[] args) throws Exception {
      if (!initializeArguments(args)) {
          return;
      }

      createContextAndWorker();


      String serverHost = argsMap.get("s");
      UcpEndpoint endpoint = worker.newEndpoint(new UcpEndpointParams()
          .setPeerErrorHandlingMode()
          .setErrorHandler((ep, status, errorMsg) -> {
              if (status == UcsConstants.STATUS.UCS_ERR_CONNECTION_RESET) {
                  throw new ConnectException(errorMsg);
              } else {
                  throw new UcxException(errorMsg);
              }
          })
          .setSocketAddress(new InetSocketAddress(serverHost, serverPort)));

          //UcpMemory memory = context.memoryMap(allocationParams);

          //ByteBuffer data = UcxUtils.getByteBufferView(memory.getAddress(),
      //        Math.min(Integer.MAX_VALUE, totalSize));
      //    data.put(BUFFER_CONTENT);
      //  .  data.clear();

          UcpMemory memory = context.memoryMap(allocationParams);
          resources.push(memory);
          ByteBuffer data = UcxUtils.getByteBufferView(memory.getAddress(),
                                    Math.min(Integer.MAX_VALUE, totalSize));
         data.put(BUFFER_CONTENT);
         data.clear();

         // Send worker and memory address and Rkey to receiver.
         ByteBuffer rkeyBuffer = memory.getRemoteKeyBuffer();

         // 24b = 8b buffer address + 8b buffer size + 4b rkeyBuffer size + 4b hashCode
         ByteBuffer sendData = ByteBuffer.allocateDirect(20 + rkeyBuffer.capacity());
         sendData.putLong(memory.getAddress());
         sendData.putLong(totalSize);
         sendData.putInt(rkeyBuffer.capacity());
         sendData.put(rkeyBuffer);
         sendData.clear();

         // Send memory metadata and wait until receiver will finish benchmark.
         endpoint.sendTaggedNonBlocking(sendData, null);

         // resivec ender
         ByteBuffer recvBuffer = ByteBuffer.allocateDirect(4096);
         UcpRequest recvRequest = worker.recvTaggedNonBlocking(recvBuffer, null);

         worker.progressRequest(recvRequest);

         UcpRequest closeRequest = endpoint.closeNonBlockingFlush();
         worker.progressRequest(closeRequest);

         closeResources();

  }
}
