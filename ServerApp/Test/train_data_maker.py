import sys
file=open('output_3.txt','r',encoding='utf8')
data=file.read().split('**')
temp_data=[0]
num=1001
while(num < len(data)):
    a=data[num]
    print(num,'/',len(data)-1)
    print(a)
    choose='0'
    temp_data.append(choose)
    
    while(1):
        choose=input("1 자연재해 2 전염병 3 기타 4 이전으로\n : ")
        if choose == '1' or choose == '2' or choose == '3' or choose == '4' or choose == '5':
            break
    if choose == '4':
        num -= 1
        continue
    elif choose == '5':
        break
    temp = '##' + choose + '**' + a
    
    temp_data[num]=temp
    num += 1
text_data=''
for i in range(1,len(temp_data)):
    if temp_data[i] == '0':
        break
    text_data += temp_data[i]
print(temp_data)
file=open('output_train_text.txt','w',encoding='utf8')
file.write(text_data)
file.close()
