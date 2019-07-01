/**
 * @file anagram_dict.cpp
 * Implementation of the AnagramDict class.
 *
 * @author Matt Joras
 * @date Winter 2013
 */

#include "anagram_dict.h"

#include <algorithm>
#include <fstream>

using std::map;
using std::string;
using std::vector;
using std::ifstream;

/**
 * Constructs an AnagramDict from a filename with newline-separated
 * words.
 * @param filename The name of the word list file.
 */
AnagramDict::AnagramDict(const string& filename)
{
    /* Your code goes here! */
    vector< string > wordlist;
	ifstream words(filename);
	string word;
	if(words.is_open())
	{
   		while(getline(words, word)) {
        	wordlist.push_back(word);
        }
	}

	map<string,vector<string>> m;

  	for (size_t i = 0; i < wordlist.size(); i++) {
		    vector<string> st(1,wordlist[i]);
		    dict.insert(std::pair<string,vector<string>>(wordlist[i],st));
		    string good_string = wordlist[i];
		    sort(good_string.begin(),good_string.end());
		    auto gg = m.find(good_string);
		    if (gg == m.end()) {
			    vector<string> vec(1,wordlist[i]);
			    m[good_string] = vec;
		    } else {
			    gg->second.push_back(wordlist[i]);
		    }
	}

	for (auto & aaf:dict) {
		string good_string = aaf.first;
		sort(good_string.begin(),good_string.end());
		auto find = m.find(good_string);
		aaf.second = find->second;
	}
}

/**
 * Constructs an AnagramDict from a vector of words.
 * @param words The vector of strings to be used as source words.
 */
AnagramDict::AnagramDict(const vector<string>& words)
{
    /* Your code goes here! */
    map<string,vector<string>> m;

  	for (size_t i = 0; i < words.size(); i++) {
		vector<string> tp(1,words[i]);
		dict.insert(std::pair<string,vector<string>>(words[i],tp));
		string good_string = words[i];
		sort(good_string.begin(),good_string.end());
		auto lookup = m.find(good_string);
		if (lookup == m.end()) {
			vector<string> vec(1,words[i]);
			m[good_string] = vec;
		} else {
			lookup->second.push_back(words[i]);
		}
	}

	for (auto & aaf:dict) {
		string good_string = aaf.first;
		sort(good_string.begin(),good_string.end());
		auto find = m.find(good_string);
		aaf.second = find->second;
	}
}

/**
 * @param word The word to find anagrams of.
 * @return A vector of strings of anagrams of the given word. Empty
 * vector returned if no anagrams are found or the word is not in the
 * word list.
 */
vector<string> AnagramDict::get_anagrams(const string& word) const
{
    /* Your code goes here! */
    auto lookup = dict.find(word);
    if (lookup == dict.end()) return vector<string>();
    return lookup->second;
}

/**
 * @return A vector of vectors of strings. Each inner vector contains
 * the "anagram siblings", i.e. words that are anagrams of one another.
 * NOTE: It is impossible to have one of these vectors have less than
 * two elements, i.e. words with no anagrams are ommitted.
 */
vector<vector<string>> AnagramDict::get_all_anagrams() const
{
    /* Your code goes here! */
    vector<vector<string>> ret;
  	for (auto & aaf : dict) {
  		if (aaf.second.size() > 1) {
  			ret.push_back(aaf.second);
  		}
  	}
      return ret;
}
