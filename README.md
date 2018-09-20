# pyramid-service
This is a work in progress on a Pyramid Solitaire solving web service.

## Introduction
I've been curious about how to write code to find optimal solutions to
[Pyramid Solitaire](https://en.wikipedia.org/wiki/Pyramid_(card_game)), which
is a pretty common solitaire game.

So far I've created
[Solitaire Player](https://github.com/mchung94/solitaire-player),
which is a program that you can
[download](https://github.com/mchung94/solitaire-player/releases/download/v1.2.0/solitaire-player-v1.2.0.zip)
and it can play Pyramid games on Microsoft Solitaire Collection for you (as
long as you have a 64-bit JRE 8 and Microsoft Solitaire Collection for Windows
10).

But if you don't want to download anything, I thought the next step should be
to make it available online as a web service.

Currently this is using a brand new solver designed to use less memory, and has
the basic web functionality working.  It's not yet deployed online anywhere,
but you can run it yourself if you have JDK 8.  There's a lot of work to be
done on the actual web part of the code, but the Pyramid Solitaire solving
code is ready.

## Usage
### Requirements
- 64-bit JDK 8
- Usually the program only needs a few hundred MB of memory but sometimes it
requires a few GB on difficult puzzles.

### Steps
1. Run `gradlew bootJar`, the executable jar will be in build/libs/pyramid-service-0.1.0.jar
2. Run `java -jar pyramid-service-0.1.0.jar`
3. As an example, in your browser, go to
[http://localhost:8080/pyramid-solitaire/solver/Board?deck=ThJsJh9cQd5c2d9hTd4hQs9d3s8dKh6c3h6d8cKcAhQhTc9sKd8s4s2c4cJc7cJd8h6s5d3c4d3d6hTs5sKs7dAc7s2sQc2h5hAs7hAd](http://localhost:8080/pyramid-solitaire/solver/Board?deck=ThJsJh9cQd5c2d9hTd4hQs9d3s8dKh6c3h6d8cKcAhQhTc9sKd8s4s2c4cJc7cJd8h6s5d3c4d3d6hTs5sKs7dAc7s2sQc2h5hAs7hAd)
4. The result is JSON:
```json
[
   {
      "description":"Get 1275 points in 44 steps while clearing the board.",
      "score":1275,
      "actions":[
         "Remove 9s and 4s",
         "Remove Kd",
         "Draw",
         "Remove 2c and Jc",
         "Remove Ah and Qh",
         "Remove 6d and 7c",
         "Draw",
         "Draw",
         "Draw",
         "Remove 8s and 5d",
         "Remove Kc",
         "Remove Kh",
         "Draw",
         "Draw",
         "Remove Tc and 3d",
         "Draw",
         "Draw",
         "Remove 8c and 5s",
         "Remove Ks",
         "Remove 3s and Ts",
         "Remove 6h and 7d",
         "Draw",
         "Remove 6c and 7s",
         "Draw",
         "Draw",
         "Draw",
         "Remove 8d and 5h",
         "Remove Td and 3h",
         "Remove 4h and 9d",
         "Remove Qs and As",
         "Draw",
         "Draw",
         "Recycle",
         "Remove 9h and 4c",
         "Draw",
         "Remove 5c and 8h",
         "Draw",
         "Draw",
         "Draw",
         "Remove Qd and Ac",
         "Remove Jh and 2d",
         "Remove 9c and 4d",
         "Remove Js and 2s",
         "Remove Th and 3c"
      ]
   }
]
```

### Building a Deck
Cards consist of a rank (A 2 3 4 5 6 7 8 9 T J Q K) followed by a suit
(c d h s).  Note that a ten is the letter T and that the ranks are uppercase
and the suits are lowercase.  Pyramid Solitaire uses one standard 52-card deck.
In order by suit (c d h s) the list of cards
```Ac2c3c4c5c6c7c8c9cTcJcQcKcAd2d3d4d5d6d7d8d9dTdJdQdKdAh2h3h4h5h6h7h8h9hThJhQhKhAs2s3s4s5s6s7s8s9sTsJsQsKs```
would look like this:
```
            Ac
          2c  3c
        4c  5c  6c
      7c  8c  9c  Tc
    Jc  Qc  Kc  Ad  2d
  3d  4d  5d  6d  7d  8d
9d  Td  Jd  Qd  Kd  Ah  2h
top of stock pile -> 3h 4h 5h 6h 7h 8h 9h Th Jh Qh Kh As 2s 3s 4s 5s 6s 7s 8s 9s Ts Js Qs Ks
```

### Services
Given a card deck string like the above, the service supports all of Microsoft
Solitaire Collection's challenges with these URLs:
- /pyramid-solitaire/solver/Board?deck=...
  - The Board Challenge solver is for finding the minimum number of steps to
  clear the 28 pyramid cards, even if there are still cards in the stock or
  waste piles.
- /pyramid-solitaire/solver/Score?deck=...
  - The Score Challenge solver finds the way to get the maximum score.
- /pyramid-solitaire/solver/Score?goal=2500&current=1800&deck=...
  - The Score Challenge solver, given the goal score and the current score,
  will find the fastest way to reach the goal score.
- /pyramid-solitaire/solver/Card?goal=4&rank=J&current=1&deck=...
  - The Card Challenge solver, given a card rank, a goal number of cards to
  remove (of that rank), and the current number of cards removed so far, will
  find the best way to remove that many cards.  For example, with goal=4,
  rank=J, and current=1, it means your goal is to remove 4 Jacks, and you've
  removed one Jack so far.  This is the solver that can potentially return
  multiple results - in the examples of the 4 Jacks, if it found a way to
  remove 1 Jack while clearing the board, or remove 2 Jacks without clearing
  the board, it doesn't know which is better (not clearing the board means
  having to use limited re-deals) so it returns both solutions.
