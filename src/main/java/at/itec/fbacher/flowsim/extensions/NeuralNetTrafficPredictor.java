package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.model.Link;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.IOException;

/**
 * Created by florian on 16.11.2015.
 */
public class NeuralNetTrafficPredictor implements TrafficPredictor {

    Instances structure;
    MultilayerPerceptron classifier;

    public NeuralNetTrafficPredictor() {
        ArffLoader loader = new ArffLoader();
        try {
            structure = loader.getStructure();
            structure.setClassIndex(structure.numAttributes() - 1);
            classifier = new MultilayerPerceptron();
            classifier.buildClassifier(structure);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public double getPrediction(Link link, int when) {
        //classifier
        return 0;
    }
}
