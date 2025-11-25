import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Decoding {
    int getAscii(StringBuilder data,int charNum){
        int num = (int)data.charAt(charNum);
        if (num == 128) {
            num=13;
        } else if (num == 129) {
            num=10;
        }
        return num;
    }
    public String run(String filename) throws FileNotFoundException {
        String[] dictionary = new String[256];
        StringBuilder data = new StringBuilder();
        try {


            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(streamReader);


                String line = br.readLine();

                while (line != null) {
                    data.append(line);
                    data.append(System.lineSeparator());
                    line = br.readLine();
                    data.deleteCharAt(data.length()-1);
                }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            }
        for (int i = 0; i < data.length(); i++) {
            if ((int)data.charAt(i)==128){
                data.replace(i,i+1, String.valueOf((char)13));
            }else if ((int)data.charAt(i)==129){
                data.replace(i,i+1, String.valueOf((char)10));
            }
        }
            data.deleteCharAt(data.length()-1);

            StringBuilder str = new StringBuilder();
        int additional = 0;
        StringBuilder bin = new StringBuilder();
        int c = 1;
        int len = (int)data.charAt(0);
        int dicSize =0;
        while (dicSize < len){
            int ascii = getAscii(data,c);
            StringBuilder signs = new StringBuilder(Integer.toBinaryString(getAscii(data,c+1)));
            for (int j = signs.length(); j < 7; j++) {
                signs.insert(0,"0");
            }

            int blocksize = Integer.parseInt(signs.substring(0,4),2);
            int reminder = Integer.parseInt(signs.substring(4,7),2);

            StringBuilder code = new StringBuilder();
            for (int i = 0; i < blocksize; i++) {


                StringBuilder litcode = new StringBuilder(Integer.toBinaryString(getAscii(data,c+2+i)));
                for (int j = litcode.length(); j < 7; j++) {
                    litcode.insert(0,"0");
                }
                if (i==blocksize-1){
                    code.append(litcode.substring(7-reminder));
                }else{
                    code.append(litcode);
                }

            }
            c += 2+blocksize;
            dictionary[ascii]=code.toString();
            dicSize++;
        }


        for (int i = c; i < data.length(); i++) {
            if (i == data.length()-1){

                additional=(int)data.charAt(i);
                bin.append(str.substring(7-additional));

                break;
            }

            bin.append(str);
            int num = ((int) data.charAt(i));
            if (num==128){
                num=13;
            }else if(num==129){
                num=10;
            }
            str = new StringBuilder(Integer.toBinaryString(num));
            for (int j = str.length(); j < 7 ;j++) {
                str.insert(0, "0");
            }

        }
        int t=0;
        int i;
        StringBuilder decodedText = new StringBuilder();
        while (t<bin.length()){
            boolean exit = false;
        for (i = t+1; i < bin.length(); i++) {

            for (int j = 0; j < 127; j++) {

                if (bin.substring(t, i).equals(dictionary[j])) {
                    decodedText.append((char) j);
                    exit = true;
                    break;
                }

            }
            if (exit)break;
        }
            t = i;
        }
        return decodedText.toString();
    }
}
