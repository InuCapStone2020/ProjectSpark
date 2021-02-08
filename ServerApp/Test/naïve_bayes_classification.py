import math 
from selenium import webdriver
import time
from tqdm.notebook import tqdm
from konlpy.tag import Komoran
import re
positiveword=[]
negativeword=[]
komoran = Komoran()
class NaiveBayesClassifier:
    def __init__(self, k=0.5): #클래스 초기화  
        self.k = k
        self.word_probs = []

    def load_data(self):
        docs = []
        labels = []
        file=open('output_train.txt','r',encoding='utf8')
        data = file.read().split('##')
        for i in range(1,len(data)):
            temp_data = data[i]
            doc = temp_data.split(']')[1]
            label = temp_data.split('[')[0]
            
            docs.append(doc)
            if label == '1': label = '자연재해'
            elif label == '2': label = '전염병'
            labels.append(label)
        
        return docs, labels
        #output_train 파일로 지도학습할 데이터를 가져옴
    
    def tokenize(self, sentence):
        stopword_list=['[',']','(',')','.','~','0','1','2','3','4','5','6','7','8','9','0',
            '▶','▲','→','%','-']
        whiteword_list=['눈','설']
        for i in stopword_list:
            sentence=sentence.replace(i,' ')
            
            
        isHangul = len(re.findall(u'[\u3130-\u318F\uAC00-\uD7A3]+',sentence))
        if isHangul == 0:
            word_list = sentence.split()
            return word_list
            
            
        temp_word_list = komoran.nouns(sentence)
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
        
        #데이터 전처리 작업
        #komoran 한국어 형태소 분석기를 활용하여 stopword_list에 있는 것들을 빼고 분석
        #그 후에 한 글자짜리를 제거. whiteword_list에 있는 한 글짜 단어는 제거 안함
        #한글이 없는 것은 형태소 분석 안하고 띄어쓰기로 분석
        return word_list

    def count_words(self, docs, labels):
    
        count_dict = dict()
        for doc, label in zip(docs, labels):
            for word in self.tokenize(doc):
                if word not in count_dict:
                    count_dict[word] = {'자연재해': 0, '전염병': 0} # count_dict={'word1':{'pos':10, 'neg':5}, ...}
                count_dict[word][label] += 1
        
        # 단어 사전(dictionary)를 만들고, 각 단어의 긍정/부정 문서 등장 횟수 세기
        print('num of words...', len(count_dict))
        #사전에 있는 단어의 개수 출력 - 확인용
        return count_dict

    def word_prob(self, count_dict, pos_class_num, neg_class_num, k):
        # (단어, p(단어|긍정), p(단어|부정))의 튜플 형태로 만들어주어 리스트에 추가

        word_prob_list = []
        
        for key in count_dict:
            pos_word_num = count_dict[key]['자연재해']
            neg_word_num = count_dict[key]['전염병']
    
            pos_class_prob = (pos_word_num + k) / (pos_class_num + 2*k)
            neg_class_prob = (neg_word_num + k) / (neg_class_num + 2*k)
            #단어가 다 새로 나온다면 전체 확률이 0이 됨
            #분모와 분자에 일정한 값을 더해 분자가 0이 되는 것을 방지
            #pos_class_num 만큼을 보았고 2k만큼을 더 본다면 k번 등장할 수도 있다
            
            
            tup = (key, pos_class_prob, neg_class_prob)
            word_prob_list.append(tup)
            
        return word_prob_list

    def class_prob(self, word_prob_list, test_sentence, use_unseen=False):
        # p(긍정|문서), p(부정|문서) 계산
        
        test_words = self.tokenize(test_sentence)
        
        sent_log_pos_class_prob, sent_log_neg_class_prob = 0.0, 0.0
        
        for word, word_pos_class_prob, word_neg_class_prob in word_prob_list:
            if word in test_words:
                sent_log_pos_class_prob = sent_log_pos_class_prob + math.log(word_pos_class_prob) #언더플로우 방지
                sent_log_neg_class_prob = sent_log_neg_class_prob + math.log(word_neg_class_prob)
            else:
                if use_unseen:
                    sent_log_pos_class_prob = sent_log_pos_class_prob + math.log(1.0-word_pos_class_prob) #언더플로우 방지
                    sent_log_neg_class_prob = sent_log_neg_class_prob + math.log(1.0-word_neg_class_prob)                
            
        sent_pos_class_prob = math.exp(sent_log_pos_class_prob)
        sent_neg_class_prob = math.exp(sent_log_neg_class_prob)
        
        pos_class_prob = sent_pos_class_prob/(sent_pos_class_prob+sent_neg_class_prob)
        neg_class_prob = sent_neg_class_prob/(sent_pos_class_prob+sent_neg_class_prob)
        
        return pos_class_prob, neg_class_prob
    
        # 베이즈 정리 계산
    def train(self):
        # load_data, count_words, word_prob 계산
        
        train_docs, train_labels = self.load_data()
        
        word_count_dict = self.count_words(train_docs, train_labels)
        
        pos_class_num = len([label for label in train_labels if label == '자연재해'])
        neg_class_num = len([label for label in train_labels if label == '전염병'])
        
        self.word_probs = self.word_prob(word_count_dict, pos_class_num, neg_class_num, self.k)
        for i in range(len(self.word_probs)):
            if self.word_probs[i][1] >= self.word_probs[i][2]:
                positiveword.append(self.word_probs[i][0])
            else:
                negativeword.append(self.word_probs[i][0])
                
        
        #word.probs에서 긍정 확률이 부정의 확률보다 많다면 긍정 데이터에 저장
        
    def classify(self, doc, use_unseen=False):
        # class_prob 계산
        
        pos_class_prob, neg_class_prob = self.class_prob(self.word_probs, doc, use_unseen)
        
        if pos_class_prob > neg_class_prob:
            print('자연재해', pos_class_prob * 100, '%')
        else:
            print('전염병', neg_class_prob * 100 , '%')


classifier = NaiveBayesClassifier() # use_morph=False
classifier.train()
use_unseen = False
classifier.classify('social distance 2.5 mask.', use_unseen)
classifier.classify('2.2.(화)~2.4.(목) 09:00~19:30 신앙촌상회 초량2호점(동구 중앙대로221번길 11)을 방문한 분은 인근 보건소에 상담 바랍니다.', use_unseen)

