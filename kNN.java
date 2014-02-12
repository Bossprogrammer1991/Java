**
 *
 * @author Eric Mariasis
 */

import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Mean {
    
    public static int nextNum(Scanner s) {
	while(s.hasNext()) 
	    {
		if(s.hasNextInt())
		    {
			int d = s.nextInt();
			return d;
		    }
		else if((s.next()).equals("?"))
		    {
			return -1;
		    }
		else{
		    s.next();
		}
	    }
	return -2;
    }

    public static void getTestLine(Scanner s, ArrayList<int[]> vals) {
	if(s.hasNext()){
	    int[] linestuff = new int[3];
	    for(int i = 0; i < 3; i++) {
		int v = nextNum(s);
		linestuff[i] = v;
	    }
	    vals.add(linestuff);
	}
    }
    
    public static void getTrainLine(Scanner s, ArrayList<int[]> vals, ArrayList<String> classes, ArrayList<String> exs) {
	getTestLine(s, vals);
	if(s.hasNext()) {
	    String next = s.next();
	    classes.add(next);
	    String id = s.next();
	    exs.add(id);
	}
    }

    public static void getTrainLines(Scanner s, ArrayList<int[]> vals, ArrayList<String> classes, ArrayList<String> exs, BufferedReader br) throws java.io.IOException{
	String line;
	while((line = br.readLine()) != null && s.hasNext()) {
	    getTrainLine(s, vals, classes, exs);
	}
    }

    public static int getMeanForAtt(ArrayList<int[]> vals, int i) {
	int sum = 0;
	int numdata = 0;
	for(int j = 0; j < vals.size(); j++)
	    {
		int [] cur = vals.get(j);
		if(cur[i] == -1)
		    {
			continue;
		    }
		else
		    {
			numdata++;
			sum += cur[i];
		    }
	    }
	int mean = sum / numdata;
	return mean;
    }

    public static void outputLines(Scanner s, ArrayList<int[]> vals, ArrayList<int[]> vals_train, ArrayList<String> classes, ArrayList<String> exs, BufferedReader br, BufferedWriter bw)  throws java.io.IOException{
        String line;
        int numlines = 0;
        while((line = br.readLine()) != null) {
            numlines++;
	    int index = numlines - 10;
            if(numlines < 10)
                {
                    bw.write(line);
                    bw.write("\n");
                }
            else{
		if(index < vals.size()) {
		for(int i = 0; i < 3; i++)
		    {
			int [] cur = vals.get(index);
			if(cur[i] == -1)
			    {
				bw.write(getMeanForAtt(vals_train, i) + ",");
			    }
			else{
			    bw.write(cur[i] + ",");
			}
		    }
		bw.write(classes.get(index) + ",");
		bw.write(exs.get(index) + "\n");
		}
            }
        }
    }

    public static void main(String[] args) throws java.io.IOException {
	String trainfile = args[0];
	String testfile = args[1];
	String output_train = args[2];
	String output_test = args[3];
	BufferedReader br_train = new BufferedReader(new FileReader(trainfile));
	BufferedReader br_test = new BufferedReader(new FileReader(testfile));
	BufferedReader br_out_test = new BufferedReader(new FileReader(testfile));
	BufferedReader br_out_train = new BufferedReader(new FileReader(trainfile));
	BufferedWriter bw_out_test = new BufferedWriter(new FileWriter(output_test));
	BufferedWriter bw_out_train = new BufferedWriter(new FileWriter(output_train));
	ArrayList<int[]> vals_train = new ArrayList<int[]>();
	ArrayList<int[]> vals_test = new ArrayList<int[]>();
	ArrayList<String> classes_train = new ArrayList<String>();
	ArrayList<String> classes_test = new ArrayList<String>();
	ArrayList<String> exs_train = new ArrayList<String>();
	ArrayList<String> exs_test = new ArrayList<String>();
	Scanner trainscan = new Scanner(new File(trainfile)).useDelimiter(",|\n");
	Scanner testscan = new Scanner(new File(testfile)).useDelimiter(",|\n");
	Scanner output_train_scan = new Scanner(new File(trainfile)).useDelimiter(",|\n");
	Scanner output_test_scan = new Scanner(new File(testfile)).useDelimiter(",|\n");
	getTrainLines(trainscan, vals_train, classes_train, exs_train, br_train);
	getTrainLines(testscan, vals_test, classes_test, exs_test, br_test);
	outputLines(output_train_scan, vals_train, vals_train, classes_train, exs_train, br_out_train, bw_out_train);
	outputLines(output_test_scan, vals_test, vals_train, classes_test, exs_test, br_out_test, bw_out_test);
	br_train.close();
	br_test.close();
	br_out_test.close();
	br_out_train.close();
	bw_out_test.close();
	bw_out_train.close();
	trainscan.close();
	testscan.close();
	output_train_scan.close();
	output_test_scan.close();
    }

}