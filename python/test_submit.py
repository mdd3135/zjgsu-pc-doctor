import requests


url="http://1.117.89.152:8080/submit"
d={"name":"yzw", "contact_details":"phone:142832", "problem_description":"拆机清灰", "problem_category":"拆机", "status":0}

r = requests.post(url, data=d)
print(r.content)