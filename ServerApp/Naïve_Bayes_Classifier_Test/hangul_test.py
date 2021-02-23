# 필요한 모듈 불러오기

from nltk.tokenize import word_tokenize
import nltk
from collections import Counter

from konlpy.tag import Hannanum
hannanum = Hannanum()

from konlpy.tag import Kkma
kkma = Kkma()

from konlpy.tag import Komoran
komoran = Komoran()

from konlpy.tag import Okt
okt = Okt()
import sys
file=open('output.txt','r',encoding='utf8')
data=file.read().split('**')
chosen_num=[0,0,0]
#print(data)

for i in range(1,len(data)):
    a=data[i].split(']')[1]
    print(i,'/',len(data)-1)
    print('1. hannaum')
    print(hannanum.nouns(a))
    print('2. komoran')
    print(komoran.nouns(a))
    print('3. okt')
    print(okt.nouns(a))
    print('원본')
    print(a)
    while(1):        
        choose=input("숫자를 입력하세요(오류시 0) : ")
        if choose == '0':
            continue
        if choose == '1' or choose == '2' or choose == '3':
            break
    chosen_num[int(choose)-1] += 1

total=chosen_num[0]+chosen_num[1]+chosen_num[2]
print('1. hannaum')
print(chosen_num[0]/total*100,'%')
print('2. komoran')
print(chosen_num[2]/total*100,'%')
print('3. okt')
print(chosen_num[3]/total*100,'%')
