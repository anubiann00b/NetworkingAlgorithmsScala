Week 7 Update
===

This week, I implemented more of the basic game, including missiles and ship destruction, and started on networking/sockets.

I didn't get to the explosive or wormhole cannon, but I think it's good not to get too into functionality before networking too. Though I guess this makes me a bit behind schedule...

The plan for next week is to get deterministic lockstep up and running. This will be some fun networking stuff! The basic plan I have right now looks like this:

Client --> Request to join game
Server --> Accept, send ID (or reject)
Server --> Send game state
Client --> Send input continuously
Server --> Send all client inputs continuously, send game state occasionally or as requested (or when players join/leave?)

I'm gonna throw some serialization on the components. One nice thing about an ECS is entities are just bags of components which are pretty easy to serialize. A nice feature of deterministic lockstep is sending an entity doesn't need to be optimized, as it happens only occasionally. The bulk of the packets sent back and forth will be input data (`InputComponent`) from the client, which can be as short as a single byte, and a list of `InputComponent`s from the server, one from each client.

If sbt, scala, and java are set up, `sbt server:run` and `sbt desktop:run` will run the server and client respectively. The client currently connects on localhost.

Code structure explanation (filling out on GitHub):
