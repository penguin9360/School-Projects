/**
 * @file cartalk_puzzle.cpp
 * Holds the function which solves a CarTalk puzzler.
 *
 * @author Matt Joras
 * @date Winter 2013
 */

#include <fstream>

#include "cartalk_puzzle.h"

using namespace std;

/**
 * Solves the CarTalk puzzler described here:
 * http://www.cartalk.com/content/wordplay-anyone.
 * @return A vector of (string, string, string) tuples
 * Returns an empty vector if no solutions are found.
 * @param d The PronounceDict to be used to solve the puzzle.
 * @param word_list_fname The filename of the word list to be used.
 */
vector<std::tuple<std::string, std::string, std::string>> cartalk_puzzle(PronounceDict d,
                                    const string& word_list_fname)
{
    vector<std::tuple<std::string, std::string, std::string>> gg;

    /* Your code goes here! */
    vector<string> wordlist;
    ifstream wordsFile(word_list_fname);
    string word;
    if (wordsFile.is_open()) {
    	while (getline(wordsFile, word)) {
    		if (word.length() == 5) wordlist.push_back(word);
    	}

    }
    for (size_t i = 0; i < wordlist.size(); i++) {
    	string st1 = wordlist[i];
    	string st2 = st1.substr(1);

    	string st3;
    	st3.append(st1.begin(),st1.begin() + 1);
    	st3.append(st2.begin() + 1,st2.end());

    	if (d.homophones(st1,st2) && d.homophones(st1,st3) && d.homophones(st2,st3)) {
    		tuple<string,string,string> t(st1,st2,st3);
    		gg.push_back(t);
    	}

    }

    return gg;
}
