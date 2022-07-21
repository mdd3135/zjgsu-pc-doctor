import requests


url="http://127.0.0.1:8080/update"
d={"id":9, "name":"update", "contact_details":"phone:5432", "problem_description":"测试", "problem_category":"测试", "status":1}

r = requests.post(url, data=d)
print(r.content)