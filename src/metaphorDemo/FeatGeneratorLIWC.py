#!/usr/bin/python
# -*- coding: utf-8 -*-

from metaphorDemo.interfaces import FeatGenerator

import pickle
import sys
import re
import os
import codecs
import heapq

class GenerateLIWCFeat():
    """#Program to take as input sentences and return the scores for various categories.
        #metaphors - array of metaphors
        #language - EN, RU, FA, ES"""
    def __init__(self):
        self.VALENCE_DIR = None
        self.lang = None
        self.liwcCategory = {}
        self.liwcCategoryReverse = {}
        self.liwcDicitonary = {}
    
    def setParams(self, params): #params[0] for language, params[1] for resource path
        self.lang = params[0]
        self.VALENCE_DIR = params[1]
    
    def loadResourec(self):
        dictionary_dir = self.VALENCE_DIR
        category_file = dictionary_dir + "liwcCategory_"+ self.lang +".pickle"
        categoryReverse_file = dictionary_dir + "liwcCategoryReverse_"+ self.lang +".pickle"
        dictionary_file = dictionary_dir + "liwcDicitonary_"+ self.lang +".pickle"
        
        self.liwcCategory = pickle.load(open(category_file,"r"))
        self.liwcCategoryReverse = pickle.load(open(categoryReverse_file,"r"))
        self.liwcDicitonary = pickle.load(open(dictionary_file,"r"))

    
    def generateFeat(metaphors):
        metaphors = [metaphors] #although input only one metaphor sentence, but convert to array for compatability
        # Set standard output encoding to UTF-8.
        sys.stdout = codecs.getwriter('UTF-8')(sys.stdout)
        #Open the preprocessed dictionary file
        #mariam	langIndFeatures = [ 'WC','WPS','Sixltr','Dic','Numerals' ]
        punctuations = ['Period','Comma','Colon','SemiC','QMark','Exclam','Dash','Quote','Apostro','Parenth','OtherP','AllPct']
        langDepFeatures = self.liwcCategoryReverse.keys()
        features = []
        features.extend(langIndFeatures)
        features.extend(langDepFeatures)
        features.extend(punctuations)
        features.append('Valence')
        header = '\t'.join(features)
        affectExplList = []
        output = []
        functional_domains = {'EN': {'funct', 'pronoun', 'ppron', 
            'conj', 'i', 'we', 'you', 'shehe', 'they', 
            'ipron', 'article', 'verb', 'auxverb', 'past', 
            'present','future', 'adverb', 'preps', 'relativ',
            'motion', 'space', 'time', 'incl', 'excl', 'cogmech'},
            'ES': {'Yo', 'Nosotros', 'Pronom', 'Otro', 'unomismo', 
            'Tu', 'Art\xc3\xadculo','Presente', 'Pasado', 
            'N\xc3\xbamero', 'Prepo', 'Incl'},
            'RU': {'Функция', 'Местоимение', 'Личноеместоимение',
            'я', 'мы', 'ты', 'онаон', 'они', 'неопределенныеместоимен',
            'вы', 'вербальные', 'сленг', 'Наречие', 'Предлог', 'Союз', 
            'Отрицание', 'quant', 'Числительное', 'ВульгаризмТабу',
            'Друг', 'Человечество'},
            'FA': {'funct', 'pronoun', 'ppron', 'conj', 'i', 'we',
            'you', 'shehe', 'they', 'ipron', 'article', 'verb',
            'auxverb', 'past', 'present','future', 'adverb', 
            'preps', 'relativ','motion', 'space', 'time', 
            'incl', 'excl', 'cogmech'}
        }
            
        for i,metaphor in enumerate(metaphors):
#       	print "Processing metaphor ", i+1
            line = metaphor.strip().lower()
            valence = '0'
            words = line.split()
            wordCount = float(len(words))
            dictCount = 0
            dictionaryWords = []
            #affect domains map to their words
            affectWordsDict = {}
                # print ""
                # For each word in sentence find the corresponding word in LIWC dictionary
            for word in words:

                if re.search(ur'[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\\s]+$',word,flags=re.UNICODE):
				
                    word = re.sub(ur'[.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+$','',word,flags=re.UNICODE)

                if re.search(ur'^[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+',word,flags=re.UNICODE):
				
                    word = re.sub(ur'^[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+','',word,flags=re.UNICODE)

                if len(word) ==0:
                    continue
                    #the first_letter should be fetched from unicode
                if lang == 'FA':
                    first_letter = word.decode('utf-8')[0].encode('utf-8')
                else:
                    first_letter = word[0]
                if first_letter in liwcDicitonary:
                    check_word = word
                    flag = False

                    while(len(check_word) > 0):
                        partial_word = check_word + "*"
                        if (not flag):
                            if partial_word in liwcDicitonary[first_letter]:
                                dictCount += 1
                                dictionaryWords.append(partial_word)
                                break
                            elif check_word in liwcDicitonary[first_letter]:
                                dictCount += 1
                                dictionaryWords.append(check_word)
                                break

                        if flag and partial_word in liwcDicitonary[first_letter]:
                                dictCount += 1
                                dictionaryWords.append(partial_word)
                                break
                        if lang == 'FA':
                            check_word = check_word.decode('utf-8')[:-1].encode('utf-8')
                        else:
                            check_word = check_word[:-1]
                        flag = True
		
                liwcParameterCount = {}
            # For each word found in dictionary find the parameter count
            for word in dictionaryWords:
                if lang == 'FA':
                    properties = liwcDicitonary[word.decode('utf-8')[0].encode('utf-8')][word]
                else:
                    properties = liwcDicitonary[word[0]][word]

                for prop in properties:
                    if liwcCategory[prop] in liwcParameterCount:
                        liwcParameterCount[liwcCategory[prop]] += 1
                        #new code
                        affectWordsDict[liwcCategory[prop]].add(word.strip('*'))
                    else:
                        liwcParameterCount[liwcCategory[prop]] =1
                        #new code
                        affectWordsDict[liwcCategory[prop]] = {word.strip('*')}

            allPunctCount = 0
            lineOutput = ""
            for parameter in features:
                if parameter == "Filename":
                    lineOutput += inputFileName + "\t"
                elif parameter == "Seg":
                    lineOutput += str(i+1) + "\t"
                elif parameter == "WC":
                    lineOutput += str(wordCount) + "\t"
				###print wordCount
                elif parameter == "WPS":
                    count = len(re.findall(r'\S+[.?!]\s+|.$',line))
                    lineOutput += str(wordCount/count) + "\t" # IS IT WRONG DIVISION???
