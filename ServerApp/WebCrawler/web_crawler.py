from selenium import webdriver
import time
import re
import json
import pymysql
#--------------글로벌 변수--------------

#chromedriver 파일 위치
target="C:\\Users\\podoCU\\Exercises\\chromedriver.exe"

#국민재난안전포털 재난문자 url
main_url = "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679"

#한 사이클 돌리는 대기 시간
sleep_time = 1
#----DB에서 가장 최근 데이터 불러오기----

main_db = pymysql.connect(
    user='user'
    ,passwd='passwd'
    ,host='127.0.0.1'
    ,db='main_db'
    ,charset='utf8'
)
#user유저네임/passwd비밀번호/host호스트/db데이터베이스명/charset인코딩  port3306

#검색결과를 딕셔너리 형태로 반환
cursor=main_db.cursor(pymysql.cursors.DictCursor)

#검색할 명령어
sql = "SELECT MAX(NUM) FROM Message_List;"
cursor.execute(sql)
result=cursor.fetchall()
number=result[0]['MAX(NUM)']
print(number)
#불러온 것 중 가장 최근 데이터의 고유번호

number=91870
#---------웹 크롤링 드라이버 옵션---------
options = webdriver.ChromeOptions()

#백그라운드로 돌리기 위한 옵션
options.add_argument('headless')
options.add_argument("disable-gpu")

#크롬 실행
driver = webdriver.Chrome(target)

#크롬 백그라운드로 실행
#driver = webdriver.Chrome(target,options=options)

#해당 홈페이지 실행
driver.get(main_url)
#---------웹 크롤링 모듈---------

#DB의 가장 최근 데이터 번호
number=str(number)
#웹 페이지 접속 오류 재시도를 위한 변수
flag=False
#없는 번호 및 프로그램 종료 시점을 알기 위한 카운트 변수
cnt=0
#데이터 저장하기 위한 변수
data_message_all=[]

while(1):
    #가장 최근 데이터 + 1
    number=str(int(number)+1)
    #해당 번호로 접속하기 위한 jsp 명령어
    label2="bbsDtl('63','"+number+"');"
    #해당 페이지 접속
    driver.get(main_url)
    driver.execute_script(label2)
    
    #다음 페이지, 이전 페이지의 텍스트
    temp1 = driver.find_elements_by_id("bbs_next")[0].text
    temp2 = driver.find_elements_by_id("bbs_gubun")[0].text
    
    #텍스트가 같으면 둘다 "데이터가 없습니다"이므로 재시도, +1이 되므로 다시 -1
    if(temp1 == temp2 and flag==False):
        flag=True
        time.sleep(sleep_time)
        number=str(int(number)-1)
        continue
    
    #재시도했는데 없으면 없는 것으로 간주, 다음으로 넘어감
    #10번 다음으로 넘어갔는데 없으면 더이상 없는 것으로 간주, 프로그램 종료
    elif(temp1 == temp2 and flag==True):
        cnt += 1
        if (cnt >= 10):
            driver.quit()
            break
        flag=False
        time.sleep(sleep_time)
        continue
    
    #위 구간을 넘어가면 있는 것이므로 변수 초기화
    cnt=0
    if (flag==True):
        flag=False
    
    #제목에 있는 날짜와 시간을 구분하여 저장
    date_all=(driver.find_element_by_id("sj")).text
    
    if(date_all==''):
        date_all=(driver.find_element_by_id("sj")).text
    
    date_date=date_all.split(' ')[0]
    date_time=date_all.split(' ')[1]
    
    #본문에 있는 내용과 지역을 구분하여 저장
    text_all=(driver.find_element_by_id("cn")).text
    
    text_text=text_all.split("-송출지역-")[0]
    text_place_all=text_all.split("-송출지역-")[1]
    
    #지역이 여러개일 수도 있으므로 리스트에 저장
    text_place=[]
    for i in range(len(text_place_all.split("\n"))):
        temp=text_place_all.split("\n")[i]
        if(temp != ''):
            text_place.append(temp)

    time.sleep(sleep_time)
    
    #---------데이터 변환 모듈---------
    for i in range(len(text_place)):
        #저장한 데이터를 JSON형식으로 변환
        data_message = {"num":int(number),"sub_num":i,"date":date_date,"time":date_time,"place":text_place[i],"text":text_text}
        data_message_all.append(data_message)
#---------JSON파일로 저장 및 파일 생성---------
data_message = {"data":data_message_all}
#저장할 JSON 파일 이름
file_name=str(number)+'.json'

with open(file_name,"w",encoding="utf-8")as fp:
    data_json=json.dump(data_message,fp,ensure_ascii=False,indent='\t')

#데이터를 JSON파일로 저장 및 보기 ensure-한글표시 indent-여러줄로 표시
data_json=json.dumps(data_message,ensure_ascii=False,indent='\t')
print(data_json)
