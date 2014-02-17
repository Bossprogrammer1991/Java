import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class perceptron {

    /**
     * @param args
 * @throws Exception
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 3) {
            System.out
                .println("Invalid parameters.\n Usage: kNN <train file> <test file> <output file>\n Files must be in ARFF format.");
            return;
        }
        String trainFile = args[2];
        String testFile = args[3];
        String outputFile = args[4];
        String eta_str = args[1];
        double eta = Double.parseDouble(eta_str);

        ArffParser parser = new ArffParser();
        ArffInstances trainData = null;
        ArffInstances testData = null;
        ArrayList<Double> wi_s = new ArrayList<Double>();
        ArrayList<String> classes = new ArrayList<String>();
        try {
            trainData = parser.loadArff(trainFile);
            System.out.println("Loaded train file: " + trainFile
                               + ". Total data records: " + trainData.getNumberOfRows());

            testData = parser.loadArff(testFile);
            System.out.println("Loaded test file: " + testFile
                               + ". Total data records: " + testData.getNumberOfRows());
        } catch (Exception e) {
            System.out.println("Parsing of "
                               + (trainData == null ? trainFile : testFile)
                               + " failed. Invalid ARFF file?");
            throw e;
        }

        takeNormalizationAction(trainData, testData, "1");

        System.out.println("Going to start computation.");
        runPerceptron(trainData, eta, wi_s);
        usePerceptron(testData, wi_s, classes);
System.out.println("Finished computation.");
        perceptron percept = new perceptron();
        percept.saveOutputFile(testFile, outputFile, classes);

        System.out.println("Generated output file " + outputFile);
        percentAccuracy(classes, testData);

    }

    public static double percentAccuracy(ArrayList<String> classes, ArffInstances testData)
    {
        int numright = 0;
        ArrayList<String> knn = new ArrayList<String>();
        for(int i = 0; i < testData.getNumberOfRows(); i++)
            {
                if(testData.getClassAttributeValue(i).equals(classes.get(i)))
                    {
                        numright++;
                    }
            }
        double percent = (double)numright / (double)testData.getNumberOfRows()
            * 100.0;
        System.out.println("PERCENT accuracy is " + percent);
        return percent;
    }


    public static double getMeanForAtt(ArffInstances trainData, int j)
    {
        double sum = 0;
        int numrows = trainData.getNumberOfRows();
        for(int i = 0; i < numrows; i++)
            {
                sum += trainData.getRawAttributeValue(i, j);
            }
        double mean = sum / numrows;
        return mean;
    }

    public static double getSDForAtt(ArffInstances trainData, int j)
double sum = 0;
        double mean = getMeanForAtt(trainData, j);
        int denom = trainData.getNumberOfRows() - 1;
        for(int i = 0; i < trainData.getNumberOfRows(); i++)
            {
                double curdev = trainData.getRawAttributeValue(i, j) - mean;
                double toAdd = Math.pow(curdev, 2);
                sum += toAdd;
            }
        double variance = sum / denom;
        double sd = Math.sqrt(variance);
        return sd;
    }

    public static double getNormalized(ArffInstances target, ArffInstances trainData, int i, int j)
    {
        BigDecimal z_ij = BigDecimal.ZERO;
        double x_ij = target.getRawAttributeValue(i, j);
        double m_j = getMeanForAtt(trainData, j);
        double o_j = getSDForAtt(trainData, j);
        z_ij = z_ij.add(new BigDecimal((x_ij - m_j) / o_j));
        z_ij = z_ij.setScale(3, BigDecimal.ROUND_UP);
        double z = z_ij.doubleValue();
        return z;
    }

    public static void normalize(ArffInstances target, ArffInstances trainData)
    {
        int target_r = target.getNumberOfRows();
        int target_c = target.getClassAttributeIndex();
        ArrayList<double[]> normalizedData = new ArrayList<double[]>();
        for(int r = 0; r < target_r; r++) //iterate through once and store normalized values so that training values can't be mutated during normalization
            {
                double [] newrow = new double[target_c];
                for(int c = 0; c < target_c; c++)
                    {
                        double normal = getNormalized(target, trainData, r, c);
                        newrow[c] = normal;
                    }
                normalizedData.add(newrow);
            }
 for(int r = 0; r < target_r; r++) //now change all values after calculation is done
            {
                for(int c = 0; c < target_c; c++)
                    {
                        target.setRawAttributeValue(r, c, normalizedData.get(r)[c]);
                    }
            }
    }

    public static void takeNormalizationAction(ArffInstances trainData, ArffInstances testData, String arg)
    {
        if(arg.equals("1"))
            {
                normalize(testData, trainData);
                normalize(trainData, trainData);
            }
    }

    public static double output(ArrayList<Double> wi_s, ArrayList<Double> xi_s)
    {
        double sum = 0;
        for(int i = 0; i < wi_s.size(); i++)
            {
                double toAdd = wi_s.get(i) * xi_s.get(i);
                sum += toAdd;
            }
        return sum;
    }

    public static double targ_out(ArffInstances trainData, int i)
    {
        String val = trainData.getClassAttributeValue(i);
        return Double.parseDouble(val);
    }

    public static void addXis(int i, ArffInstances trainData, ArrayList<Double> xi_s)
    {
        xi_s.add(1.0);
        for(int j = 0; j < trainData.getClassAttributeIndex(); j++)
            {
                xi_s.add(trainData.getRawAttributeValue(i, j));
            }
 }

    public static void addWis(ArffInstances trainData, ArrayList<Double> wi_s)
    {
        for(int i = 0; i < trainData.getClassAttributeIndex() + 1; i++)
            {
                wi_s.add(0.0);
            }
    }

    public static void runPerceptron(ArffInstances trainData, double eta, ArrayList<Double> wi_s)
    {
        addWis(trainData, wi_s);
        for(int i = 0; i < trainData.getNumberOfRows(); i++)
            {
                ArrayList<Double> xi_s = new ArrayList<Double>();
                addXis(i, trainData, xi_s);
                for(int run = 0; run < 500; run++)
                    {
                        double targetValue = targ_out(trainData, i);
                        for(int j = 0; j < trainData.getClassAttributeIndex() + 1; j++)
 {
                                double wi = wi_s.get(j);
                                double xi = xi_s.get(j);
                                double op = output(wi_s, xi_s);
                                wi = wi + eta * (targetValue - op) * xi;
                                wi_s.set(j, wi);
                            }
                    }
            }
    }

    public static void usePerceptron(ArffInstances testData, ArrayList<Double> wi_s, ArrayList<String> classes)
    {
        for(int i = 0; i < testData.getNumberOfRows(); i++)
            {
                ArrayList<Double> xi_s = new ArrayList<Double>();
                xi_s.add(1.0);
                for(int j = 0; j < testData.getClassAttributeIndex(); j++)
                    {
                        xi_s.add(testData.getRawAttributeValue(i, j));
                    }
                double op = output(wi_s, xi_s);
                if(op > 0)
                    {
                        classes.add("1");
                    }
                else{
                    classes.add("-1");
                }
            }
    }

    private void saveOutputFile(String testFile, String outputFile,
                                ArrayList<String> classes) throws IOException {

        BufferedReader reader = null;
        BufferedWriter writer = null;
        boolean startOfData = false;

        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            reader = new BufferedReader(new FileReader(this.getClass()
                                  .getResource(testFile).getPath()));

            String lineRead = null;
            int i = 0;
            while ((lineRead = reader.readLine()) != null) {
                String lineToWrite = lineRead;
                if (startOfData) {
                    lineToWrite = "";
                    String [] components = lineRead.split(",");
                    for(int j = 0; j < components.length - 1; j++)
                        {
                            lineToWrite = lineToWrite + components[j] + ",";
                        }
                    lineToWrite = lineToWrite + classes.get(i++);
                }

                writer.write(lineToWrite);
                writer.newLine();

                if (lineRead.toLowerCase().startsWith("@data")) {
                    startOfData = true;
 }
            }

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ioe) {
                throw ioe;
            }
        }

    }

    /** Custom comparator to sort a map on value instead of key. **/
    private static final Comparator<Map.Entry<Integer, BigDecimal>> mapValueComp = new Comparator<Map.Entry<Integer, BigDecimal>>() {

        @Override
        public int compare(Entry<Integer, BigDecimal> o1,
                           Entry<Integer, BigDecimal> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    };

}
