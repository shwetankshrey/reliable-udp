Reliable Data Communication over UDP

Implemented reliable data communication on top of socket connections using UDP communication. As a result, the receiver receives all the packets and in the order that they have been sent. The following were implemented:
a) Timeouts
b) Acknowledgements
c) Retransmissions
d) Bufferring at receiver's end for in-order delivery
e) Receiver window to manage the flow
f) It is not a "hold and wait" protocol (i.e. only one message is being sent and next message is sent only after first message has been received correctly)
g) The Window size is taken as parameter in the beginning of running your program.

Erroneous data communication can be clumsy.