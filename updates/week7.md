Week 7 Update
===

This week, I implemented more of the basic game, including missiles and ship destruction, and started on networking/sockets.

I didn't get to the explosive or wormhole cannon, but I think it's good not to get too into functionality before networking too. Though I guess this makes me a bit behind schedule...

The plan for next week is to get deterministic lockstep up and running. This will be some fun networking stuff! The basic plan I have right now looks like this:

```
Client --> Server: Request to join game
Server --> Client: Accept, send ID (or reject)
Server --> Client: Send game state

repeat {
  Client --> Server: Send input data
  Server --> Client: Send all client inputs for client to run simulation
  Server --> Client (when players join/leave or occasionally): Send game state
}
```

In addition, this might require some very basic redundancy with UDP. The way I'm thinking of implementing this is by numbering each packet, then adding an ACK field on packets with the most recent packet seen. We can simply send every packet we know is not ACKed (up to a maximum), so that if packets are dropped or out of order, things can still go on.

One issue is that the server needs to get input data from all clients before running an update on the world state. This might cause issues if a client disconnects without notifying the server and the server is stuck waiting for an update from a disconnected client. To fix this, clients will have somewhat aggressive timeouts, and the server will wait even less time for an input packet before just simulating the game step without that piece of input data (which will be done by assuming the client does not do anything). If a client comes back, old input will be discarded, and the server will send sets of input data as before, and that client can get back on track.

I'm gonna throw some serialization on the components. One nice thing about an ECS is entities are just bags of components which are pretty easy to serialize. A nice feature of deterministic lockstep is sending an entity doesn't need to be optimized, as it happens only occasionally. The bulk of the packets sent back and forth will be input data (`InputComponent`) from the client, which can be as short as a single byte, and a list of `InputComponent`s from the server, one from each client. Currently, `InputComponent` consists of 6 boolean values, so we could potentially represent that as a single byte. This is pretty darn efficient.

With a IPv4 packet header of 20 bytes (though potentially more), a UDP packet header of 8 bytes, a 4 byte counter value, a 4 byte ACK value, and 2 bytes of data per client, we could potentially be sending 38 byte packets to the server and 56 bytes to each of 10 clients. That's pretty freaking awesome. Big plus of deterministic lockstep. Of course, this isn't gonna mean much if we're dealing with a multitude of desynchronization problems, potentially even floating point non-determinism, and have to send many game state updates... Only one way to find out!

If sbt, scala, and java are set up, `sbt server:run` and `sbt desktop:run` will run the server and client respectively. The client currently connects on localhost.

Code structure explanation:
===

There are three projects: desktop, server, and core. Both desktop and server include core, and most of the code is in core. Currently, desktop and server only include a single Main file each: [Main.scala in desktop](../desktop/src/main/scala/me/shreyasr/networking/Main.scala) and [Main.scala in server](../server/src/main/scala/me/shreyasr/networking/Main.scala).

The desktop Main creates a window and instantiates the main game class, [NetworkingAlgorithms.scala](../core/src/main/scala/me/shreyasr/networking/NetworkingAlgorithms.scala). This class sets up the ECS engine, and contains the main game loop, `render()`. This function currently updates the engine one tick and then renders all the entities. In the future, updates will have to be seperated from rendering, as it is possible for packets to be delayed or received out of order.

All the systems are in [core/src/main/scala/me/shreyasr/networking/system](../core/src/main/scala/me/shreyasr/networking/system), and all the components are in [Components.scala](../core/src/main/scala/me/shreyasr/networking/component/Components.scala)

Controls:

* W and S to accelerate and deccelerate
* A and D to turn
* SPACE to shoot lasers
* M to shoot missiles

There's a ship that shoots lasers and missiles at you that can take 10 hits before dying. This currently is all running locally (but not for long! :D). Another convenient thing about a ECS is the same systems I'm using now will work when running under deterministic lockstep, but will be running on the server too, with a little bit of different code around it dealing with sending and receiving and updating input data.
