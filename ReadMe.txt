Names: Daniela Delgado and John Bermudez
File names need to run: SentAnalysis.java; SentAnalysisBest.java; test; train
Group honor code: We have neither given nor received unauthorized aid on this assignment.
Known bugs: There are no known bugs in our assignment. 
Part 2 Reflection Question: 
    - Three examples in which our classifier failed was with the sentences "The movie was mediocre at best", "The movie was not enjoyable", and
    "The book did not have a wow factor." For each example above, the classifier counts the number of positive and negative words in the sentence 
    according to the ppositive and negative dictionaries. Since the number of positive words equals the number of negative words in the sentence, the 
    classifier says the sentence is positive whereas if one is greater than the other, it would classify it as the majority. There was nothing difficult
    about the target text. It might just be a problem with computation of negative & positive words. There also might not be enough training data to detect
    words that are used less often.
Part 3 Reflection Question: 
    - For our SentAnalysisBest java program, we decided to implement the bigram feature. In terms of accuracy and everything, our SentAnalysisBest is better than the base
    classifier (but only a marginal amount). The base classifier had an accuracy of 76% and our modified classifier had an accuracy of 77%. We think that the bare marginal 
    increase in accuracy is due to not taking into account other possibilities (n-grams, double negatives, etc.). If we were to add more features, we would see bigger increase
    in accuracy. I think another thing that would be helpful to our accuracy is by considering mispellings in the reviews. In terms of precision, our modified SentAnalysis has a
    better precision rate than the base classifier (by 3%). However, the negative precision is a bit lower (about 1%) with our modified SentAnalysis compared to the base one. We 
    think this might be due to not accurately predicting things like double negative and mispellings.