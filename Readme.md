# Context

As of late, I have spent a fair bit of time exploring elementary concepts of Cryptography. This is in part because the practical security concerns of the field are some I find interesting, and in part because in dealing with Cryptography, there is a lot of nitty-gritty work with bit manipulation, an area I typically find daunting, and am striving to improve upon.

As the TA of a 2nd year data structures class, I was helping a student who was working with JAVA, and as they were explaining the logic of their assignment solution to me, they mentioned they were using JAVA's random object to generate a stream of random numbers.

Immediately, my recent studies in Cryptography made me [skeptical](https://en.wikipedia.org/wiki/Pseudorandomness). I reasoned that it is highly likely that JAVA's random object was using some pseudorandom process to produce this "random" stream of numbers, and challenged myself to decrypt it. Surely, given some number of outputs from a random object's call to `nextInt()`, I would be able to predict all future values?

After a little bit of skimming through [documentation](https://docs.oracle.com/javase/8/docs/api/java/util/Random.html), I found that an instance of a JAVA random object does indeed generate a stream of pseudorandom numbers, by using a 48-bit secret seed and a linear congruential formula, and set out to decrypt it.

# Linear Congruential PRNGs

The basic concept behind a Linear Congruential PRNG is an algorithm that uses a stored state (the seed) that has a specific precision (in JAVA's case, 48 bits). The values are then generated with the following recurrence relation:

`X(n+1) = (a * X(n) + c) mod m`

Where X is the sequence of pseudorandom numbers, X(0) is the initial seed or start value, a represents an unchanging "multiplier", and c, an unchanging "increment".

In the case of JAVA, documentation revealed that the a value used in `nextInt()` is 25214903917, and the c value used is 11. Why these values are selected and what makes them good values was beyond the scope of my findings, but a valuable future consideration in my studies.

# JAVA's nextInt() method

We have already mentioned that JAVA uses a precision of 48 bits. For this reason, the m value used for the modulo operation is a bitmask of 48 1's, or 281474976710655 in decimal. This is because, if the seed is represented in 48 bits, it is impossible for the seed in decimal to exceed this value.

The final step that is performed to obtain the output after applying the aforementioned recurrence relation is to bit-shift the result to the right by 16 bits. This is done to accomplish two things:

a) Convert the 48 bit seed into a 32 bit integer, since the method `nextInt()` is to return an integer. (Other calls to methods such as `nextLong()` simply generate multiple 32 bit numbers and combine them).

b) Allows the PRNG to never give out the full 48 bits of the seed. At most, on each call to `nextInt()`, only 32 bits are given out.


# Decryption

This script instantiates an instance of JAVA's random object, and then uses two values that were gathered from calls to `nextInt()`. With two integers, the only information we're missing to work backwards and obtain the seed is the 16 bits that were lost in the shifting. 16 Bits only represents at most 65536 values, so the correct seed can be obtained in a fraction of a second even with a brute force approach.

Once the seed is obtained, with our knowledge of the a, m, and c values, the trick of decrypting all future values in the sequence becomes quite trivial. 
