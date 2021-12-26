package LZW.Compressor;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;

/**
 * LZW PASSWORD PROTECTED PROJECT
 * Submitted by:
 * Student 1: 	DANNY KOGEL 318503257
 * Student 2. 	ALEX BREGER 205580087
 */

public class LZW_Compressor_Decompressor implements Compressor {

    //bitsize, also used in the bit size slider.
    protected static int m_SizeOfDictionaryElementInBits = 22;

    public static int getM_SizeOfDictionaryElementInBits() {
        return m_SizeOfDictionaryElementInBits;
    }

    public static void setM_SizeOfDictionaryElementInBits(int m_SizeOfDictionaryElementInBitsCurr) {
        m_SizeOfDictionaryElementInBits = m_SizeOfDictionaryElementInBitsCurr;
    }
    //the size of the default dictionary(all ascii chars)
    private int m_DefaultDictionarySize = 256;

    public LZW_Compressor_Decompressor() {

    }

    @Override
    public void Compress(String[] input_names, String[] output_names)
    {
        HashMap<String, String> dictionary = new HashMap<String, String>();

        //initializes default dictionary in the hashmap
        for(int i = 0; i < m_DefaultDictionarySize; i++)
        {
            dictionary.put("" + (char)i, Integer.toString(i));
        }

        BitInputStream inputBit = null;
        BitOutputStream outputBit = null;
        char char_temporaryCharacterToConcatenate;
        String String_inputValueCurrent = "";
        int nextFreeKeyInHashMap = m_DefaultDictionarySize;

        try
        {
            try {
                inputBit = new BitInputStream(new FileInputStream(input_names[0]));
                //in case a problem with file arises at reading
            }catch (FileNotFoundException e){
                JFrame notFound = new JFrame();
                JOptionPane.showMessageDialog(notFound,"There was a problem locating a file","Alert",JOptionPane.WARNING_MESSAGE);
            }
            outputBit = new BitOutputStream(new FileOutputStream(input_names[1]));

            //reads the file until its empty doing the LZW logic
            while (true) {
                //warning for going over the dictionary size.
                if (nextFreeKeyInHashMap == (Math.pow(2, m_SizeOfDictionaryElementInBits)))
                {
                    JFrame bitSizeError = new JFrame();
                    JOptionPane.showMessageDialog(bitSizeError,"The current BitSize setting is too low for this file.\nTo properly Compress it please increase it in the BitSize menu","Alert",JOptionPane.WARNING_MESSAGE);
                    throw new DictionarySizeError("[COMPRESSING] ERROR DICTIONARY SIZE");
                }

                char_temporaryCharacterToConcatenate = (char)inputBit.readBits(8);
                if(char_temporaryCharacterToConcatenate=='\uFFFF')
                {
                    break;
                }
                if(dictionary.containsKey(String_inputValueCurrent + char_temporaryCharacterToConcatenate))
                {
                    String_inputValueCurrent = String_inputValueCurrent + char_temporaryCharacterToConcatenate;
                }
                else {
                    outputBit.writeBits(m_SizeOfDictionaryElementInBits,Integer.valueOf(dictionary.get(String_inputValueCurrent)));
                    dictionary.put(String_inputValueCurrent + char_temporaryCharacterToConcatenate, Integer.toString(nextFreeKeyInHashMap++));
                    String_inputValueCurrent = "" + char_temporaryCharacterToConcatenate;
                }
            }
            //reads the last portion of the file.
            outputBit.writeBits(m_SizeOfDictionaryElementInBits,Integer.valueOf(dictionary.get(String_inputValueCurrent)));
            outputBit.writeBits(8,Integer.valueOf(dictionary.get(String_inputValueCurrent)));

        }
        catch (IOException | NullPointerException | DictionarySizeError e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void Decompress(String[] input_names, String[] output_names) {

        HashMap<String, String> DecompressedDictionary = new HashMap<String, String>();
        //initializes default dictionary in the hashmap switiching keys and values places
        for(int i = 0; i < m_DefaultDictionarySize; i++)
        {
            DecompressedDictionary.put(Integer.toString(i),"" + (char)i);
        }

        BitInputStream inputBit = null;
        BitOutputStream outputBit = null;

        String inputValueNext = "";
        String inputValueCurrent = "";
        int int_getValueFromHashMap = 0;
        int nextFreeKeyInHashMap = m_DefaultDictionarySize;

        try
        {
            inputBit = new BitInputStream(new FileInputStream(input_names[1]));
            outputBit = new BitOutputStream(new FileOutputStream(output_names[0]));

            int_getValueFromHashMap = (inputBit.readBits(m_SizeOfDictionaryElementInBits));
            inputValueCurrent = DecompressedDictionary.get(String.valueOf(int_getValueFromHashMap));
            try {
                outputBit.writeBits(8, inputValueCurrent.charAt(0));
                //not sure what causes the problem but does not affect the output code.
            }catch(NullPointerException e){
            }

            //does the LZW logic while the file contains data
            while (true)
            {
                try {
                int_getValueFromHashMap = (inputBit.readBits(m_SizeOfDictionaryElementInBits));
                if(int_getValueFromHashMap==-1)
                {
                    break;
                }
                if(DecompressedDictionary.containsKey(String.valueOf(int_getValueFromHashMap)))
                {
                    inputValueNext = inputValueCurrent;
                    inputValueCurrent = DecompressedDictionary.get(String.valueOf(int_getValueFromHashMap));
                    DecompressedDictionary.put(Integer.toString(nextFreeKeyInHashMap++),inputValueNext + inputValueCurrent.charAt(0));
                }
                else
                {
                    DecompressedDictionary.put(Integer.toString(nextFreeKeyInHashMap++),inputValueCurrent + inputValueCurrent.charAt(0));
                    inputValueCurrent = DecompressedDictionary.get(String.valueOf(int_getValueFromHashMap));
                }
                    //writes all the available characters it can
                    for (char ch : inputValueCurrent.toCharArray()) {
                        outputBit.writeBits(8, ch);
                    }
                }catch (NullPointerException e) {}
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
