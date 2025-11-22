package xyz.secozzi.aniyomilocalmanager.domain.ktor.ratelimit

import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class RateLimitConfig {
    internal val limits: MutableMap<String, Pair<Int, Duration>> = mutableMapOf()

    /**
     * Adds a rate limit rule for a specific host.
     *
     * @param host The host (e.g., "api.example.com") to apply the limit to.
     * @param permits The number of requests allowed within the specified period.
     * @param period The time window for the rate limit.
     */
    fun addLimit(host: String, permits: Int, period: Duration) {
        limits[host] = permits to period
    }
}

private class TokenBucket(
    private val permits: Int,
    private val period: Duration,
) {
    private val mutex = Mutex()
    private var availableTokens = permits
    private var lastRefillTime = System.currentTimeMillis()

    suspend fun acquire() {
        mutex.withLock {
            refillTokens()

            while (availableTokens <= 0) {
                val timeToWait = period.inWholeMilliseconds - (System.currentTimeMillis() - lastRefillTime)
                if (timeToWait > 0) {
                    mutex.unlock()
                    delay(timeToWait)
                    mutex.lock()
                    refillTokens()
                }
            }

            availableTokens--
        }
    }

    private fun refillTokens() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRefillTime

        if (elapsed >= period.inWholeMilliseconds) {
            availableTokens = permits
            lastRefillTime = now
        }
    }
}

val RateLimitPlugin = createClientPlugin("RateLimitPlugin", ::RateLimitConfig) {
    val limits = pluginConfig.limits
    val buckets = mutableMapOf<String, TokenBucket>()

    limits.forEach { (host, config) ->
        buckets[host] = TokenBucket(config.first, config.second)
    }

    on(SendingRequest) { request, content ->
        val host = request.url.host
        buckets[host]?.acquire()
    }
}
