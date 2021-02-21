import math 
from selenium import webdriver
import time
from tqdm.notebook import tqdm
from konlpy.tag import Komoran
import re

#-----------------------------------------------------------------------
positiveword=[]
negativeword=[]
etcword=[]
#테스트용 리스트
#-----------------------------------------------------------------------

komoran = Komoran()
class NaiveBayesClassifier:
    def __init__(self, k=0.5):
        self.k = k
        self.word_probs = []
        #클래스 초기화
        
    def load_data(self):
        docs = []
        labels = []
        file=open('output_train_all.txt','r',encoding='utf8')
        data = file.read().split('##')
        for i in range(1,len(data)):
            temp_data = data[i]
            doc = temp_data.split(']')[1]
            label = temp_data.split('**')[0]
            
            docs.append(doc)
            if label == '1': label = '자연재해'
            elif label == '2': label = '전염병'
            elif label == '3': label = '기타'
            labels.append(label)
        
        return docs, labels
        #output_train 파일에서 지도학습할 데이터를 가져와 docs와 labels로 구분
    
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
            
        temp_word_list = komoran.nouns(sentence)
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
        
        
        #-----------------------------------------------------------------------
        print('num of words...', len(count_dict))
        #사전에 있는 단어의 개수 출력 - 테스트용
        #-----------------------------------------------------------------------
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
    
        # 베이즈 정리 계산
    def train(self):
        
        train_docs, train_labels = self.load_data()
        #데이터를 가져온 후 저장
        
        word_count_dict = self.count_words(train_docs, train_labels)
        #등장 횟수를 세어 dictionary형태로 저장
        
        pos_class_num = len([label for label in train_labels if label == '자연재해'])
        neg_class_num = len([label for label in train_labels if label == '전염병'])
        etc_class_num = len([label for label in train_labels if label == '기타'])
        #각 label별 등장 횟수 저장
        
        self.word_probs = self.word_prob(word_count_dict, pos_class_num, neg_class_num, etc_class_num, self.k)
        #각 단어가 각 label일 확률을 계산하여 저장
        
        
        #-----------------------------------------------------------------------
        for i in range(len(self.word_probs)):
            if self.word_probs[i][1] >= self.word_probs[i][2] and self.word_probs[i][1] >= self.word_probs[i][3]:
                positiveword.append(self.word_probs[i][0])
            elif self.word_probs[i][2] > self.word_probs[i][1] and self.word_probs[i][2] > self.word_probs[i][3]:
                negativeword.append(self.word_probs[i][0])
            else:
                etcword.append(self.word_probs[i][0])
        #각 label별로 어떤 데이터가 많은지 알아보기 위한 테스트
        #-----------------------------------------------------------------------
        
    def classify(self, doc):
        pos_class_prob, neg_class_prob, etc_class_prob = self.class_prob(self.word_probs, doc)
        #해당 문장에 있는 단어를 베이즈 정리를 이용하여 문장이 각 label에 속해있을 확률을 계산
        
        if pos_class_prob >= neg_class_prob and pos_class_prob >= etc_class_prob:
            print('자연재해', pos_class_prob * 100, '%')
        elif neg_class_prob > pos_class_prob and neg_class_prob > etc_class_prob:
            print('전염병', neg_class_prob * 100 , '%')
        else:
            print('기타', etc_class_prob * 100 , '%')
        #해당 결과를 퍼센트 형태로 출력


classifier = NaiveBayesClassifier() # use_morph=False
classifier.train()
classifier.word_probs
