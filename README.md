[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jezza/toml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jezza/toml)

Toml
---

```java
// TomlTable has all of the same methods has HashMap. (computeIfAbsent, put, etc)
TomlTable table = Toml.from(new StringReader("key = \"value\""));
```

## Features:

 * TOML 0.5 compliant
 * Fast
 * Memory Efficient
 * Zero dependencies
 * Simple inheritance model

The main goals are simple:  
Remain fast and lightweight.

This library has no runtime dependencies.  
It uses JUnit for tests, and JFlex at compile-time.

### Usage

The API should be easy enough to follow.

The classes that most people will care about are: `Toml`, `TomlTable`, and `TomlArray`.


#### `Toml`

You can use `Toml` to read a `TomlTable` from an `InputStream` or `Reader`.  
You can also provide an existing `TomlTable` to read into, meaning, you can chain multiple toml inputs together.

#### `TomlTable`

Very simply, a `HashMap`.
It supports dotted keys, but other than that, it's more or less exactly what you'd find in a normal `HashMap`.

You can call `asMap` to get the underlying `Map` implementation.
Any modifications to this map will reflect in the `TomlTable` instance.

#### `TomlArray`

Simply an ArrayList.
You can perform all of the same actions.
You can call the `asList` method to get the underlying `List` implementation.
Any modifications to this list will reflect in the `TomlArray` instance.


### Values/Objects

The values you can retrieve from the collections are as follows:

1) `Boolean/boolean`
2) `Double/double`
3) `Long/long`
4) `String`
5) `TomlArray`
6) `TomlTable`
7) `TemporalAccessor`

The only one of note is the `TemporalAccessor`.

Using it is as simple as:
```java
OffsetDateTime.from((TemporalAccessor) table.get("timestamp"));
LocalDateTime.from((TemporalAccessor) table.get("timestamp"));
LocalDate.from((TemporalAccessor) table.get("timestamp"));
LocalTime.from((TemporalAccessor) table.get("timestamp"));
```

### Inheritance

The `Toml` class provides access to the underlying infrastructure.

Inheritance is as simple as:

```java
StringReader first = new StringReader("value = \"Hello, World!\"");
StringReader second = new StringReader("value = \"Bye Bye, World!\"");

TomlTable table = Toml.from(first, second);
```

The inheritance model isn't _perfect_.  
There's a couple of pain points with it I'd like to address, but it's not critical.  
For example, you can't delete keys or array entries.  
To reduce surprises, it's best to think of it as one big input, because that's how it acts.

### Language (Parser/Lexer)

I've exported the lang package.  
That contains the parser and the lexer, should you wish to utilise them.

Have fun!


### Extension Mode

There is also a nonstandard mode, should you wish to utilise some non-standardised features.
You need to opt in by setting a System property.
You'll find the property inside of `Toml`.

* Relative Table Paths.

By prepending a dot, you can refer to the previous table.
Taking a lot of the tedium out when writing big headers. 

```toml
[[items]]
id = "0"

# Is the same as [items.metadata]
[.metadata]
special = "0-0"
```
