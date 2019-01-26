[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jezza/toml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jezza/toml)

Usage
---

```java
// table is basically just a HashMap. Has all of the same methods, etc...
TomlTable table = Toml.from(new StringReader("key = \"value\""));
```

Features:
---

 * TOML 0.5 "compliant"
 * Fast
 * Memory Efficient
 * Zero dependencies
 * Simple inheritance
 * Other buzzwords

---

The main goals are simple.  
Remain fast and lightweight.

This library has no dependencies. (Save JUnit for tests, and JFlex at compile-time)

The API should be easy enough to use.

I had to make some decisions that might make it a bit annoying to work with.

For example (As of writing):

`TomlTable` and `TomlArray` don't provide any help towards the objects you can pull out of them.

The javadoc elaborates a bit more, but the types you can pull out are:
Boolean | Double | Long | String | TomlArray | TomlTable | TemporalAccessor

Misc
---

The actual implementation is really simple.
The only classes most people will care about is:
`Toml`, `TomlTable`, `TomlArray`.

`Toml` can be used to quickly read a TomlTable from a `Reader` or `InputStream`.

Whereas `TomlTable` and `TomlArray` are data structures.
`TomlArray` is just an ArrayList, nothing special.

`TomlTable` is a `LinkedHashMap`, but with a couple of tricks surrounding keys.
It also has a method to convert it to a map.

Side-note: I've exported the `lang` package too, so if you want to take control of the parsing,
you're more than welcome to.
