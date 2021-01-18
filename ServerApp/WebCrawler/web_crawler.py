import math 
from selenium import webdriver
import time
from tqdm import tqdm
import re
options = webdriver.ChromeOptions()
options.add_argument('headless')
options.add_argument("disable-gpu")
driver = webdriver.Chrome(r"C:\Users\podoCU\Exercises\chromedriver.exe")
#options=options
number=0
text=''
flag=0

while(flag!=-1):
    if(flag > 10):
        flag=0
        time.sleep(3)
        driver = webdriver.Chrome(r"C:\Users\podoCU\Exercises\chromedriver.exe")
    main_url = "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679"
    driver.get(main_url)
    title2=driver.find_elements_by_id("bbs_tr_0_bbs_title")
    print(title2,end='')
    if(title2==[]):
        #time.sleep(5)
        flag += 1
        continue
    flag=-1
    text = title2[0].text
    label = title2[0].get_attribute('href')
    
label=label.split(':')[1]
number = re.findall("\d+", label)[1] #str
while(1):
    label2="bbsDtl('63','"+number+"');"
    print(label2)
    driver.execute_script(label2)
    date_all=(driver.find_element_by_id("sj")).text
    text_all=(driver.find_element_by_id("cn")).text
    
    date_date=date_all.split(' ')[0]
    date_time=date_all.split(' ')[1]
    
    print(date_time)
    print(date_date)
    
    text_text=text_all.split("-송출지역-")[0]
    text_place=text_all.split("-송출지역-")[1]
    print(text_text)
    print(text_place)
    
    time.sleep(5)
    driver.get(main_url)
    number=str(int(number)-1)
    
