Week 6 Update
===

This week, I implemented the basic game structure, including movement, shooting the simple laser projectile, collision detection, and health.

The game uses the Ashley entity component system (ECS). Under this system, entities are just sets of components, and the game engine consists of a list of entities and a set of systems which operate on those entities. The engine can be queried for entities using `engine.getEntitiesFor(Family)`, providing a family, which is a specification of what components should be in the entities we want.

Iterating systems specify a family in their constructor, and have `processEntity(Entity)` called on each entity that matches that family every iteration.

Entity sprites are handled as a list of lines. These lines also double as the entity hitbox. It's dirty but quite effective for the scope of this project.

The game development stuff isn't terribly new to me, but I'm really enjoying working in Scala. There are a couple paradigms I really like:

> NetworkingAlgorithms.scala:110
```
lines
      .map(_.getCartesian(pos.x, pos.y, dir))
      .foreach{case (x1, y1, x2, y2) => { shapeRenderer.line(x1, y1, x2, y2) }}
```

I'm a big fan of this functional/stream type paradigm. I'm not sure exactly what to call it. In Java/Android, a similar pattern is called Functional Reactive Programming but I feel like there's a better term for it, especially in the context of a functional language.

> NetworkingAlgorithms.scala:103
```
val damage = entity.getOpt[ShipStatsComponent]
      .map(s => s.health.toFloat / s.maxHealth)
      .map(Utils.clamp(_, 0, 1))
      .getOrElse(1f)
```

The Option type is one of my favorite things ever. I believe it's equivalent to the Maybe monad in Haskell, kinda being a union type. It's either `Some(x)` or `None`. It's really useful to do the thing where you operate on the contained object within the monad via map in this case. Here, we're getting a ShipStatsComponent that may or may not exist, and mapping it to get the health of the ship between 0 and 1, and then getting it if it exists or 1 if it does not. 

> CollisionSystem.scala:66
```
val linePairs = for (a <- eLines; b <- pLines) yield (a, b)
```

List comprehension! Heck yeah! Ignoring the not ideal nature of this algorithm, it's real nice to be able to express this this concisely and clearly. The code is a lot flatter than the double for loop you'd have to do in Java (though that's pretty readable too). However, there definitely are examples where list comprehension is a lot clearer than nested loops and conditionals.

Next week, I will continue with the basic functionality of the game, and add in a couple more interesting weapons. The main goal of these weapons is to challenge client/server synchronization. I'm planning on implementing a seeking missile, an explosive that delivers force on nearby entities when it goes off, and a wormhole that provides a constant gravitational pull. From there, it's networking time!

If sbt, scala, and java are set up, `sbt desktop:run` will run the project.
