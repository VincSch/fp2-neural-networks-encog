package de.htw.fp2.examples.OCRApplet;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
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
 * This class implements an applet that allows the user to test out
 * basic character recognition functions.
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

public class OCRApplet extends Applet
{
  /**
   * The downsample width for the application.
   */
  static final int DOWNSAMPLE_WIDTH = 5;

  /**
   * The down sample height for the application.
   */
  static final int DOWNSAMPLE_HEIGHT = 7;

  Vector sampleList = new Vector();;

  /**
   * The entry component for the user to draw into.
   */
  Entry entry;

  /**
   * The down sample component to display the drawing downsampled.
   */
  Sample sample;

  /**
   * The neural network.
   */
  KohonenNetwork net;

  public static final String HANDWRITING[] = {
      "A:00110001100111001010111111100110001",
      "B:11111100011000111111100111000110111",
      "C:11111100001000010000100001100001111",
      "D:11111100011000110000100011000111111",
      "E:11111100001000011111100001000011111",
      "F:11111100001000011110100001000010000",
      "G:01110110001000010111100011000111111",
      "H:10001100001000111001111111000110001",
      "I:11111001000010000100001000010000111",
      "J:11111001000010000100101001010011100",
      "K:10001100111111011010100101001110011",
      "L:10000100001000010000100001000011111",
      "M:11111101111011110100101001010110101",
      "N:11111110111000110001100001000110001",
      "O:11111100011000110000100011000111111",
      "P:11111100011001111110100001000010000",
      "Q:01111110011000110001100111101101111",
      "R:11111100011000111011111101001110001",
      "S:01111110001100001111000010000111111",
      "T:11111001000010000100001000010000100",
      "U:10001100001000110001100011001111110",
      "V:10001100011101101011011100111000110",
      "W:10101101011010110101101011011111111",
      "X:10011110100111001100111001011010010",
      "Y:10001110110111001100010000100001000",
      "Z:11111000110011001100110001000011111", };

