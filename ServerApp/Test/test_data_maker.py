import sys
file=open('output.txt','r',encoding='utf8')
data=file.read().split('**')
temp_data=''
for i in range(1,len(data)):
    a=data[i]
    print(a)
    choose=0
    while(1):
        choose=input("1 자연재해 2 전염병\n : ")
        if choose == '1' or choose == '2':
            break
    temp = '##' + choose + '**' + a
    temp_data += temp
    
file=open('output_train.txt','w',encoding='utf8')
file.write(temp_data)
file.close()
