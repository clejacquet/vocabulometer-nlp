package jp.osakafu.imp.vocabulometer.nlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModelProvider {
    private static final String POS_TAGGER_PATH = "src/main/resources/models/english-left3words-distsim.tagger";
    private static final String NER_MODEL_PATH = "src/main/resources/classifiers/english.all.3class.distsim.crf.ser.gz";
    private static final String STOPWORDS_PATH = "src/main/resources/stopwords.txt";
    private static final String PUNCTUATION_PATH = "src/main/resources/punctuation.txt";
    private static final String VOCAB_PATH = "src/main/resources/merged.txt";

    private static AbstractSequenceClassifier<CoreLabel> NER_CLASSIFIER;
    private static StanfordCoreNLP POS_PIPELINE;
    private static Set<String> STOPWORDS;
    private static Set<String> PUNCTUATION;
    private static Vocab VOCAB;

    public static AbstractSequenceClassifier<CoreLabel> getNerClassifier() {
        if (NER_CLASSIFIER == null) {
            try {
                NER_CLASSIFIER = CRFClassifier.getClassifier(NER_MODEL_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                return NER_CLASSIFIER;
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

    private static <T extends Collection<String>> T getTokenCollectionFromFile(String path, Supplier<T> collectionSupplier) {
        try {
            T wordSet = collectionSupplier.get();

            BufferedReader bf = new BufferedReader(new FileReader(path));

            String line = bf.readLine();
            while (line != null) {
                wordSet.add(line);
                line = bf.readLine();
            }

            return wordSet;
        } catch (FileNotFoundException err) {
            System.err.println("File path provided not existing");
            return  collectionSupplier.get();
        } catch (IOException e) {
            e.printStackTrace();
            return  collectionSupplier.get();
        }
    }

    public static Set<String> getStopWords() {
        if (STOPWORDS == null) {
            STOPWORDS = getTokenCollectionFromFile(STOPWORDS_PATH, HashSet::new);
        }

        return STOPWORDS;
    }

    public static Vocab getVocab() {
        if (VOCAB == null) {
            VOCAB = new Vocab();
            HashMap<String, Integer> levels = new HashMap<>();
            Set<String> all = new HashSet<>();

            try {
                BufferedReader bf = new BufferedReader(new FileReader(VOCAB_PATH));
                int currentLevel = 1;

                String line = bf.readLine();
                while (line != null) {
                    if (line.equals("")) {
                        currentLevel++;
                    } else {
                        levels.put(line, currentLevel);
                        all.add(line);
                    }

                    line = bf.readLine();
                }

                VOCAB.all = all;
                VOCAB.levels = levels;

            } catch (FileNotFoundException err) {
                System.err.println("File path provided not existing");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return VOCAB;
    }

    public static Set<String> getPunctuation() {
        if (PUNCTUATION == null) {
            PUNCTUATION = getTokenCollectionFromFile(PUNCTUATION_PATH, HashSet::new);
        }

        return PUNCTUATION;
    }

    public static void forceInit() {
        getNerClassifier();
        getPosPipeline();
        getStopWords();
        getVocab();
        getPunctuation();
    }
}
