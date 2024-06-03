# ScheduleVisualizer

## Overview
ScheduleVisualizer is a tool designed to visualize Jenkins job schedules and other related enhancements. It supports ODS and XLSX file formats for input schedules and displays the data in a graphical format using JFrame and JPanel. This application can be integrated into a Maven or Spring Boot project.

## Features
- **File Format Support**: Upload schedules in ODS or XLSX formats.
- **Graphical Visualization**: Uses JFrame to display the schedule data in a JPanel.
- **Easy Integration**: Can be run with Maven or as a Spring Boot application.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Maven
- Spring Boot (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/kumarganeshmagam/scheduleVisualizer.git
   cd scheduleVisualizer
   ```

2. **Build the project using Maven**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

### Usage
1. **Upload Schedule Files**: Upload your schedule files in ODS or XLSX format.
2. **Run the Main Class**: Execute the main class to visualize the schedule data.
3. **View in JFrame**: The schedule data will be displayed in a JPanel within a JFrame.

### Example Schedule Format
Ensure your schedule files are formatted as follows:

| Job          | Tag         | Env  | No of Plans | Time Taken | Start Time            | isActive |
|--------------|-------------|------|-------------|------------|-----------------------|----------|
| plan CDP     | WhiteTouch  | Prod | 9           | 4          | 17:30:00              | TRUE     |
| GDP Builder  | Touch       | Prod | 10          | 24         | 19:30:00              | TRUE     |
| CFP PhlatoOrg| JustTouch   | Prod | 7           | 17         | 08:00:00,17:00:00     | TRUE     |
| GCP Store    | Touch       | Prod | 4           | 7          | 07:45:00              | TRUE     |
| E@E Header AU| WhiteTouch  | Prod | 13          | 12         | 16:00:00,04:00:00     | FALSE    |

## Contributing
Contributions are welcome! Please read the [contributing guidelines](CONTRIBUTING.md) first.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact
For any inquiries or feedback, please reach out to [kumarganeshmagam@gmail.com].

---

This README provides a comprehensive overview of the ScheduleVisualizer project, its usage, and contribution guidelines. Feel free to customize it further to suit your project's needs.
