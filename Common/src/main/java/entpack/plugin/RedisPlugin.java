/**
 * Copyright (c) 2011-2019, James Zhan 詹波 (jfinal@126.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package entpack.plugin;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.IKeyNamingPolicy;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.serializer.FstSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * RedisPlugin.
 * RedisPlugin 支持多个 Redis 服务端，只需要创建多个 RedisPlugin 对象
 * 对应这多个不同的 Redis 服务端即可。也支持多个 RedisPlugin 对象对应同一
 * Redis 服务的不同 database，具体例子见 jfinal 手册
 */
public class RedisPlugin extends com.jfinal.plugin.redis.RedisPlugin {

    public RedisPlugin(String cacheName, String host) {
        super(cacheName, host);
    }

    public RedisPlugin(String cacheName, String host, int port) {
        this(cacheName, host);
        this.port = port;
    }

    public RedisPlugin(String cacheName, String host, int port, int timeout) {
        this(cacheName, host, port);
        this.timeout = timeout;
    }

    public RedisPlugin(String cacheName, String host, int port, int timeout, String password) {
        this(cacheName, host, port, timeout);
        this.password = password;
    }

    public RedisPlugin(String cacheName, String host, int port, int timeout, String password, int database) {
        this(cacheName, host, port, timeout, password);
        this.database = database;
    }

    public RedisPlugin(String cacheName, String host, int port, int timeout, String password, int database, String clientName) {
        this(cacheName, host, port, timeout, password, database);
        if (StrKit.isBlank(clientName))
            throw new IllegalArgumentException("clientName can not be blank.");
        this.clientName = clientName;
    }

    public RedisPlugin(String cacheName, String host, int port, String password) {
        this(cacheName, host, port, Protocol.DEFAULT_TIMEOUT, password);
    }

    public RedisPlugin(String cacheName, String host, String password) {
        this(cacheName, host, Protocol.DEFAULT_PORT, Protocol.DEFAULT_TIMEOUT, password);
    }

    public void setDatabase(int database) {
        this.database = database;
    }


    @Override
    public boolean start() {
        JedisPool jedisPool;
        if (port != null && timeout != null && password != null && database != null && clientName != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database, clientName);
        else if (port != null && timeout != null && database != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        else if (port != null && timeout != null && password != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        else if (port != null && timeout != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        else if (port != null)
            jedisPool = new JedisPool(jedisPoolConfig, host, port);
        else
            jedisPool = new JedisPool(jedisPoolConfig, host);

        if (serializer == null)
            serializer = FstSerializer.me;
        if (keyNamingPolicy == null)
            keyNamingPolicy = IKeyNamingPolicy.defaultKeyNamingPolicy;

        Cache cache = new Cache(cacheName, jedisPool, serializer, keyNamingPolicy);
        Redis.addCache(cache);
        return true;
    }
}


