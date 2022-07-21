import json
import requests


url="http://127.0.0.1:8080/query"
d ={}
# d={"name":"mdd", "page":1}

r = requests.get(url, data=d)
print(r.content)