# Software Testing - Software Quiz Application

## Project Description 📖

This application is a software-related quiz that collects the user's first name, surname, and email. Once the information is provided, the user proceeds to the next page, where they answer ten specialized questions related to software. Each question is worth 2 points, and the quiz has a time limit. The scores are calculated and stored in a MySQL database. The results are displayed on the third page.


## Database 🛠

The project uses two tables in a MySQL database: **users** and **scores**.

### users Table 📋

This table stores user registration information.

| **Column**  | **Data Type** | **Description**                       |
|-------------|---------------|---------------------------------------|
| `id`        | `int(11)`     | Auto-incremented unique ID            |
| `name`      | `varchar(50)` | User's first name                     |
| `surname`   | `varchar(50)` | User's surname                        |
| `email`     | `varchar(100)`| User's email (must be unique)         |

### scores Table 📊

This table stores the quiz results of users.

| **Column**     | **Data Type**   | **Description**                       |
|----------------|-----------------|---------------------------------------|
| `id`           | `int(11)`       | Auto-incremented unique ID            |
| `name`         | `varchar(255)`  | User's first name                     |
| `surname`      | `varchar(255)`  | User's surname                        |
| `email`        | `varchar(255)`  | User's email (must be unique)         |
| `score`        | `int(11)`       | User's quiz score (calculated from answers) |
| `created_at`   | `timestamp`     | Timestamp of when the score was recorded |

## Database Setup 🧰

### Import the Database:

- Use the provided SQL dump file to create the database and tables in your MySQL server.
- The tables **users** and **scores** will be created with necessary columns, including primary keys and unique indexes for the **email** column.

### Database Connection Setup:

- Update the database connection details in the `server/db_config.php` file to match your MySQL server settings.

## Installation and Setup 🚀

### To Run the Android App:

1. Clone the project from GitHub.
2. Open the project files in Android Studio.
3. Connect your Android device and run the project.

### To Set Up the Server:

1. Install PHP and MySQL on your server.
2. Import the SQL dump provided to create the database and tables.
3. Update the database connection in `server/db_config.php`.
4. Upload the PHP scripts to your server.

## Usage 📲

- **Page 1**: Enter your name, surname, and email.
- **Page 2**: Answer the software-related questions (10 questions, each worth 2 points). Your score will be calculated and saved in the database.
- **Page 3**: View your name, surname, email, and score.