#	lineOutput += str(count/wordCount) + "\t" 
				#print count
				##print lineOutput
                elif parameter == "Sixltr":
                    count = 0

                    for word in words:
                        word = word.decode('utf-8')
                        if re.search(ur'[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\\s]+$',word,flags=re.UNICODE):
                            word = re.sub(ur'[.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+$','',word,flags=re.UNICODE)

                        if re.search(ur'^[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+',word,flags=re.UNICODE):
                            word = re.sub(ur'^[\.\,\:\;\"\'\)\!\?\%\$\#\@\^\&\*\(\_\-\+\=\~\/\\]+','',word,flags=re.UNICODE)
                        if len(word) > 6:
                            count += 1
                    lineOutput += str(count/wordCount * 100) + "\t"

                elif parameter == "Dic":
                    lineOutput += str(len(dictionaryWords)/wordCount * 100) + "\t"

                elif parameter == "Numerals":
				# Need to calculate this
                    count = len(re.findall(r'\d+[\W\s]+',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
			
                elif parameter in liwcCategoryReverse:
                    if parameter in liwcParameterCount:
                        lineOutput += str(liwcParameterCount[parameter]/wordCount * 100) + "\t"
                    else:
                        lineOutput += '0' + "\t"

                elif parameter == "Period":
                    # count = len(re.findall(r'\S+\.\s+|\S+\.$',line))
                    count = len(re.findall(r'\.',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Comma":
                    # count = len(re.findall(r'\S+\,\s+|\S+\,$',line))
                    count = len(re.findall(r'\,',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Colon":
                    # count = len(re.findall(r'\S+\:\s+|\S+\:',line))
                    count = len(re.findall(r'\:',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count


                elif parameter == "SemiC":
                    # count = len(re.findall(r'\S+\;\s+|\S+\;$',line))
                    count = len(re.findall(r'\;',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count


                elif parameter == "QMark":
                        # count = len(re.findall(r'\S+\?\s+|\S+\?$',line))
                    count = len(re.findall(r'\?',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Exclam":
                    # count = len(re.findall(r'\S+\!\s+|\S+\!$',line))
                    count = len(re.findall(r'\!',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Dash":
                    count = len(re.findall(r'\-',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Quote":
                    # count = len(re.findall(r'\S+\"\s+|\s+\"\S+',line))
                    count = len(re.findall(r'\"',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Apostro":
                    # count = len(re.findall(r'\S+\'\S+',line))
                    count = len(re.findall(r'\'',line))
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "Parenth":
                    count = len(re.findall(r'[\(\)]',line))
                    count /= 2
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "OtherP":
                    otherP = re.findall(r'[~`@#$%^&*_+=|\\/><]',line)
                    count =0
                    for punct in otherP:
                        if len(punct) == 1:
                            count +=1
				
                    lineOutput += str(count/wordCount * 100) + "\t"
                    allPunctCount +=count

                elif parameter == "AllPct":
                    # Need to calculate this
		
                    lineOutput += str(allPunctCount/wordCount * 100) + "\t"

                elif parameter == "Valence":# and valence != "":
                    if valence!="":
                        lineOutput += valence + "\t"
                    else:
                        lineOutput += "0" + "\t"

		#output += lineOutput + "\n"
#               print lineOutput
            farr=lineOutput.strip().split('\t')
            output.append(farr[len(farr)-1] + " " + " ".join([ str(i+1)+":"+farr[i] for i in range(0,len(farr)-1) ]))
# print functional_domains[lang]
            """
            nlargests = heapq.nlargest(3,
                        [(key, liwcParameterCount[key]) for key in liwcParameterCount.keys()
                        if key not in functional_domains[lang]], key=lambda s: s[1])
            affectexpls = {}
            for top_item in nlargests:
                    affectexpls[top_item[0]] = affectWordsDict[top_item[0]]
                affectExplList.append(affectexpls)
            """
        return output
#print affectexpls
#print liwcParameterCount
    """
	#mariam
	featFileName = VALENCE_DIR + "/valence_pipeline/" + "%s.txt.feat.dat" %(re.findall(r'(\w+)\.',inputFileName)[0])
	featFile = open(featFileName,'w')
	if output != "":
		featFile.write(output)
        #if feat_output != "":
	#	featFile.write(feat_output)
        featFile.close()
        affectFileName = VALENCE_DIR + "/valence_pipeline/affectExplList.pickle"
        pickle.dump(affectExplList, open(affectFileName, 'w'))
    """