  /**
   * Setup the GUI
   */
  public void init()
  {
    preload();
    updateList();

    setLayout(new GridLayout(2, 1));

    Panel topPanel = new Panel();
    Panel bottomPanel = new Panel();

    // create top button panel
    Panel topButtonPanel = new Panel();
    Panel bottomButtonPanel = new Panel();
    topButtonPanel.setLayout(new GridLayout(3, 1));
    topButtonPanel.add(recognize = new Button("Recognize"), BorderLayout.SOUTH);
    topButtonPanel.add(clear = new Button("Clear"));
    Panel addPanel = new Panel();
    addPanel.setLayout(new GridLayout(1, 2));
    addPanel.add(add = new Button("Add:"));
    addPanel.add(letterToAdd);
    topButtonPanel.add(addPanel);
    letterToAdd.setText("a");

    // create the bottom button pannel
    bottomButtonPanel.setLayout(new GridLayout(1, 2));
    bottomButtonPanel.add(del = new Button("Delete"));
    bottomButtonPanel.add(delAll = new Button("Delete All"));
    bottomButtonPanel.add(train = new Button("Train"));

    // create top panel
    entry = new Entry();
    topPanel.setLayout(new BorderLayout());
    topPanel.add(message = new Label("Draw a capital letter, click Recognize"),
        BorderLayout.NORTH);
    topPanel.add(entry, BorderLayout.CENTER);
    topPanel.add(topButtonPanel, BorderLayout.EAST);

    // create bottom panel
    bottomPanel.setLayout(new BorderLayout());
    bottomPanel.add(new Label("Known Letter Database"), BorderLayout.NORTH);
    Panel bottomContent = new Panel();
    bottomContent.setLayout(new GridLayout(1, 2));
    bottomPanel.add(bottomContent, BorderLayout.CENTER);
    bottomPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

    // create the letters panel
    Panel lettersPanel = new Panel();
    scrollPane1.add(letters);
    lettersPanel.setLayout(new BorderLayout());
    lettersPanel.add(letters, BorderLayout.CENTER);

    // create the downsample panel
    Panel downSamplePanel = new Panel();
    downSamplePanel.setLayout(new BorderLayout());
    sample = new Sample(DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
    entry.setSample(sample);
    downSamplePanel.add(sample, BorderLayout.CENTER);

    bottomContent.add(lettersPanel);
    bottomContent.add(downSamplePanel);

    add(topPanel);
    add(bottomPanel);

    Font dialogFont = new Font("Arial", Font.BOLD, 10);
    clear.setFont(dialogFont);
    add.setFont(dialogFont);
    del.setFont(dialogFont);
    delAll.setFont(dialogFont);
    recognize.setFont(dialogFont);
    train.setFont(dialogFont);

    SymAction lSymAction = new SymAction();
    clear.addActionListener(lSymAction);
    add.addActionListener(lSymAction);
    del.addActionListener(lSymAction);
    delAll.addActionListener(lSymAction);
    SymListSelection lSymListSelection = new SymListSelection();
    letters.addItemListener(lSymListSelection);

    train.addActionListener(lSymAction);
    recognize.addActionListener(lSymAction);

    message.setForeground(Color.red);

    entry.requestFocus();

  }

  /**
   * The add button.
   */
  Button add = new Button();

  /**
   * The clear button
   */
  Button clear = new Button();

  /**
   * The recognize button
   */
  Button recognize = new Button();

  ScrollPane scrollPane1 = new ScrollPane();

  /**
   * The letters list box
   */
  java.awt.List letters = new java.awt.List();

  /**
   * The delete button
   */
  Button del = new Button();

  /**
   * The delete button
   */
  Button delAll = new Button();

  /**
   * The train button
   */
  Button train = new Button();

  Label message = new Label();

  TextField letterToAdd = new TextField("", 1);

  class SymAction implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object object = event.getSource();
      if (object == clear)
        clear_actionPerformed(event);
      else if (object == add)
        add_actionPerformed(event);
      else if (object == del)
        del_actionPerformed(event);
      else if (object == delAll)
        deleteAll_actionPerformed(event);
      else if (object == train)
        train_actionPerformed(event);
      else if (object == recognize)
        recognize_actionPerformed(event);
    }
  }

  /**
   * Called to clear the image.
   * 
   * @param event
   *          The event
   */
  void clear_actionPerformed(ActionEvent event)
  {
    entry.clear();
    sample.getData().clear();
    sample.repaint();
  }

  /**
   * Called to clear the image.
   * 
   * @param event
   *          The event
   */
  void deleteAll_actionPerformed(ActionEvent event)
  {
    sampleList.removeAllElements();
    net = null;
    updateList();
    entry.clear();
    sample.getData().clear();
    sample.repaint();
  }

  /**
   * Called to add the current image to the training set
   * 
   * @param event
   *          The event
   */
  void add_actionPerformed(ActionEvent event)
  {
    int i;

    String letter = letterToAdd.getText().trim();

    if (letter.length() > 1)
    {
      message.setText("Enter only one letter.");
      return;
    }

    if (letter.length() < 1)
    {
      message.setText("Enter a letter to add.");
      return;
    }

    entry.downSample();
    SampleData sampleData = (SampleData) sample.getData().clone();
    sampleData.setLetter(letter.charAt(0));

    for (i = 0; i < sampleList.size(); i++)
    {
      SampleData str = (SampleData) sampleList.elementAt(i);
      if (str.equals(sampleData))
      {
        message.setText("Letter already defined, delete it first!");
        return;
      }

      if (str.compareTo(sampleData) > 0)
      {
        sampleList.insertElementAt(sampleData, i);
        updateList();
        return;
      }
    }
    sampleList.insertElementAt(sampleData, sampleList.size());
    updateList();
    letters.select(i);
    entry.clear();
    sample.repaint();

  }

  /**
   * Called when the del button is pressed.
   * 
   * @param event
   *          The event.
   */
  void del_actionPerformed(ActionEvent event)
  {
    int i = letters.getSelectedIndex();

    if (i == -1)
    {
      message.setText("Please select a letter to delete.");

      return;
    }

    sampleList.removeElementAt(i);
    updateList();
  }

  class SymListSelection implements ItemListener
  {

    public void itemStateChanged(ItemEvent event)
    {
      Object object = event.getSource();
      if (object == letters)
        letters_valueChanged(event);

    }
  }

  /**
   * Called when a letter is selected from the list box.
   * 
   * @param event
   *          The event
   */
  void letters_valueChanged(ItemEvent event)
  {
    if (letters.getSelectedIndex() == -1)
      return;
    SampleData selected = (SampleData) sampleList.elementAt(letters
        .getSelectedIndex());
    sample.setData((SampleData) selected.clone());
    sample.repaint();
    entry.clear();
  }

  /**
   * Called when the train button is pressed.
   * 
   * @param event
   *          The event.
   */
  void train_actionPerformed(ActionEvent event)
  {

    try
    {
      int inputNeuron = OCRApplet.DOWNSAMPLE_HEIGHT
          * OCRApplet.DOWNSAMPLE_WIDTH;
      int outputNeuron = sampleList.size();

      TrainingSet set = new TrainingSet(inputNeuron, outputNeuron);
      set.setTrainingSetCount(sampleList.size());

      for (int t = 0; t < sampleList.size(); t++)
      {
        int idx = 0;
        SampleData ds = (SampleData) sampleList.elementAt(t);
        for (int y = 0; y < ds.getHeight(); y++)
        {
          for (int x = 0; x < ds.getWidth(); x++)
          {
            set.setInput(t, idx++, ds.getData(x, y) ? .5 : -.5);
          }
        }
      }

      net = new KohonenNetwork(inputNeuron, outputNeuron, this);
      net.setTrainingSet(set);
      net.learn();
      this.clear_actionPerformed(null);
      message.setText("Trained. Ready to recognize.");
    } catch (Exception e)
    {
      message.setText("Exception:" + e.getMessage());

    }

  }

  /**
   * Called when the recognize button is pressed.
   * 
   * @param event
   *          The event.
   */
  void recognize_actionPerformed(ActionEvent event)
  {
    if (net == null)
    {
      message.setText("I need to be trained first!");
      return;
    }
    entry.downSample();

    double input[] = new double[5 * 7];
    int idx = 0;
    SampleData ds = sample.getData();
    for (int y = 0; y < ds.getHeight(); y++)
    {
      for (int x = 0; x < ds.getWidth(); x++)
      {
        input[idx++] = ds.getData(x, y) ? .5 : -.5;
      }
    }

    double normfac[] = new double[1];
    double synth[] = new double[1];

    int best = net.winner(input, normfac, synth);
    char map[] = mapNeurons();
    message.setText("  " + map[best] + "   (Neuron #" + best + " fired)");
    clear_actionPerformed(null);

  }

  /**
   * Used to map neurons to actual letters.
   * 
   * @return The current mapping between neurons and letters as an array.
   */
  char[] mapNeurons()
  {

    char map[] = new char[sampleList.size()];
    double normfac[] = new double[1];
    double synth[] = new double[1];

    for (int i = 0; i < map.length; i++)
      map[i] = '?';
    for (int i = 0; i < sampleList.size(); i++)
    {
      double input[] = new double[5 * 7];
      int idx = 0;
      SampleData ds = (SampleData) sampleList.elementAt(i);
      for (int y = 0; y < ds.getHeight(); y++)
      {
        for (int x = 0; x < ds.getWidth(); x++)
        {
          input[idx++] = ds.getData(x, y) ? .5 : -.5;
        }
      }

      int best = net.winner(input, normfac, synth);
      map[best] = ds.getLetter();
    }
    return map;
  }

  public void updateList()
  {
    letters.removeAll();
    for (int i = 0; i < sampleList.size(); i++)
    {
      SampleData sample = (SampleData) sampleList.elementAt(i);
      letters.add("" + sample.letter);
    }

  }

  public void preload()
  {
    int index = 0;
    for (int i = 0; i < OCRApplet.HANDWRITING.length; i++)
    {
      String line = HANDWRITING[i].trim();
      SampleData ds = new SampleData(line.charAt(0),
          OCRApplet.DOWNSAMPLE_WIDTH, OCRApplet.DOWNSAMPLE_HEIGHT);
      sampleList.insertElementAt(ds, index++);
      int idx = 2;
      for (int y = 0; y < ds.getHeight(); y++)
      {
        for (int x = 0; x < ds.getWidth(); x++)
        {
          ds.setData(x, y, line.charAt(idx++) == '1');
        }
      }
    }
    train_actionPerformed(null);
  }

}
