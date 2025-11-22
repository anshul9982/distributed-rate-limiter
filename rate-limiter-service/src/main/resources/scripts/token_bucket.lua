-- token_bucket.lua
local key = KEYS[1]
local max_tokens = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2]) -- tokens per second
local now = tonumber(ARGV[3]) -- current unix timestamp
local requested = tonumber(ARGV[4])

-- Data structure in Redis: Hash Map
-- { "tokens": 5, "last_refill": 1700000000 }

local last_tokens = tonumber(redis.call('hget', key, 'tokens'))
local last_refill = tonumber(redis.call('hget', key, 'last_refill'))

-- If this is the first request, initialize
if not last_tokens then
    last_tokens = max_tokens
    last_refill = now
end

-- 1. REFILL THE BUCKET
-- Calculate how much time passed since last refill
local delta = math.max(0, now - last_refill)
-- Calculate tokens to add: (time_passed * rate)
local filled_tokens = math.min(max_tokens, last_tokens + (delta * refill_rate))

-- 2. CHECK IF WE HAVE ENOUGH TOKENS
local allowed = 0
local new_tokens = filled_tokens

if filled_tokens >= requested then
    allowed = 1
    new_tokens = filled_tokens - requested
end

-- 3. SAVE STATE
redis.call('hset', key, 'tokens', new_tokens)
redis.call('hset', key, 'last_refill', now)
-- Set TTL (expire key if idle for a while to save RAM)
redis.call('expire', key, 60)

-- Return [allowed (1/0), tokens_left]
return { allowed, new_tokens }