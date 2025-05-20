### 请求头加envMark正常传递
请求接口: 获取feign调用被调用方header的key对应的value
```
GET http://localhost:9009/api/get-remote-header?key=envMark
headers:
    envMark: test-dynamic-env
```
响应
```
test-dynamic-env
```
打印日志
```
header key: envMark, header: test-dynamic-env
```


### 请求头加env，不会传递
请求接口: (未传递情况)
```
GET http://localhost:9009/api/get-remote-header?key=env
headers:
    env: test-dynamic-env
```
响应
```
(empty)
```
打印日志
```
header key: env, header: null
```