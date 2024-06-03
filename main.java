import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JobScheduleVisualizer extends JFrame {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private JPanel fileSelectionPanel;
    private JPanel chartPanelContainer;
    private JTextField filePathField;
    private JButton backButton;

    public JobScheduleVisualizer() {
        super("Schedule Visualizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeUI();
    }

    private void initializeUI() {
        fileSelectionPanel = new JPanel();
        fileSelectionPanel.setLayout(new BoxLayout(fileSelectionPanel, BoxLayout.Y_AXIS));
        chartPanelContainer = new JPanel(new BorderLayout());

        JLabel instructionLabel = new JLabel("Please select the file data you want to visualize. Supported files: ODS, XLSX.");
        JLabel fileLabel = new JLabel("Choose a file: ");
        filePathField = new JTextField(30);
        JButton browseButton = new JButton("Browse");
        JButton loadButton = new JButton("Load");

        JLabel emailLabel = new JLabel("For further enhancements, contact: ganesh.m@valuelabs.com");

        JPanel fileInputPanel = new JPanel();
        fileInputPanel.add(fileLabel);
        fileInputPanel.add(filePathField);
        fileInputPanel.add(browseButton);

        fileSelectionPanel.add(instructionLabel);
        fileSelectionPanel.add(fileInputPanel);
        fileSelectionPanel.add(loadButton);
        fileSelectionPanel.add(Box.createVerticalStrut(20));  // Add space
        fileSelectionPanel.add(emailLabel);

        setLayout(new BorderLayout());
        add(fileSelectionPanel, BorderLayout.NORTH);

        // Add action listeners
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Spreadsheet Files", "ods", "xlsx"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        loadButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            String fileType = getFileExtension(filePath);
            IntervalCategoryDataset dataset = createDataset(filePath, fileType);
            if (dataset != null) {
                displayChart(dataset);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load data. Please check the file and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private String getFileExtension(String filePath) {
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    private void displayChart(IntervalCategoryDataset dataset) {
        // Create chart
        JFreeChart chart = ChartFactory.createGanttChart(
                "Job Schedule Visualizer", "Jobs", "Time", dataset, true, true, false);

        // Customize plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);
        DateAxis axis = (DateAxis) plot.getRangeAxis();
        axis.setDateFormatOverride(dateFormat);
        axis.setTickUnit(new DateTickUnit(DateTickUnitType.HOUR, 1));

        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1700, 1400));

        // Add chart panel to scroll pane
        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setPreferredSize(new java.awt.Dimension(800, 600));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Set custom increments for smooth scrolling
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(12);

        // Add smooth scrolling with MouseWheelListener
        scrollPane.addMouseWheelListener(e -> {
            if (e.isShiftDown()) {
                scrollPane.getHorizontalScrollBar().setValue(
                        scrollPane.getHorizontalScrollBar().getValue() + e.getWheelRotation() * scrollPane.getHorizontalScrollBar().getUnitIncrement());
            } else {
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getValue() + e.getWheelRotation() * scrollPane.getVerticalScrollBar().getUnitIncrement());
            }
        });

        // Add a back button
        backButton = new JButton("Back");
        backButton.addActionListener(this::goBack);

        chartPanelContainer.removeAll();
        chartPanelContainer.add(backButton, BorderLayout.NORTH);
        chartPanelContainer.add(scrollPane, BorderLayout.CENTER);

        // Update UI to display chart
        getContentPane().removeAll();
        add(chartPanelContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void goBack(ActionEvent e) {
        // Switch back to file selection panel
        getContentPane().removeAll();
        add(fileSelectionPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private IntervalCategoryDataset createDataset(String filePath, String fileType) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries series = new TaskSeries("Jobs");

        try {
            switch (fileType) {
                case "ods":
                    series = readODS(filePath);
                    break;
                case "xlsx":
                    series = readXLSX(filePath);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + fileType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        dataset.add(series);
        return dataset;
    }

    private TaskSeries readODS(String filePath) throws Exception {
        TaskSeries series = new TaskSeries("Jobs");
        try (SpreadsheetDocument document = SpreadsheetDocument.loadDocument(new File(filePath))) {
            Table table = document.getTableByName("Sheet1");

            for (int rowIndex = 1; rowIndex < table.getRowCount(); rowIndex++) {
                String jobName = table.getCellByPosition(0, rowIndex).getDisplayText();
                if (jobName == null || jobName.isEmpty()) continue;

                String startText = table.getCellByPosition(5, rowIndex).getDisplayText();
                String durationText = table.getCellByPosition(4, rowIndex).getDisplayText();
                if (startText == null || durationText == null || startText.isEmpty() || durationText.isEmpty()) continue;

                Date startTime = dateFormat.parse(startText);
                double timeTakenInMinutes = Double.parseDouble(durationText);

                Date endTime = new Date(startTime.getTime() + (long) (timeTakenInMinutes * 60 * 1000));
                Task task = new Task(jobName, startTime, endTime);
                series.add(task);
            }
        }
        return series;
    }

    private TaskSeries readXLSX(String filePath) throws IOException, InvalidFormatException, ParseException {
        TaskSeries series = new TaskSeries("Jobs");
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Cell jobCell = row.getCell(0);
                Cell startCell = row.getCell(5);
                Cell durationCell = row.getCell(4);

                if (jobCell == null || startCell == null || durationCell == null) continue;

                String jobName = jobCell.getStringCellValue();
                String startText = startCell.getStringCellValue();
                String durationText = durationCell.getStringCellValue();

                if (jobName.isEmpty() || startText.isEmpty() || durationText.isEmpty()) continue;

                Date startTime = dateFormat.parse(startText);
                double timeTakenInMinutes = Double.parseDouble(durationText);

                Date endTime = new Date(startTime.getTime() + (long) (timeTakenInMinutes * 60 * 1000));
                Task task = new Task(jobName, startTime, endTime);
                series.add(task);
            }
        }
        return series;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JobScheduleVisualizer visualizer = new JobScheduleVisualizer();
            visualizer.setVisible(true);
        });
    }
}
