package jp.osakafu.imp.vocabulometer.nlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ModelProvider {
    private static final String POS_TAGGER_PATH = "src/main/resources/models/english-left3words-distsim.tagger";
    private static final String NER_MODEL_PATH = "src/main/resources/classifiers/english.all.3class.distsim.crf.ser.gz";
    private static final String STOPWORDS_PATH = "src/main/resources/stopwords.txt";
    private static final String PUNCTUATION_PATH = "src/main/resources/punctuation.txt";

    private static AbstractSequenceClassifier<CoreLabel> NER_CLASSIFIER;
    private static StanfordCoreNLP POS_PIPELINE;
    private static Set<String> STOPWORDS;
    private static Set<String> PUNCTUATION;

    public static AbstractSequenceClassifier<CoreLabel> getNerClassifier() {
        if (NER_CLASSIFIER == null) {
            try {
                NER_CLASSIFIER = CRFClassifier.getClassifier(NER_MODEL_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return NER_CLASSIFIER;
    }

    public static StanfordCoreNLP getPosPipeline() {
        if (POS_PIPELINE == null) {
            // Create StanfordCoreNLP object properties, with POS tagging
            // (required for lemmatization), and lemmatization
            Properties props;
            props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma");
            props.put("pos.model", POS_TAGGER_PATH);

            // StanfordCoreNLP loads a lot of models, so you probably
            // only want to do this once per execution
            POS_PIPELINE = new StanfordCoreNLP(props);
        }

        return POS_PIPELINE;
    }

    private static Set<String> getTokenSetFromFile(String path) {
        try {
            Set<String> wordSet = new HashSet<>();

            BufferedReader bf = new BufferedReader(new FileReader(path));

            String line = bf.readLine();
            while (line != null) {
                wordSet.add(line);
                line = bf.readLine();
            }

            return wordSet;
        } catch (FileNotFoundException err) {
            System.err.println("File path provided not existing");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Set<String> getStopWords() {
        if (STOPWORDS == null) {
            STOPWORDS = getTokenSetFromFile(STOPWORDS_PATH);
        }

        return STOPWORDS;
    }

    public static Set<String> getPunctuation() {
        if (PUNCTUATION == null) {
            PUNCTUATION = getTokenSetFromFile(PUNCTUATION_PATH);
        }

        return PUNCTUATION;
    }

    public static void forceInit() {
        getNerClassifier();
        getPosPipeline();
        getStopWords();
        getPunctuation();
    }
}
