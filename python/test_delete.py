import requests


url="http://1.117.89.152:8080/delete"
d={"id":20}

r = requests.post(url, data=d)
print(r.content)