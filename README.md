I'm writing this at 3am, so this is going to be coherent...

*Features:*

 * TOML 0.5 "compliant"
 * Fast
 * Memory Efficient
 * Other buzzwords
 * Simple inheritance model (seriously though...)

*This library:*

It's a library that "fully" implements the TOML 0.5 spec.
I say "fully", because I have no doubt that I've just completely ignored it in some areas...

For the most part, it should follow along close enough.

Oh, I think you can currently declare duplicate keys...
So, I'll probably have to fix that.
Maybe a strict mode and have it on by default.
Yeah, I think that'll be a good idea.

That way we don't get a bunch of idiots, myself included, complaining that TOML isn't secure
because you can override keys...
I don't know how that's a security issue, but I have no doubt that someone somewhere will make it one...

*The idea behind the library:*

Basically, the thing that drove me to write this library was, all of the current libraries are not very efficient
and require entire language runtimes so that they can parse the thing.
Having actually looked at the toml spec before, I knew that it wasn't difficult, so I thought, why not.

The idea was to make a library that was fast, efficient, and requires no runtimes.

I only use JFlex for the lexer because I've never used it, and I wanted to try it out...
I wish I had a better reason...
I've written a ton of lexers by hand, and after spending far too long trying to work out this new fancy syntax, I 
can honestly say "ehh".

I spent a lot of time dealing with weird things that it did.
Each time was because of an error on my half, but still, it didn't help.

*Current Status:*

This is still a WIP.
The library itself is fully functional.
It's not the best to use, but that's the next goal.
Quality of Life.

I'll be making the API easier to use in the coming days/weeks/months.

*A quick note about inheritance:*

So, interestingly, this library does support toml inheritance.
When you parse the TOML, you have the option to give it a base/root.
If you give it the result from a previous file, then boom, you've got inheritance.

It overwrites the values from the first with the values from the second.
