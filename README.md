# pyramid-service
This is a work in progress on a Pyramid Solitaire solving web service.
You can try it out online at https://secondthorn.com/pyramid-solitaire/solver
with the appropriate parameters, for example
[Score Challenge to get 291 points](https://secondthorn.com/pyramid-solitaire/solver/score?goalScore=1290&currentScore=999&deck=Ac2c3c4c5c6c7c8c9cTcJcQcKcAd2d3d4d5d6d7d8d9dTdJdQdKdAh2h3h4h5h6h7h8h9hThJhQhKhAs2s3s4s5s6s7s8s9sTsJsQsKs).

Currently, complicated puzzles will run out of heap space on my server, so I'm
working on a solution to that.  On my site, this service is set up using
nginx, PostgreSQL, and Spring Boot, and I'm in the process of adding message
queues for distributed worker processes to solve the more complicated puzzles.

## Introduction
I've been curious about how to write code to find optimal solutions to
[Pyramid Solitaire](https://en.wikipedia.org/wiki/Pyramid_(card_game)) which is
one of my favorite solitaire card games.  The hard part about writing a
program to solve these games is to optimize it.  I've improved the
performance about 50x compared to straightforward implementations of
Breadth-First Search and A\*, and reduced memory usage to the point where it
can run well on a laptop with 8GB of RAM.

Earlier, I created
[Solitaire Player](https://github.com/mchung94/solitaire-player),
which is a program that you can
[download](https://github.com/mchung94/solitaire-player/releases/download/v1.2.0/solitaire-player-v1.2.0.zip).
It can play Pyramid games on Microsoft Solitaire Collection for you, as
long as you have a 64-bit JRE 8 and Microsoft Solitaire Collection for Windows
10.

But if you don't want to download anything, I thought the next step should be
to make it available online as a web service.

## Usage
### Requirements
- 64-bit JDK 8
- Usually the program only needs a few hundred MB of memory but sometimes it
requires a few GB on difficult puzzles.
- It's set up to use PostgreSQL to store game challenges and their solutions,
so you may need to change this.

### Steps
1. Run `gradlew bootJar`, the executable jar will be in build/libs/pyramid-service-0.1.0.jar
2. Make a copy of of [application.properties](src/main/resources/application.properties) and put it [where Spring will load it](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files).
3. Run `java -jar pyramid-service-0.1.0.jar`
4. As an example, in your browser, go to
[http://localhost:8080/pyramid-solitaire/solver/board?deck=ThJsJh9cQd5c2d9hTd4hQs9d3s8dKh6c3h6d8cKcAhQhTc9sKd8s4s2c4cJc7cJd8h6s5d3c4d3d6hTs5sKs7dAc7s2sQc2h5hAs7hAd](http://localhost:8080/pyramid-solitaire/solver/Board?deck=ThJsJh9cQd5c2d9hTd4hQs9d3s8dKh6c3h6d8cKcAhQhTc9sKd8s4s2c4cJc7cJd8h6s5d3c4d3d6hTs5sKs7dAc7s2sQc2h5hAs7hAd)
5. The result is JSON:
```json
[
   {
      "description":"Get 1275 points in 44 steps while clearing the board.",
      "score":1275,
      "boardCleared":true,
      "steps":[
         {
            "stepNumber":1,
            "action":"Remove 4s and 9s"
         },
         {
            "stepNumber":2,
            "action":"Remove Kd"
         },
         {
            "stepNumber":3,
            "action":"Draw"
         },
         {
            "stepNumber":4,
            "action":"Remove 2c and Jc"
         },
         {
            "stepNumber":5,
            "action":"Remove Ah and Qh"
         },
         {
            "stepNumber":6,
            "action":"Remove 6d and 7c"
         },
         {
            "stepNumber":7,
            "action":"Draw"
         },
         {
            "stepNumber":8,
            "action":"Draw"
         },
         {
            "stepNumber":9,
            "action":"Draw"
         },
         {
            "stepNumber":10,
            "action":"Remove 5d and 8s"
         },
         {
            "stepNumber":11,
            "action":"Remove Kc"
         },
         {
            "stepNumber":12,
            "action":"Remove Kh"
         },
         {
            "stepNumber":13,
            "action":"Draw"
         },
         {
            "stepNumber":14,
            "action":"Draw"
         },
         {
            "stepNumber":15,
            "action":"Remove 3d and Tc"
         },
         {
            "stepNumber":16,
            "action":"Draw"
         },
         {
            "stepNumber":17,
            "action":"Draw"
         },
         {
            "stepNumber":18,
            "action":"Remove 5s and 8c"
         },
         {
            "stepNumber":19,
            "action":"Remove Ks"
         },
         {
            "stepNumber":20,
            "action":"Remove 3s and Ts"
         },
         {
            "stepNumber":21,
            "action":"Remove 6h and 7d"
         },
         {
            "stepNumber":22,
            "action":"Draw"
         },
         {
            "stepNumber":23,
            "action":"Remove 6c and 7s"
         },
         {
            "stepNumber":24,
            "action":"Draw"
         },
         {
            "stepNumber":25,
            "action":"Draw"
         },
         {
            "stepNumber":26,
            "action":"Draw"
         },
         {
            "stepNumber":27,
            "action":"Remove 5h and 8d"
         },
         {
            "stepNumber":28,
            "action":"Remove 3h and Td"
         },
         {
            "stepNumber":29,
            "action":"Remove 4h and 9d"
         },
         {
            "stepNumber":30,
            "action":"Remove As and Qs"
         },
         {
            "stepNumber":31,
            "action":"Draw"
         },
         {
            "stepNumber":32,
            "action":"Draw"
         },
         {
            "stepNumber":33,
            "action":"Recycle"
         },
         {
            "stepNumber":34,
            "action":"Remove 4c and 9h"
         },
         {
            "stepNumber":35,
            "action":"Draw"
         },
         {
            "stepNumber":36,
            "action":"Remove 5c and 8h"
         },
         {
            "stepNumber":37,
            "action":"Draw"
         },
         {
            "stepNumber":38,
            "action":"Draw"
         },
         {
            "stepNumber":39,
            "action":"Draw"
         },
         {
            "stepNumber":40,
            "action":"Remove Ac and Qd"
         },
         {
            "stepNumber":41,
            "action":"Remove 2d and Jh"
         },
         {
            "stepNumber":42,
            "action":"Remove 4d and 9c"
         },
         {
            "stepNumber":43,
            "action":"Remove 2s and Js"
         },
         {
            "stepNumber":44,
            "action":"Remove 3c and Th"
         }
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

If you are curious if your deck is formatted correctly, just run it through
the service and it will tell you if there are missing or duplicated cards.

### Endpoints
Given a card deck string like the above, the service supports all of Microsoft
Solitaire Collection's challenges with these endpoints:
- Board Challenges (clearing the 28 pyramid cards)
  - /pyramid-solitaire/solver/board?deck=...
  - It only needs a single query parameter, the deck.
  - This will find the minimum number of steps to clear the 28 pyramid cards,
    even if there are still cards in the stock or waste piles.  If there is no
    solution, it will return a "solution" that just tells you to lose quickly.
- Score Challenges (maximizing the score)
  - /pyramid-solitaire/solver/score?goalScore=2500&currentScore=1800&deck=...
  - It needs three query parameters:
    - goalScore
    - currentScore
    - deck
  - This will find the fastest way to reach the goal score.  If that's not
    possible, it will try to maximize the score overall.  The maximum possible
    score is 1290, so if you want to just maximize the score without a goal in
    mind, use goalScore=1290&currentScore=0.
- Card Challenges (removing cards of a given rank)
  - /pyramid-solitaire/solver/card?goalNumberToRemove=4&rankToRemove=J&currentNumberRemoved=1&deck=...
  - It needs four query parameters:
    - rankToRemove (one of A 2 3 4 5 6 7 8 9 T J Q K)
    - goalNumberToRemove
    - currentNumberRemoved
    - deck
  - This will find the best way to remove the cards to reach the goal.
    For example, with goalNumberToRemove=4, rankToRemove=J, and
    currentNumberRemoved=1, it means your goal is to remove 4 Jacks, and you've
    removed one Jack so far.  This is the solver that can potentially return
    multiple results - in this example, if it found a way to remove 1 Jack
    while clearing the board, or remove 2 Jacks without clearing the board, it
    doesn't know which is better (not clearing the board means having to use
    limited re-deals) so it returns both solutions.


