## jmeter-redis-plugins

The plugin can be used in Apache JMeter(5.4.1).You can run `maven package` and put the JAR package under the folder `lib/ext`.

You can add a component named `Redis Sampler` in the path `Add/Sampler`.

You can set redis connection in the field `Server` like `127.0.0.1:6379`, and redis cluster like `host1:port1,host2:port2`, default port is `6379`.

Supported Actions: GET / SET

Supported Types: String / Hash / List / Set

You can set hash value like `{"key":"value"}`, set list and set value like `value1,value2,value3`.
