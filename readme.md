# **FOOAAAHH Proxy**

Always returns a json object with the boolean `"success"` as long as the status code is `200`<br>
If `"success": true`, the data will be in the same json object.<br>
If `"success": false`, there will be a string called `"reason"` that says why the request failed.<br>

The server may respond with `429 TOO MANY REQUESTS` if you are being rate limited<br>
Any other status code and an internal server error occured, contact devs if it persists

---------------------------------

## Session requests

---------------------------------
## Using a Session Token
You must [create a session](#creating-a-new-session) and use the given key to preform any session requests.<br>
The key becomes invalid after `10 minutes` without any requests, use a [heartbeat](#heartbeats) to keep it open.<br>
If the client **DOES** attempt a request using an invalid key, the server will respond by redirecting the client to the [log-in](https://fooaaahh.jcwyt.com/) page.

---------------------------------

## Creating a New Session
Run when a username is submitted and save session token for later use  
`POST` request to `/fooaaahh/session/new`

### Request Object

```jsonc
{
  "username": "John Doe",
// This username is what will appear on the leaderboard and be used for submitting scores
}
```

### Return Object

```jsonc
{
  "success": true,
  "session_token": "483dc26e-5e30-45d7-b79d-4f772a9f651f",
  "base64key": "N/Hi+aW0ZwHKgZ2GSMwJvQ=="
}
```

---------------------------------
## Heartbeats
Run this every so often because sessions will time out after 10 minutes without a type of request.<br>
`POST` request to `/fooaaahh/session/heartbeat`

### Request Object

```jsonc
{
  "session_token": "483dc26e-5e30-45d7-b79d-4f772a9f651f"
}
```

### Return Object

```jsonc
{
  "success": true
}
```

---------------------------------
## Starting a game
Run this whenever a game is started, the time of start will be logged
`POST` request to `/fooaaahh/session/startgame`

### Request Object

```jsonc
{
  "session_token": "483dc26e-5e30-45d7-b79d-4f772a9f651f"
}
```

returns

```jsonc
{
  "success": true
}
```

---------------------------------
## Ending a game
Run this to submit a score regardless of if its your high score  
`POST` request to `/fooaaahh/session/endgame`

### Request Object

```jsonc
{
  "session_token": "483dc26e-5e30-45d7-b79d-4f772a9f651f",
  "score": "ZJBAU3ymXu0o/GSFUC4btA==" // Encrypted using the base64key from creating a session 
}
```
See [here](https://github.com/1withspaghetti/FooaahhAPI/tree/main/src/main/resources/encrypt.html) for how to encrypt the score
### Return Object

```jsonc
{
  "success": true,
  "username": "John Doe"
}
```
