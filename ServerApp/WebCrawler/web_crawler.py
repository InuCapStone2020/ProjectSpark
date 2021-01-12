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

while(flag==0):
    main_url = "https://www.safekorea.go.kr/idsiSFK/neo/sfk/cs/sfc/dis/disasterMsgList.jsp?menuSeq=679"
    driver.get(main_url)
    flag=0
    title2=driver.find_elements_by_id("bbs_tr_0_bbs_title")
    print(title2)
    if(title2==[]):
        #time.sleep(5)
        continue
    flag=1
    text = title2[0].text
    label = title2[0].get_attribute('href')
    
label=label.split(':')[1]
number = re.findall("\d+", label)[1] #str
print(label)
while(1):
    label2="bbsDtl('63','"+number+"');"
    print(label2)
    driver.execute_script(label2)
    time.sleep(5)
    driver.get(main_url)
    number=str(int(number)-1)
    
