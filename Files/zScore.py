#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Feb 25 14:24:33 2018

@author: krishna
"""
import copy
f = open("data2.csv","r")
Mf = 0
Sf = 0
dataMatrix = []

for line in f:
    temp = line.split(' ')
    dataMatrix.append(temp)
    
zScoreMatrix = copy.deepcopy(dataMatrix)
for indC in range(len(dataMatrix[0])-1):
    SUM = 0
    for indR in range(1,len(dataMatrix)):
        SUM += float(dataMatrix[indR][indC])
    Mf = float(SUM/(len(dataMatrix)-1))
    SUM = 0
    for indR in range(1,len(dataMatrix)):
        SUM+=abs(float(dataMatrix[indR][indC])-Mf)
    Sf = float(SUM/(len(dataMatrix)-1))
    for indR in range(1,len(dataMatrix)):
        zScoreMatrix[indR][indC] = str(float((float(dataMatrix[indR][indC])-float(Mf))/float(Sf)))
        
f1 = open("zData.csv","w")
for x in zScoreMatrix:
    temp1 = " ".join(x)
    f1.write(temp1)
f.close()
f1.close()
        
    