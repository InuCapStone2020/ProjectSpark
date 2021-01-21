from selenium import webdriver
import math 
import time
import re

#--------------글로벌 변수--------------

#chromedriver 파일 위치
target="C:\\Users\\podoCU\\Exercises\\chromedriver.exe"

#국민재난안전포털 재난문자 url
main_url = "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679"

#한 사이클 돌리는 대기 시간
sleep_time = 1

#----DB에서 가장 최근 컨텐츠 불러오기----

#불러오는 함수 넣을 자리

#불러온 것 중 가장 최근 데이터의 고유번호
number=86188

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
number=str(number) #int->str
flag=False
cnt=0

while(1):
    number=str(int(number)+1)
    label2="bbsDtl('63','"+number+"');"
    print(label2)#test
    driver.get(main_url)
    driver.execute_script(label2)
    
    temp1 = driver.find_element_by_id("bbs_next").text
    temp2 = driver.find_element_by_id("bbs_gubun").text
    
    if(temp1 == temp2 and flag==False):
        flag=True
        time.sleep(sleep_time)
        number=str(int(number)-1)
        continue
        
    elif(temp1 == temp2 and flag==True):
        cnt += 1
        if (cnt >= 10):
            driver.quit()
            break
        flag=False
        time.sleep(sleep_time)
        continue
    
    cnt=0
    if (flag==True):
        flag=False
        
    date_all=(driver.find_element_by_id("sj")).text
    
    if(date_all==''):
        date_all=(driver.find_element_by_id("sj")).text
    
    date_date=date_all.split(' ')[0]
    date_time=date_all.split(' ')[1]
    
    print(date_time)
    print(date_date)
    
    text_all=(driver.find_element_by_id("cn")).text
    
    text_text=text_all.split("-송출지역-")[0]
    text_place=text_all.split("-송출지역-")[1]
    text_place_high=text_place.split(" ")[0]
    text_place_low=text_place.split(" ")[1]
    
    
    print(text_text)
    print(text_place)
    print(text_place_high)
    print(text_place_low)
    time.sleep(sleep_time)
    
    
    #---------데이터 변환 모듈---------
    message_data = {"번호":number,"날짜":date_date,"시간":date_time,"지역_상위":text_place_high,"지역_하위":
                   text_place_low,"내용":text_text}
    print(message_data)
    
    
