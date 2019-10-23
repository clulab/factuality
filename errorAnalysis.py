#!/usr/bin/python3  
#author: Fan Luo

import numpy as np
import string 
import math
import sys
import argparse

def create_parser():
    parser = argparse.ArgumentParser(description='Arguments')
    parser.add_argument('--predictionFileModal', type=str, help="")
    parser.add_argument('--predictionFileNoModal', type=str, help="")
    parser.add_argument('--outputFile', type=str, help="")

    return parser

def parse_commandline_args():
    return create_parser().parse_args()


# take the 'error' field for sort
def takeError(d):
    return d['error']


def main():
    if len(sys.argv) != 7:
        print("usage: python errorAnalysis.py --predictionFileModal [INPUT_FILE] --predictionFileNoModal [INPUT_FILE] --outputFile [OUTPUT_FILE]")
    else:
        args = parse_commandline_args()

        # Compare between before and after removing modal
        modals = ["can", "could", "may", "might", "should", "would"]

        # this is the dicts for before removing modal, each dict corresponding to one instance/line; 
        # the dicts also used for printing, so combined info from after removing modal
        keys = ['modalPredication', 'label', 'predicatePosition', 'sentence', 'sentWithoutModal', 'modalPositions', 'NoModalPredication', 'error', 'NoModalError']
        values = []
        dicts = []

        # this is for after removing modal
        NoModalKeys = ['NoModalPredication','label','NoModalPredicatePosition','sentWithoutModal']
        NoModalvalues = []
        NoModalDicts = []

        with open(args.predictionFileModal) as f1:
            with open(args.predictionFileNoModal) as f2:

                # store info of after removing modal into NoModalDicts
                for line in f2:
                    line = line.strip().split()
                    if(len(line) > 3):
                        NoModalvalues = line[:3]
                        NoModalvalues.append(" ".join(line[3:]))  # sentence without modal
                        d = dict(zip(NoModalKeys, NoModalvalues))
                        NoModalDicts.append(d)

                # store info of before removing modal, also combine info from after removing modal for printing
                for line in f1:
                    # Split each line.
                    line = line.strip().split()
                    if(len(line) > 3):
                        values = line[:3]
                        values.append(" ".join(line[3:]))  # sentence

                        sentWithoutModal = []
                        modalPositions=[]
                        for i, w in enumerate(line[3:]):
                            if(w.lower() not in modals):  # if the word is not a modal, append to sentWithoutModal
                                sentWithoutModal.append(w)
                            else:   # a word is a modal, record its position, to be marked when printing
                                modalPositions.append(i)
                        values.append(sentWithoutModal)
                        values.append(modalPositions)

                        NoModalPredication = ''
                        if (len(modalPositions) == 0): # NoModalPredication will be same as ModalPredication, since there is no modal, sentence are same
                            NoModalPredication = line[0]

                            # This is for debug, if no modal in the sentence, the prediction of before and after should be same
                            # for NoModalDict in NoModalDicts:
                            #     if (" ".join(line[3:]) in list(NoModalDict.values()) and NoModalDict['label']==line[1] and line[2]==NoModalDict['NoModalPredicatePosition']):
                            #         print("match")
                            #         if(NoModalPredication != NoModalDict['NoModalPredication']):
                            #             print("error")
                            #             print(line)
                            #             print(NoModalPredication)
                            #             print(NoModalDict['NoModalPredication'])

                        else:   # at least one modal exists
                            for NoModalDict in NoModalDicts:
                                if (" ".join(sentWithoutModal) in list(NoModalDict.values()) and NoModalDict['label']==line[1]):
                                    # print(NoModalDict['NoModalPredicatePosition'])
                                    # print(line[2])
                                    # print(len(modalPositions))
                                    if(int(NoModalDict['NoModalPredicatePosition']) <= int(line[2]) and int(NoModalDict['NoModalPredicatePosition']) >= int(line[2]) - len(modalPositions)):   # line[2]) is predicate position. if modal is after predicate, its position is not afffected though
                                        NoModalPredication = NoModalDict['NoModalPredication']
                                        
                            if(NoModalPredication == ''):
                                print("Did not found a corresponding sentence after removing modal")
                                # print(line)
                                # print(sentWithoutModal)

                        values.append(NoModalPredication)
                        values.append(abs(float(line[0]) - float(line[1])))  # abs error: diff between prediction and label
                        values.append(abs(float(NoModalPredication) - float(line[1])))  # abs NoModalError

                        # Create dict for each row.
                        d = dict(zip(keys, values))
                        dicts.append(d)

                #sort dicts according to error before removing modal
                sortedDicts = sorted(dicts, reverse=True, key=takeError)


        # print output
        with open(args.outputFile, 'w') as output:
            output.write("label\twithModalPrediction\tNoModalPrediction\twithModalDiff\tNoModalDiff\twithout modal\tsentence\n")


            for sortedDict in sortedDicts:
                output.write(sortedDict['label'])
                output.write('\t')
                output.write(sortedDict['modalPredication'])
                output.write('\t')
                output.write(sortedDict['NoModalPredication'])
                output.write('\t')
                output.write(str(sortedDict['error']))
                output.write('\t')
                output.write(str(sortedDict['NoModalError']))
                output.write('\t')
                if (len(sortedDict['modalPositions']) == 0):
                    output.write('-\t')
                elif(float(sortedDict['NoModalError'])+0.001 < float(sortedDict['error'])):
                    output.write('better\t')
                elif(float(sortedDict['NoModalError'])-0.001 > float(sortedDict['error'])):
                    output.write('worse\t')
                else:
                    output.write('same\t')

                for i, w in enumerate(sortedDict['sentence'].split()):
                    if(i == sortedDict['predicatePosition']):
                        output.write('**' + w + '**\t')  # wrap the word by '**' to denote this is the predicate
                    elif(i in sortedDict['modalPositions']):
                        output.write('--' + w + '--\t')   # wrap the word by '--' to denote this is the removed modal
                    else:
                        output.write( w + '\t')


                output.write('\n')


if __name__ == '__main__':
    main()
