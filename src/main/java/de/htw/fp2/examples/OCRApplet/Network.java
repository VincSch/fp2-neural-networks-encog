package de.htw.fp2.examples.OCRApplet;

import java.util.*;

/**
 * Java Neural Network Example Handwriting Recognition 
 * Copyright 2005 by Heaton Research, Inc. 
 * by Jeff Heaton (http://www.heatonresearch.com) 10-2005
 * ------------------------------------------------- 
 * This source code is copyrighted.
 * You may reuse this code in your own compiled projects. 
 * However, if you would like to redistribute this source code
 * in any form, you must obtain permission from Heaton Research. 
 * (support@heatonresearch.com). 
 * ------------------------------------------------- 
 * 
 * This class implements a somewhat generic neural network,
 * it forms the base class for the Kohonen neural network.
 * 
 * ------------------------------------------------- 
 * Want to learn more about Neural Network Programming in Java?
 * Have a look at our e-book:
 * 
 * http://www.heatonresearch.com/articles/series/1/
 *  
 * @author Jeff Heaton (http://www.jeffheaton.com)
 * @version 1.0
 */

abstract public class Network
{

  /**
   * The value to consider a neuron on
   */
  public final static double NEURON_ON = 0.9;

  /**
   * The value to consider a neuron off
   */
  public final static double NEURON_OFF = 0.1;

  /**
   * Output neuron activations
   */
  protected double output[];

  /**
   * Mean square error of the network
   */
  protected double totalError;

  /**
   * Number of input neurons
   */
  protected int inputNeuronCount;

  /**
   * Number of output neurons
   */
  protected int outputNeuronCount;

  /**
   * Random number generator
   */
  protected Random random = new Random(System.currentTimeMillis());

  /**
   * Called to learn from training sets.
   * 
   * @exception RuntimeException
   */
  abstract public void learn() throws RuntimeException;

  /**
   * Called to present an input pattern.
   * 
   * @param input
   *          The input pattern
   */
  abstract void trial(double[] input);

  /**
   * Called to get the output from a trial.
   */
  double[] getOutput()
  {
    return output;
  }

  /**
   * Called to calculate the trial errors.
   * 
   * @param train
   *          The training set.
   * @return The trial error.
   * @exception RuntimeException
   */
  double calculateTrialError(TrainingSet train) throws RuntimeException
  {
    int i, size, tset, tclass;
    double diff;

    totalError = 0.0; // reset total error to zero

    // loop through all samples
    for (int t = 0; t < train.getTrainingSetCount(); t++)
    {
      // trial
      trial(train.getOutputSet(t));

      tclass = (int) (train.getClassify(train.getInputCount() - 1));
      for (i = 0; i < train.getOutputCount(); i++)
      {
        if (tclass == i)
          diff = NEURON_ON - output[i];
        else
          diff = NEURON_OFF - output[i];
        totalError += diff * diff;
      }

      for (i = 0; i < train.getOutputCount(); i++)
      {
        diff = train.getOutput(t, i) - output[i];
        totalError += diff * diff;

      }
    }

    totalError /= (double) train.getTrainingSetCount();
    ;
    return totalError;
  }

  /**
   * Calculate the length of a vector.
   * 
   * @param v
   *          vector
   * @return Vector length.
   */
  static double vectorLength(double v[])
  {
    double rtn = 0.0;
    for (int i = 0; i < v.length; i++)
      rtn += v[i] * v[i];
    return rtn;
  }

  /**
   * Called to calculate a dot product.
   * 
   * @param vec1
   *          one vector
   * @param vec2
   *          another vector
   * @return The dot product.
   */

  double dotProduct(double vec1[], double vec2[])
  {
    int k, m, v;
    double rtn;

    rtn = 0.0;
    k = vec1.length / 4;
    m = vec1.length % 4;

    v = 0;
    while ((k--) > 0)
    {
      rtn += vec1[v] * vec2[v];
      rtn += vec1[v + 1] * vec2[v + 1];
      rtn += vec1[v + 2] * vec2[v + 2];
      rtn += vec1[v + 3] * vec2[v + 3];
      v += 4;
    }

    while ((m--) > 0)
    {
      rtn += vec1[v] * vec2[v];
      v++;
    }

    return rtn;
  }

  /**
   * Called to randomize weights.
   * 
   * @param weight
   *          A weight matrix.
   */
  void randomizeWeights(double weight[][])
  {
    double r;

    int temp = (int) (3.464101615 / (2. * Math.random())); // SQRT(12)=3.464...

    for (int y = 0; y < weight.length; y++)
    {
      for (int x = 0; x < weight[0].length; x++)
      {
        r = (double) random.nextInt() + (double) random.nextInt()
            - (double) random.nextInt() - (double) random.nextInt();
        weight[y][x] = temp * r;
      }
    }
  }

}