import com.sun.jdi.CharValue;
import org.w3c.dom.Node;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.lang.reflect.Array;

public class Encoding {
    public Encoding() {
    }
    class objChar{
        int freq;
        String code;
        int ASCII;


        public objChar(int ASCII) {
            this.freq = 0;
            this.code = "";
            this.ASCII = ASCII;
        }

        void addFreq(){
            this.freq ++;
        }

        int getFreq(){
            return freq;
        }

    }

    class heapNode{
        heapNode leftChild;
        heapNode rightChild;
        objChar Char;
        int value;

        public heapNode(objChar aChar, int value,heapNode leftChild, heapNode rightChild) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            Char = aChar;
            this.value = value;
        }
    }


    public String[] run(String input,String filename,int factor) throws URISyntaxException, IOException {
        int invalid = 0;
        if (input.contains("\n"))invalid++;
        if (input.contains(" "))invalid++;
        if (input.contains("~"))invalid++;


        int chars = 256;
        objChar[] obj = new objChar[chars];
        int validChar = 0;
        objChar[] nodes = null;
        int fac=0;
        int index = 0;
        for (int f = 0; f < fac + 1; f++) {
            for (int i = 0; i < chars; i++) {
                obj[i] = new objChar(i);

            }
            for (int i = 0; i < input.length(); i++) {
                if ((int) input.charAt(i) != 13) {
                    obj[input.charAt(i)].addFreq();
                }
            }
            mergesort(obj, 0, chars - 1);
            validChar = 0;
            for (int i = 0; i < chars; i++) {
                if (obj[i].freq != 0) {
                    validChar++;
                }
            }

            for (int i = 0; i < validChar; i++) {
                if (obj[i].freq != 0) {
                    validChar++;
                }
            }

            nodes = new objChar[validChar];
            int counter = 0;
            for (int i = 0; i < chars; i++) {
                if (obj[i].freq != 0) {
                    nodes[counter] = obj[i];
                    counter++;
                }
            }

            if (f==0){

                fac = factor*(validChar-invalid)/110;
                if (fac!=0){
                    fac++;
                }

            }


            if (fac!=f) {
                char chengeChar = (char) nodes[index].ASCII;
                nodes[0].freq = 0;
                if (chengeChar == ' ' || chengeChar == '~' || chengeChar == '\n') {
                    fac++;
                    index++;
                    continue;
                }
                input = input.replace(chengeChar, '~');
            }
        }


        huffman(validChar, nodes);
        String[] dictionary = new String[127];

        int sum = 0;
        int c = 0;
        for (int i = 0; i < chars; i++) {
            if (obj[i].freq != 0) {
                dictionary[obj[i].ASCII] = obj[i].code;
                sum += obj[i].freq * obj[i].code.length();
                c++;
            }
        }

        String pureText = serializer(input,nodes,validChar,dictionary);
        String pureEncoded = coding(pureText);
        saveToFile(filename,pureEncoded);
        System.out.println(pureEncoded.length());

        return dictionary;

    }

    String serializer(String in,objChar[] nodes,int validChar,String[] dictionary){
        StringBuilder pureText = new StringBuilder();
        StringBuilder dicSize = new StringBuilder(Integer.toBinaryString(nodes.length));
        for (int j = dicSize.length(); j < 7; j++) {
            dicSize.insert(0,"0");
        }
        pureText.append(dicSize);
        for (int i = 0; i < validChar; i++) {
            StringBuilder character = new StringBuilder();
            character.append(Integer.toBinaryString(nodes[i].ASCII));
            for (int j = character.length(); j < 7; j++) {
                character.insert(0,"0");
            }
            StringBuilder blocksize = new StringBuilder((Integer.toBinaryString((nodes[i].code.length()/7)+1)));

            for (int j = blocksize.length(); j < 4; j++) {
                blocksize.insert(0,"0");
            }
            StringBuilder rem = new StringBuilder((Integer.toBinaryString(nodes[i].code.length()%7)));
            for (int j = rem.length(); j < 3; j++) {
                rem.insert(0,"0");
            }
            StringBuilder code = new StringBuilder();
            int j = 0;
            for (j = 0; j < (nodes[i].code.length()/7); j+=7) {
                code.append(nodes[i].code.substring(j,j+7));
            }
            StringBuilder lastCode = new StringBuilder(nodes[i].code.substring(j));
            for (j = lastCode.length(); j < 7; j++) {
                lastCode.insert(0,"0");
            }
            code.append(lastCode);
            String sizes = blocksize.append(rem).toString();
            pureText.append(character).append(sizes).append(code);
        }


        for (int i = 0; i < in.length(); i++) {
            if ((int) in.charAt(i) != 13) {
                pureText.append(dictionary[in.charAt(i)]);
            }
        }
        return pureText.toString();
    }

    String coding (String pureText){
        int additional = 0;
        StringBuilder pureEncoded = new StringBuilder();

        int addGroup = 0;
        for (int i = 0; i < pureText.length(); i += 7) {
            int num = (Integer.parseInt(pureText.substring(i, Integer.min(i + 7, pureText.length())), 2));
            if (num == 13) {
                pureEncoded.append((char) 128);
            } else if (num == 10) {
                pureEncoded.append((char) 129);
            }else {
                pureEncoded.append((char) num);
            }
            if (i+7>= pureText.length()) {
                additional = pureText.length() - i;
            }
        }
        pureEncoded.append((char)additional);
        return pureEncoded.toString();
    }

    void saveToFile(String filename,String pureEncoded){
        Path path = Paths.get(filename);
        byte[] bytes = pureEncoded.toString().getBytes(StandardCharsets.UTF_8);

        try {
            Files.write(path, bytes);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    // For comparing the nodes
    class ImplementComparator implements Comparator<heapNode> {
        public int compare(heapNode x, heapNode y) {
            return x.value - y.value;
        }
    }

        void printCode(heapNode root, String s) {

            if (root.leftChild == null && root.rightChild == null ) {
                root.Char.code = s;
                return;
            }
            if (root.leftChild != null)
                printCode(root.leftChild, s + "0");
            if (root.rightChild != null)
                printCode(root.rightChild, s + "1");
        }

        void huffman(int n,objChar[] obj) {

            PriorityQueue<heapNode> q = new PriorityQueue<heapNode>(n, new ImplementComparator());

            for (int i = 0; i < n; i++) {
                heapNode hn = new heapNode(obj[i],obj[i].freq,null,null);

                q.add(hn);
            }

            heapNode root = null;

            while (q.size() > 1) {

                heapNode x = q.peek();
                q.poll();

                heapNode y = q.peek();
                q.poll();

                heapNode f = new heapNode(new objChar('-') ,x.value + y.value,x,y);
                root = f;
                q.add(f);
            }
            printCode(root, "");


        }


    static void merge(objChar[] arr, int p, int q, int r) {
        int n1 = q - p + 1;
        int n2 = r - q;

        objChar[] L = new objChar[n1];
        objChar[] R = new objChar[n2];

        for (int i = 0; i < n1; ++i)
            L[i] = arr[p + i];
        for (int j = 0; j < n2; ++j)
            R[j] = arr[q + 1 + j];

        int i = 0, j = 0;

        int k = p;
        while (i < n1 && j < n2) {
            if (L[i].freq <= R[j].freq) {
                arr[k] = L[i];
                i++;
            }
            else  {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }


        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    static void mergesort(objChar[] arr, int p, int r)
    {
        if (p < r) {
            int q =p+ (r-p)/2;
            mergesort(arr, p, q);
            mergesort(arr, q + 1, r);
            merge(arr, p, q, r);
        }
    }
    public static void savePropertiesToFile(Properties properties, File propertiesFile) throws IOException {
        FileOutputStream out = new FileOutputStream(propertiesFile);
        properties.store(out, null);
        out.close();
    }
}

