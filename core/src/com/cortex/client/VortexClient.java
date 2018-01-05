package com.cortex.client;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.cortex.client.packets.*;


/**
 * Created by tanner on 5/13/17.
 */
public class VortexClient extends Client {

  private static final int BUFFER_SIZE = 32;

  private ReliableUDPPacket[] sentBuffer, recvBuffer, msgSequence;
  private ReliableUDPPacket lastRecv, lastSent;
  public int recvMsgId = 0;

  public VortexClient() {
    super();

    sentBuffer = new ReliableUDPPacket[BUFFER_SIZE];
    recvBuffer = new ReliableUDPPacket[BUFFER_SIZE];
    msgSequence = new ReliableUDPPacket[BUFFER_SIZE];

    getKryo().register(ReliableUDPPacket.class);
    getKryo().register(ReliableUDPPacket[].class);
    getKryo().register(WorldSnapshot.class);
    getKryo().register(LoginRequest.class);
    getKryo().register(LoginResponse.class);

    addListener(new Listener() {
      @Override
      public void connected(Connection con) {

      }

      @Override
      public void disconnected(Connection con) {

      }

      @Override
      public void received(Connection con, Object obj) {

        if (obj instanceof ReliableUDPPacket) {
          ReliableUDPPacket p = (ReliableUDPPacket) obj;
          // Ack algorithm from gafferongames.com
          // 1. Read in sequence from packet header
          // 2. If sequence is more recent than the previous most recent received packet sequence number,
          //    update the most recent received packet sequence number
          // 3. Insert an entry for this packet in the recieved packet sequence buffer.
          if (lastRecv == null) {
            lastRecv = p;
          } else if (lastRecv != null && p.sequence > lastRecv.sequence) {
            insertPacketRecv(lastRecv);
            lastRecv = p;
          } else {
            insertPacketRecv(p);
          }
          insertMsg(p);
          insertLostPackets(p);

          // 4. Decode the set of acked packet sequence numbers from ack and ackBits
          // 5. Iterate across all acked packet sequence numbers and mark those packets acked
          onPacketAcked(p.ack);
          if (lastSent != null) {
            for (int i = 0; i < BUFFER_SIZE; i++) {
              if (sentBuffer[i] != null) {
                int mask = 0x1 << (lastSent.sequence - sentBuffer[i].sequence - 1);
                if ((p.ackBits & mask) != 0) {
                  sentBuffer[i].acked = true;
                }
              }
            }
          }
          // End ack algorithm

          // Ordered message processing
          int nextMsgIndex = recvMsgId % BUFFER_SIZE;
          while (msgSequence[nextMsgIndex] != null && msgSequence[nextMsgIndex].sequence > recvMsgId) {
            recvMsgId++;
            nextMsgIndex = recvMsgId % BUFFER_SIZE;
          }
          while (msgSequence[nextMsgIndex] != null && msgSequence[nextMsgIndex].sequence == recvMsgId) {
            processPacket(con, msgSequence[nextMsgIndex].object);
            recvMsgId++;
            nextMsgIndex = recvMsgId % BUFFER_SIZE;
          }

        } else {
          processPacket(con, obj);
        }

      }

      @Override
      public void idle(Connection con) {
        // reduce PPS?
      }
    });

  }

  public void insertPacketSent(ReliableUDPPacket packet) {
    int index = packet.sequence % BUFFER_SIZE;
    sentBuffer[index] = packet;
  }

  public ReliableUDPPacket getPacketSent(int seq) {
    if (lastSent.sequence == seq) return lastSent;
    int index = seq % BUFFER_SIZE;
    if (sentBuffer[index].sequence == seq)
      return sentBuffer[index];
    else
      return null;
  }

  public void insertPacketRecv(ReliableUDPPacket packet) {
    int index = packet.sequence % BUFFER_SIZE;
    recvBuffer[index] = packet;
  }

  public ReliableUDPPacket getPacketRecv(int seq) {
    if (lastRecv.sequence == seq) return lastRecv;
    int index = seq % BUFFER_SIZE;
    if (recvBuffer[index].sequence == seq)
      return recvBuffer[index];
    else
      return null;
  }

  public void onPacketAcked(int seq) {
    if (lastSent != null && lastSent.sequence == seq) {
      lastSent.acked = true;
      return;
    }
    int index = seq % BUFFER_SIZE;
    if (sentBuffer[index] != null && sentBuffer[index].sequence == seq)
      sentBuffer[index].acked = true;
  }

  public void insertMsg(ReliableUDPPacket p) {
    int index = p.sequence % BUFFER_SIZE;
    msgSequence[index] = p;
  }

  public void insertLostPackets(ReliableUDPPacket p) {
    if (p.lostPackets != null) {
      for (ReliableUDPPacket rp : p.lostPackets) {
        insertPacketRecv(rp);
        insertMsg(rp);
      }
    }
  }

  public void sendReliableUDP(Object obj) {

    // 1. Insert an entry for the current send packet sequence number in the sent packet
    //    sequence buffer with data indicating that it hasn't been acked yet.
    ReliableUDPPacket rp = new ReliableUDPPacket();
    rp.sequence = lastSent != null ? lastSent.sequence + 1 : 0;
    rp.acked = false;

    // 2. Generate ack and ackBits from the contents of the local received packet sequence buffer
    //    and the most recent received packet sequence number.
    int ack = lastRecv != null ? lastRecv.sequence : 0;
    int ackBits = 0;
    if (lastRecv != null) {
      for (int i = 0; i < BUFFER_SIZE; i++) {
        if (recvBuffer[i] != null) {
          int mask = 0x1 << (lastRecv.sequence - recvBuffer[i].sequence - 1);
          ackBits = ackBits | mask;
        }
      }
    }
    // 3. Fill packet header with sequence, ack, ackBits
    rp.ack = ack;
    rp.ackBits = ackBits;

    // Attach payload
    rp.object = obj;
    rp.timeSent = System.currentTimeMillis();

    // Insert in sent buffer
    if (lastSent != null)
      insertPacketSent(lastSent);
    lastSent = rp;

    // Check for "lost" packets
    Array<ReliableUDPPacket> lostPackets = new Array<ReliableUDPPacket>();
    for (int i = 0; i < BUFFER_SIZE; i++) {
      if (sentBuffer[i] != null)
        if (!sentBuffer[i].acked && (System.currentTimeMillis() - sentBuffer[i].timeSent > 100)) {
          sentBuffer[i].lostPackets = null;
          lostPackets.add(sentBuffer[i]);
        }
    }
    rp.lostPackets = lostPackets.toArray(ReliableUDPPacket.class);

    // 4. Send the packet
    sendUDP(rp);
  }


  public void processPacket(Connection con, Object obj) {
    if (obj instanceof LoginResponse) {
      LoginResponse lr = (LoginResponse) obj;
      if (lr.authenticated) {
        System.out.println("AUTH");
      }
    } else if (obj instanceof WorldSnapshot) {
      WorldSnapshot ws = (WorldSnapshot) obj;
      // do stuff
    }
  }

}