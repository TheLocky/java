package modules;

import core.Module;
import core.Pack;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Scanner;

public class Input implements Module {
    private static String directory = Paths.get("").toAbsolutePath().toString();
    private JFrame frame;

    public Input(JFrame par) {
        frame = par;
    }

    @Override
    public Pack Request(Pack p) {
        Pack ans = new Pack();
        if (frame != null) {
            try {
                JFileChooser od = new JFileChooser();
                FileFilter all = od.getChoosableFileFilters()[0];
                od.removeChoosableFileFilter(all);
                od.addChoosableFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
                    }

                    @Override
                    public String getDescription() {
                        return "Text file (*.txt)";
                    }
                });
                int retVal = od.showOpenDialog(frame);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selFile = od.getSelectedFile();
                    Scanner file = new Scanner(new BufferedReader(new FileReader(selFile)));
                    if (file.hasNextInt()) {
                        int size = file.nextInt();
                        if (size > 0) {
                            double[][] points = new double[2][size];
                            Double xMin = Double.MAX_VALUE;
                            Double xMax = Double.MIN_VALUE;
                            Double yMin = Double.MAX_VALUE;
                            int i = 0;
                            while (file.hasNextDouble() && (i < size)) {
                                points[0][i] = file.nextDouble();
                                if (points[0][i] > xMax)
                                    xMax = points[0][i];
                                if (points[0][i] < xMin)
                                    xMin = points[0][i];
                                i++;
                            }
                            i = 0;
                            while (file.hasNextDouble() && (i < size)) {
                                points[1][i] = file.nextDouble();
                                if (points[1][i] < yMin)
                                    yMin = points[1][i];
                                i++;
                            }
                            ans.add("Points", points);
                            ans.add("xMax", xMax);
                            ans.add("xMin", xMin);
                            ans.add("yMin", yMin);
                            ans.add("fileName", selFile.getName());
                            return ans;
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Incorrect file format!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return ans;
    }
}
