import math 
from selenium import webdriver
from tqdm.notebook import tqdm
from konlpy.tag import Komoran
import re


class NaiveBayesClassifier:
    def __init__(self, c, k=0.5):
        self.k = k 
        self.word_probs = []
        self.komoran = Komoran()
        self.clear = c
        
        #k 라플라스 스무딩을 위한 변수
        #word_probs 단어에 대한 확률을 저장 
        #komoran 사용하는 한국어 형태소 분석기
        #clear 데이터를 새로 저장할건지 구분하는 제어 변수
        
        try:
            file=open('word_prob_list.txt','r+', encoding='utf8')
            while(1):
                line = file.readline()
                if not line:
                    break
                temp = line.split('/')
                temp[3] = temp[3].replace('\n','')
                for i in range(1,len(temp)):
                    if temp[i] == '0.0':
                        temp[i] = 1.e-10
                    else:
                        temp[i] = float(temp[i])
                key, pos, neg, etc = temp
                self.word_probs.append((key,pos,neg,etc))
            file.close()
            
            #단어별 확률을 저장한 파일을 불러와 word_prob에 저장
        except:
            print("file open error(word_prob_list.txt)")
            temp = []
            docs, labels = self.load_initial_data()
            for i in range(len(docs)):
                temp.append({'CONTENT':(']'+docs[i]),'EVENT':labels[i]})
            self.train(temp)
            
            #파일 불러오기가 실패한다면 분류 완료된 파일을 불러와 word_prob에 저장
        
    def load_initial_data(self):
        docs = []
        labels = []
        file=open('output_train_all.txt','r',encoding='utf8')
        data = file.read().split('##')
        for i in range(1,len(data)):
            temp_data = data[i]
            temp_doc = temp_data.split(']')
            doc=''
            for j in range(1,len(temp_doc)):
                doc += temp_doc[j]
            label = temp_data.split('**')[0]
            docs.append(doc)
            if label == '1': label = '자연재해'
            elif label == '2': label = '전염병'
            elif label == '3': label = '기타'
            labels.append(label)
        print("initial data added")
        return docs, labels
        
        #분류 완료된 파일을 불러와 docs, labels에 저장
    
    def load_data(self, data_list):
        docs = []
        labels = []
        
        if self.clear == True:
            docs, labels = self.load_initial_data()
            
        for i in data_list:
            if i['EVENT'] == '미분류':
                continue
            
            temp_data = i['CONTENT']
            temp_doc = temp_data.split(']')
            doc=''
            for j in range(1,len(temp_doc)):
                doc += temp_doc[j]
            docs.append(doc)
            labels.append(i['EVENT'])
            
        return docs, labels
        #입력된 데이터를 docs, labels에 저장
    
    def tokenize(self, sentence):
        stopword_list=['[',']','(',')','.','~','0','1','2','3','4','5','6','7','8','9','0',
            '▶','▲','→','%','-',':','/','\n']
        whiteword_list=['눈','설']
        for i in stopword_list:
            sentence=sentence.replace(i,' ')
            
        #문장을 전처리해주는 작업
        #한국어 형태소 분석기를 더욱 잘 이용하기 위해 특수문자를 제거
        #한 글자도 제거할 예정이기 때문에 제거하지 않을 한 글자를 지정
            
        isHangul = len(re.findall(u'[\u3130-\u318F\uAC00-\uD7A3]+',sentence))
        if isHangul == 0:
            word_list = sentence.split()
            return word_list
        #만약 문장이 한글이 아니라면 띄어쓰기 형태로 구분
            
        temp_word_list = self.komoran.nouns(sentence)
        #형태소 분석을 하여 명사만 남김
        
        word_list=[]
        for j in temp_word_list:
            if len(j) != 1:
                word_list.append(j)
                continue
            else:
                for k in whiteword_list:
                    if j == k:
                        word_list.append(j)
                        break
        #한 글자를 제거
        #whiteword_list에 있는 글자는 제거 안함
        return word_list

    def count_words(self, docs, labels):
    
        count_dict = dict()
        for doc, label in zip(docs, labels):
            for word in self.tokenize(doc):
                if word not in count_dict:
                    count_dict[word] = {'자연재해': 0, '전염병': 0, '기타' : 0} 
                count_dict[word][label] += 1
        
        
        # 단어 dictionary를 만들고, 각 단어의 label별로 문서 등장 횟수 세기
        # 처음 나온 거라면 count_dict={'word':{'1':10, '2':5 '3':1}, ...}의 현태로 저장
        # 아니라면 label 등장횟수 +1
        
        return count_dict

    def word_prob(self, count_dict, pos_class_num, neg_class_num,etc_class_num, k):
        word_prob_list = []
        
        for key in count_dict:
            pos_word_num = count_dict[key]['자연재해']
            neg_word_num = count_dict[key]['전염병']
            etc_word_num = count_dict[key]['기타']
    
            pos_class_prob = (pos_word_num + k) / (pos_class_num + 2*k)
            neg_class_prob = (neg_word_num + k) / (neg_class_num + 2*k)
            etc_class_prob = (etc_word_num + k/1000) / (etc_class_num + 2*k)
            
            #단어가 다 새로 나온다면 전체 확률이 0이 됨
            #분모와 분자에 일정한 값을 더해 분자가 0이 되는 것을 방지
            #예를 들어 pos_class_num 만큼을 보았고 2k만큼을 더 본다면 k번 등장할 수도 있다
            
            #그 단어가 각 라벨별 몇번 나왔는지 횟수 / 각 라벨이 나온 전체 횟수를 계산
            #단어가 각 label일 확률을 계산하는 것
            
            tup = (key, pos_class_prob, neg_class_prob, etc_class_prob)
            word_prob_list.append(tup)
            
            #각 계산 결과를 튜플 형태로 묶어 리턴
            
        file=open('word_prob_list.txt','w', encoding='utf8')
        if len(word_prob_list) != 0:
            for i in word_prob_list:
                line = i[0] + '/' + str(i[1]) + '/' + str(i[2]) + '/' + str(i[3]) + '\n'
                file.write(line)
        file.close()
        #단어별 확률을 저장한 파일을 생성
        
        return word_prob_list

    def class_prob(self, word_prob_list, test_sentence):
        # p(긍정|문서), p(부정|문서) 계산
        
        test_words = self.tokenize(test_sentence)
        
        sent_log_pos_class_prob, sent_log_neg_class_prob, sent_log_etc_class_prob = 0.0, 0.0, 0.0
        
        for word, word_pos_class_prob, word_neg_class_prob, word_etc_class_prob in word_prob_list:
            if word in test_words:
                sent_log_pos_class_prob = sent_log_pos_class_prob + math.log(word_pos_class_prob) 
                sent_log_neg_class_prob = sent_log_neg_class_prob + math.log(word_neg_class_prob)
                sent_log_etc_class_prob = sent_log_etc_class_prob + math.log(word_etc_class_prob)
        
        #문장에 있는 각 단어별로 확률을 더하는 계산
        #언더플로우 방지를 위해 로그를 취하여 더함(로그를 취한 확률의 곱셈이 됨)
        
        sent_pos_class_prob = math.exp(sent_log_pos_class_prob)
        sent_neg_class_prob = math.exp(sent_log_neg_class_prob)
        sent_etc_class_prob = math.exp(sent_log_etc_class_prob)
        
        #로그 취한걸 해제하기 위해 exp를 사용
        pos_class_prob = sent_pos_class_prob/(sent_pos_class_prob+sent_neg_class_prob+sent_etc_class_prob)
        neg_class_prob = sent_neg_class_prob/(sent_pos_class_prob+sent_neg_class_prob+sent_etc_class_prob)
        etc_class_prob = sent_etc_class_prob/(sent_pos_class_prob+sent_neg_class_prob+sent_etc_class_prob)
        
        #전체 확률 분에 각 label별 확률을 계산
        
        return pos_class_prob, neg_class_prob, etc_class_prob
        
    def load_file_data(self):
        docs = []
        labels = []
        try:
            file=open('trained_data.txt','r',encoding='utf8')
            line=file.read()
            line = line.split('***')
            line.pop()
            #마지막 데이터 삭제
            
            for i in line:
                label, doc = i.split('###')
                if label == '미분류':
                    continue
                
                docs.append(doc)
                labels.append(label)
        except:
            print('file open error(trained_data.txt)')
        return docs, labels
        #파일에서 데이터를 불러와 label, doc에 저장
    
    def write_file_data(self, docs, labels):
        mode = 'w'
        file=open('trained_data.txt',mode, encoding='utf8')
        for i in range(len(docs)):
            line = labels[i] + '###' + docs[i] + '***'
            file.write(line)
        file.close()
        
        #파일에 데이터를 저장
        
    def train(self, data_list):
        
        train_docs, train_labels = self.load_data(data_list)
        #데이터를 가져온 후 저장
        if len(train_docs) != 0:
            if self.clear == False:
                docs, labels = self.load_file_data()
                for i in range(len(docs)):
                    train_docs.append(docs[i])
                    train_labels.append(labels[i])
            
            word_count_dict = self.count_words(train_docs, train_labels)
            #등장 횟수를 세어 dictionary형태로 저장

            pos_class_num = len([label for label in train_labels if label == '자연재해'])
            neg_class_num = len([label for label in train_labels if label == '전염병'])
            etc_class_num = len([label for label in train_labels if label == '기타'])
            #각 label별 등장 횟수 저장

            self.word_probs = self.word_prob(word_count_dict, pos_class_num, neg_class_num, etc_class_num, self.k)
            #각 단어가 각 label일 확률을 계산하여 저장

            self.write_file_data(train_docs, train_labels)
        
    def classify(self, doc):     
        pos_class_prob, neg_class_prob, etc_class_prob = self.class_prob(self.word_probs, doc)
        #해당 문장에 있는 단어를 베이즈 정리를 이용하여 문장이 각 label에 속해있을 확률을 계산
        result=''
        max_prob = max(pos_class_prob, neg_class_prob, etc_class_prob)
        
        if pos_class_prob == max_prob:
            result = ('자연재해/'+str(pos_class_prob * 100)+'%')
        elif neg_class_prob == max_prob:
            result = ('전염병/'+str(neg_class_prob * 100)+'%')
        else:
            result = ('기타/'+str(etc_class_prob * 100)+'%')
            
        if max_prob < 0.5:
            result = ('미분류/'+str(max_prob * 100)+'%')
        
        return result
