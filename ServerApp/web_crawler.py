from selenium import webdriver
import time
import pymysql
import naïve_bayes_classifier as NBC
#--------------글로벌 변수--------------

#chromedriver 파일 위치
target="C:\\Users\\podoCU\\Exercises\\chromedriver.exe"

#국민재난안전포털 재난문자 url
main_url = "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679"

#한 사이클 돌리는 대기 시간
sleep_time = 0.1

#데이터베이스 접속용 변수
user=''
passwd=''
host=''
db=''
charset=''

class WebCrawler:
    def __init__(self, c = False):
        self.number=0
        self.main_db = pymysql.connect(user=user,passwd=passwd,host=host,db=db,charset=charset)
        self.classifier = NBC.NaiveBayesClassifier(c)
        self.clear = c
        #user유저네임/passwd비밀번호/host호스트/db데이터베이스명/charset인코딩
    def load_number(self):
        #----DB에서 가장 최근 데이터 불러오기----
        try:
            #검색결과를 딕셔너리 형태로 반환
            cursor=self.main_db.cursor(pymysql.cursors.DictCursor)

            #검색할 명령어
            sql = "SELECT MAX(NUM) FROM Message_List;"
            cursor.execute(sql)
            result=cursor.fetchall()
            self.number=result[0]['MAX(NUM)']
            #불러온 것 중 가장 최근 데이터의 고유번호
            if self.number==None:
                self.number=95800
        except:    
            self.number=95800
            
    def load_db_data(self):
        cursor=self.main_db.cursor(pymysql.cursors.DictCursor)
        sql="SELECT * FROM Message_List"
        if self.clear == False:            
            sql +=" WHERE NUM > "+str(self.number)
        sql += ";"
        cursor.execute(sql)
        data=[]
        while(1):
            result=cursor.fetchone()
            if result != None:
                data.append(result)
            else:
                break
        return data

            
    def crawl_data(self):
        #---------웹 크롤링 드라이버 옵션---------
        options = webdriver.ChromeOptions()

        #백그라운드로 돌리기 위한 옵션
        options.add_argument('headless')
        options.add_argument("disable-gpu")

        #크롬 백그라운드로 실행
        driver = webdriver.Chrome(target,options=options)

        #해당 홈페이지 실행
        driver.get(main_url)

        #DB의 가장 최근 데이터 번호
        number=str(self.number)
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
                data_message = {"num":int(number),"sub_num":i,"date":date_date,"time":date_time,"place":text_place[i],"text":text_text}
                data_message_all.append(data_message)
        return data_message_all   
    def webcrawl(self):
        self.load_number()
        data_message_all = self.crawl_data()
        cursor=self.main_db.cursor(pymysql.cursors.DictCursor)
        #DB에서 데이터 가져온 후 그 번호까지 크롤링
        
        for i in data_message_all:
            num=str(i["num"])
            sub_num=str(i["sub_num"])
            date=i["date"]
            time=i["time"]
            place=i["place"]
            text=i["text"] 
            event=(self.classifier.classify(text)).split('/')[0]
            sql = 'INSERT INTO Message_List(NUM,SUBNUM,M_DATE,M_TIME,REGION,CONTENT,EVENT) VALUES (%s,%s,"%s","%s","%s","%s","%s");'%(num,sub_num,date,time,place,text,event)
            try:
                cursor.execute(sql)
                print(sql)
                #DB에 저장후 성공시 sql 출력
                
            except:
                continue
                #실패시 넘어감
                
        self.main_db.commit()
        print("db upload finished")
        data_list = self.load_db_data()
        if self.clear == True:
            print("data cleared")
        print(len(data_list),"data added")
        self.classifier.train(data_list)
        print("train finished")

crawler = WebCrawler(False)
crawler.webcrawl()
