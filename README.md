# Discript
Discript is a work-in-progress proof of concept scripting language for the [Discord API](https://discord.dev). Similar to discord.jar, the library it's built on, Discript is being built as a proof-of-concept and as just a fun hobby project to do - it's not production ready and won't necessarily make it there.

## Files
All Discript files end with the file ending `.discript`.

## Syntax
During Discript's development, you'll be able to fiind essentially the entire syntax inside the test.dscript file. It uses a similar style to Python for code blocks, so indentation is necessary (by 1 tab, not spaces!), and most primitives will be defined in a relativly similar way, such as "string" or 123.
At the top of each script, there's a "@login with " statement - which defines the Discord bot token. You can either provide a string straight in the file, or use an environment variable with: `env:ENV_VARIABLE_NAME`. Event listeners are defined using `on EVENT_TYPE_HERE as eventVariableNameHere:`, with the codeblock
following as an indented section.
<br><br>
<strong>In-built functions</strong>
<ul>
  <li>`log("log message here")` - prints to the standard output</li>
  <li>`str(NON-STRING-HERE)` - converts anything to a string</li>
</ul>

<strong>Event names</strong><br>
A full list of event names can be found on Discord's developer documentation: https://discord.com/developers/docs/topics/gateway-events#event-names
<br><br>
When defining an event listener with something like `on MESSAGE_CREATE as event`, the event variable, in this case `event`, is an object with the same structure as the raw JSON provided by Discord. For example, if you wanted to then print the author of a message's username, you'd write: `log(event.author.username)`
